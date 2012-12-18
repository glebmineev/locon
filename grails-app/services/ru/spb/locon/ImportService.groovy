package ru.spb.locon

import java.math.RoundingMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.zkoss.zk.ui.*
import org.zkoss.zk.ui.event.EventQueues
import ru.spb.locon.importer.*
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.common.StringUtils
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.ss.usermodel.*

class ImportService {

  static scope = "prototype"

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImportService.class)
  Desktop desktop

  HSSFWorkbook workbook
  ManufacturerEntity manufacturer
  CategoryEntity menuCategory

  SaveUtils saveUtils = new SaveUtils()

  List<FilterEntity> productFiltersTemp = new ArrayList<FilterEntity>()

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService")

  public void doImport() {
    try {
      ImportEvent startProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
      startProcess.state = ImportEvent.STATES.START
      sendEvent(startProcess)

      CategoryEntity.withTransaction {

        (0..workbook.getNumberOfSheets()-1).each { sheetIndex ->
          HSSFSheet sheet = workbook.getSheetAt(sheetIndex)

          if (!"Списки".equals(sheet.getSheetName())) {

            //получаем корневую категорию из названия листа excel файла.
            CategoryEntity submenuCategory = saveUtils.saveCategory(sheet.getSheetName())
            submenuCategory.setParentCategory(menuCategory)
            submenuCategory.save()

            Iterator<Row> rowIterator = sheet.rowIterator()
            while (rowIterator.hasNext()) {
              Row row = rowIterator.next()

              //Посылаем событие о начале импорта.
              String id = UUID.randomUUID().toString().replaceAll("-", "")
              ImportEvent startProductImport = new ImportEvent(id, getStringCellContent(row.getCell(1)), getStringCellContent(row.getCell(0)))
              startProductImport.state = ImportEvent.STATES.START
              sendEvent(startProductImport)

              String categoryName = ""
              if (row.getCell(8) != null)
                categoryName = getStringCellContent(row.getCell(8))

              //Создаем товар и заполняем часть полей.
              ProductEntity product = createProduct(row)
              String imagePath = "${menuCategory}/${manufacturer}/${submenuCategory}${categoryName != null ? "/${categoryName}" : ""}"

              String from = getStringCellContent(row.getCell(5))
              String to = "${imagePath}/${product?.article}_${product.name}"
              product.setImagePath(to)

              if (product.validate()) {
                ProductEntity savedProduct = product.save()
                //загружем изображение товара.
                boolean  isDownloaded = imageService.downloadImages(from, to)
                if (!isDownloaded) {
                  ImportEvent importEvent = new ImportEvent(id, getStringCellContent(row.getCell(1)), getStringCellContent(row.getCell(0)))
                  importEvent.state = ImportEvent.STATES.ERROR
                  importEvent.addErrorMessage("Не удалось загрузить картинку.")
                  sendEvent(importEvent)
                }
                //создаем на сервере папку с картинками разного размера.
                imageService.syncWithServer(savedProduct)

                CategoryEntity category = null
                //получаем корневую категорию.
                if (!categoryName.isEmpty()) {
                  category = saveUtils.saveCategory(getStringCellContent(row.getCell(8)))
                  category.setParentCategory(submenuCategory)
                  saveUtils.linkToCategories(category, savedProduct)
                }

                //Связываем категоии с товаром.
                saveUtils.linkToCategories(menuCategory, savedProduct)
                saveUtils.linkToCategories(submenuCategory, savedProduct)

                //Связываем фильтры с товаром.
                if (productFiltersTemp.size() > 0) {

                  productFiltersTemp.each {FilterEntity productFilter ->
                    if (submenuCategory != null)
                      saveUtils.saveFilterCategoryLink(submenuCategory, productFilter)
                    if (category != null)
                      saveUtils.saveFilterCategoryLink(category, productFilter)
                  }

                  productFiltersTemp.clear()
                }

                //Если картинка загрузилать и товар сохранен, то посылаем событие об успехе.
                if (isDownloaded){
                  ImportEvent endProductImport = new ImportEvent(id, getStringCellContent(row.getCell(1)), getStringCellContent(row.getCell(0)))
                  endProductImport.state = ImportEvent.STATES.SUCCESSFUL
                  sendEvent(endProductImport)
                }

              }
              else {
                ImportEvent importEvent = new ImportEvent(id, getStringCellContent(row.getCell(1)), getStringCellContent(row.getCell(0)))
                importEvent.state = ImportEvent.STATES.ERROR
                importEvent.addErrorMessage("Не удалось сохранить товар.")
                sendEvent(importEvent)
              }

            }

          }
        }

      }

      ImportEvent successfulProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
      successfulProcess.state = ImportEvent.STATES.SUCCESSFUL
      sendEvent(successfulProcess)

    }

    catch (Exception e) {
      log.error(e.message)
    }
  }

  ProductEntity createProduct(Row row) {

    ProductEntity product = new ProductEntity(
        article: getStringCellContent(row.getCell(0)),
        name: getStringCellContent(row.getCell(1)),
        volume: getStringCellContent(row.getCell(3)),
        price: getLongCellContent(row.getCell(4)),
        description: getStringCellContent(row.getCell(6)),
        usage: getStringCellContent(row.getCell(7)),
        manufacturer: manufacturer
    )

    String filterName = getStringCellContent(row.getCell(2))

    if (!filterName.isEmpty()) {
      FilterGroupEntity manufacturerGroup = saveUtils.getProductFilterGroup("Производитель")
      FilterGroupEntity usageGroup = saveUtils.getProductFilterGroup("Применение")

      FilterEntity manufacturerFilter = saveUtils.getProductFilter(manufacturer.name, manufacturerGroup)
      FilterEntity usageFilter = saveUtils.getProductFilter(filterName, usageGroup)

      productFiltersTemp.add(manufacturerFilter)
      productFiltersTemp.add(usageFilter)

      product.addToFilters(manufacturerFilter)
      product.addToFilters(usageFilter)

    }

    return product

  }

  Long getLongCellContent(Cell cell) {
    Long result = 0
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
      result = cell.getNumericCellValue()

    if (cell.getCellType() == Cell.CELL_TYPE_STRING &&
        cell.getStringCellValue() != null)
      result = Long.parseLong(cell.getStringCellValue())
    return result
  }

  String getStringCellContent(Cell cell) {
    String result = ""
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
      result = cell.getNumericCellValue().toString().replace(".0", "")

    if (cell.getCellType() == Cell.CELL_TYPE_STRING)
      result = cell.getStringCellValue()
    return result
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
    this.menuCategory = saveUtils.saveCategory(menuCategoryName)
  }

}
