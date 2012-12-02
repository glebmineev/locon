package ru.spb.locon

import jxl.Workbook
import jxl.Sheet
import jxl.Cell
import ru.spb.locon.importer.ConvertUtils
import java.math.RoundingMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.EventQueues
import ru.spb.locon.importer.ImportEvent
import ru.spb.locon.importer.SaveUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.common.StringUtils

class ImportService {

  static scope = "prototype"

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImportService.class)

  String catalogPath
  Desktop desktop

  Workbook wrkbook = null
  ManufacturerEntity manufacturer
  CategoryEntity menuCategory

  SaveUtils saveUtils = new SaveUtils()
  
  List<FilterEntity> productFiltersTemp = new ArrayList<FilterEntity>()

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService")

  public void doImport() {
    try{
    ImportEvent startProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
    startProcess.state = ImportEvent.STATES.START
    sendEvent(startProcess)

    CategoryEntity.withTransaction {
      Sheet[] sheets = wrkbook.getSheets()

      sheets.each {Sheet sheet ->
        //получаем корневую категорию.
        CategoryEntity submenuCategory = saveUtils.getCategory(sheet.getName())
        submenuCategory.setParentCategory(menuCategory)

        //перебираем строки страницы.
        CategoryEntity temp = null
        (0..sheet.getRows() - 1).each { i ->
          Cell[] row = sheet.getRow(i)
          if (row.length != 0) {
            if (row != null && row.length > 1 && !row[1].getContents().isEmpty()) {
              //Создаем продукт.
              ProductEntity product = createProduct(row,
                  "${menuCategory}/${manufacturer}/${submenuCategory}${temp != null ? "/${temp}" : ""}")
              //связываем со всеми категорими в которые он входит для дальнейшей фильтрации.
              if (submenuCategory != null)
                saveUtils.linkToCategories(submenuCategory, product)
              if (temp != null)
                saveUtils.linkToCategories(temp, product)
              if (menuCategory != null)
                saveUtils.linkToCategories(menuCategory, product)
              product.setManufacturer(manufacturer)
              product.save()

              if (productFiltersTemp.size() > 0) {

                productFiltersTemp.each {FilterEntity productFilter ->
                  if (submenuCategory != null)
                    saveUtils.saveFilterCategoryLink(submenuCategory, productFilter)
                  if (temp != null)
                    saveUtils.saveFilterCategoryLink(temp, productFilter)
                  saveUtils.saveFilterToProductLink(product, productFilter)
                }

                productFiltersTemp.clear()

              }

            }
            else if (row[0] != null && !row[0].getContents().isEmpty()) {
              String cName = row[0].getContents()
              temp = saveUtils.getCategory(ConvertUtils.formatString(cName))
              temp.setParentCategory(submenuCategory)
            }

          }
        }

      }
    }

    ImportEvent successfulProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
    successfulProcess.state = ImportEvent.STATES.SUCCESSFUL
    sendEvent(successfulProcess)

    } catch (Exception e) {
      log.error(e.getMessage())
    }
  }

  private ProductEntity createProduct(Cell[] row, String imagePath) {
    ProductEntity product = new ProductEntity()
    String id = ""
    (0 .. row.length - 1).each { i ->
      int r = 0
      String value = row[i].getContents()

      if (!value.isEmpty()) {

        if (i == 0)
          product.setArticle(value)
        if (i == 1)
          product.setName(value)
        if (i == 2){

          FilterGroupEntity manufacturerGroup = saveUtils.getProductFilterGroup("Производитель")
          FilterGroupEntity usageGroup = saveUtils.getProductFilterGroup("Применение")

          FilterEntity manufacturerFilter = saveUtils.getProductFilter(manufacturer.name, manufacturerGroup)
          FilterEntity usageFilter = saveUtils.getProductFilter(value, usageGroup)

          productFiltersTemp.add(manufacturerFilter)
          productFiltersTemp.add(usageFilter)
        }

        if (i == 3)
          product.setVolume(value)

        if (i == 4) {
          float price = Float.parseFloat(value.replace(",", "."))
          float result = new BigDecimal(price).setScale(2, RoundingMode.UP).floatValue()
          product.setPrice(result)

          String article = row[0].getContents()
          String name = row[1].getContents()
          id = UUID.randomUUID().toString().replaceAll("-", "")


          ImportEvent startProcess = new ImportEvent(id, name, article)
          startProcess.state = ImportEvent.STATES.START
          sendEvent(startProcess)

        }

        if (i == 5) {
          String article = row[0].getContents()
          String name = row[1].getContents()

          String to = "${imagePath}/${product.article}_${product.name}"
          product.setImagePath("${to}")

          //загружем
          boolean isDownloaded = imageService.downloadImages(value, to)
          imageService.syncWithServer(product)
          if (isDownloaded) {
            ImportEvent importEvent = new ImportEvent(id, name, article)
            importEvent.state = ImportEvent.STATES.SUCCESSFUL
            sendEvent(importEvent)
          } else {
            ImportEvent importEvent = new ImportEvent(id, name, article)
            importEvent.state = ImportEvent.STATES.ERROR
            importEvent.addError("Не удалось загрузить картинку")
            sendEvent(importEvent)
          }

        }

        if (i == 6)
          product.setDescription(value)

        if (i == 7)
          product.setUsage(value)

      } else {

        if (i == 5) {
          String article = row[0].getContents()
          String name = row[1].getContents()

          String to = "${imagePath}/${product.article}_${product.name}"

          String root = ApplicationHolder.application.mainContext.servletContext.getRealPath("/")
          StringUtils stringUtils = new StringUtils()
          String store = "${stringUtils.buildPath(2, root)}\\productImages"

          File dir = new File("${store}/${to}")
          if (!dir.exists())
            dir.mkdirs()

          ImportEvent importEvent = new ImportEvent(id, name, article)
          importEvent.state = ImportEvent.STATES.ERROR
          importEvent.addError("Не удалось загрузить картинку")
          sendEvent(importEvent)

          product.setImagePath("${to}")
          imageService.syncWithServer(product)
        }
      }

    }

    return product
  }

  //отсыл события композеру ImportComposer.
  private void sendEvent(ImportEvent event) {
    Executions.activate(desktop)
    EventQueues.lookup("catalogImportQueue").publish(event)
    Executions.deactivate(desktop)
  }

  void setManufacturer(String manufacturerName) {
    this.manufacturer = saveUtils.getManufacturer(manufacturerName)
  }

  void setMenuCategory(String menuCategoryName) {
    this.menuCategory = saveUtils.getCategory(menuCategoryName)
  }

}
