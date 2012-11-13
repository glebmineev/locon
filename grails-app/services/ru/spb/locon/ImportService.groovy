package ru.spb.locon

import jxl.Workbook
import jxl.Sheet
import jxl.Cell
import ru.spb.locon.importer.ConvertUtils
import java.math.RoundingMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.EventQueues
import ru.spb.locon.importer.ImportEvent
import ru.spb.locon.importer.DownloadUtils
import ru.spb.locon.importer.SaveUtils

class ImportService {

  static scope = "prototype"

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImportService.class)

  String catalogPath = ConfigurationHolder.config.locon.store.catalog
  String applicationPath
  Desktop desktop

  Workbook wrkbook = null
  ManufacturerEntity manufacturer
  CategoryEntity menuCategory

  DownloadUtils downloadUtils = new DownloadUtils()
  SaveUtils saveUtils = new SaveUtils()
  
  List<ProductFilterEntity> productFiltersTemp = new ArrayList<ProductFilterEntity>()

  public void doImport() {

    ImportEvent startProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
    startProcess.state = ImportEvent.STATES.START
    sendEvent(startProcess)

    CategoryEntity.withTransaction {
      int yuuu=0
      Sheet[] sheets = wrkbook.getSheets()

      sheets.each {Sheet sheet ->
        //получаем корневую категорию.
        CategoryEntity submenuCategory = saveUtils.getCategory(sheet.getName())
        submenuCategory.setParentCategory(menuCategory)

        //перебираем строки страницы.
        CategoryEntity temp = null
        (0..sheet.getRows() - 1).each { i ->
          int yyy = 0
          Cell[] row = sheet.getRow(i)
          if (row.length != 0) {
            yyy = 0
            if (row != null && row.length > 1 && !row[1].getContents().isEmpty()) {
              yyy = 0
              //Создаем продукт.
              ProductEntity product = createProduct(row,
                  "${menuCategory}/${manufacturer}/${submenuCategory}/${temp != null ? temp : ""}")
              //связываем со всеми категорими в которые он входит для дальнейшей фильтрации.
              saveUtils.linkToCategories(submenuCategory, product)
              saveUtils.linkToCategories(temp, product)
              saveUtils.linkToCategories(menuCategory, product)
              product.setManufacturer(manufacturer)
              product.save()

              if (productFiltersTemp.size() > 0) {

                productFiltersTemp.each {ProductFilterEntity productFilter ->
                  saveUtils.saveFilterCategoryLink(submenuCategory, productFilter)
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


  }

  private ProductEntity createProduct(Cell[] row, String imagePath) {
    ProductEntity product = new ProductEntity()
    String id = ""
    (0..row.length - 1).each { i ->
      int r = 0
      String value = row[i].getContents()

      if (!value.isEmpty()) {

        if (i == 0)
          product.setArticle(value)
        if (i == 1)
          product.setName(value)
        if (i == 2){

          ProductFilterGroupEntity manufacturerGroup = saveUtils.getProductFilterGroup("Производитель")
          ProductFilterGroupEntity usageGroup = saveUtils.getProductFilterGroup("Применение")

          ProductFilterEntity manufacturerFilter = saveUtils.getProductFilter(manufacturer.name, manufacturerGroup)
          ProductFilterEntity usageFilter = saveUtils.getProductFilter(value, usageGroup)

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
          File dir = new File("${catalogPath}/${to}")
          if (!dir.exists())
            dir.mkdirs()

          //загружем
          boolean isDownloaded = downloadUtils.downloadImages(value, "${catalogPath}/${to}")

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

          product.setImagePath("${to}")

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
          File dir = new File("${catalogPath}/${to}")
          if (!dir.exists())
            dir.mkdirs()

          ImportEvent importEvent = new ImportEvent(id, name, article)
          importEvent.state = ImportEvent.STATES.ERROR
          importEvent.addError("Не удалось загрузить картинку")
          sendEvent(importEvent)

          product.setImagePath("${to}")
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
