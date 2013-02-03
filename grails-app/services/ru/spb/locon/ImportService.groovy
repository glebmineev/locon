package ru.spb.locon

import org.hibernate.SessionFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import ru.spb.locon.excel.CellHandler
import ru.spb.locon.excel.ExcelObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.zkoss.zk.ui.*
import ru.spb.locon.excel.ImportException
import ru.spb.locon.importer.*
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.ss.usermodel.*

class ImportService extends IImporterService implements ApplicationContextAware {

  static scope = "prototype"

  ApplicationContext applicationContext

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImportService.class)

  HSSFWorkbook workbook
  ManufacturerEntity manufacturer
  CategoryEntity menuCategory

  SaveUtils saveUtils = new SaveUtils()
  Map<String, Set<FilterEntity>> filtersCache = new HashMap<String, Set<FilterEntity>>()
  Map<String, Set<CategoryEntity>> categoryCache = new HashMap<String, Set<CategoryEntity>>()

  List<String> imageErrors = new ArrayList<String>()

  ImportService() {
    ImportComposer.class.metaClass.runAsync = { Runnable runme ->
      ApplicationHolder.getApplication().getMainContext().executorService.withPersistence(runme)
    }
  }

  void doImport(InputStream is) {

    //АНАЛИЗ ФАЙЛА.
    ImportEvent startProcessExcel = new ImportEvent("Анализ excel файла", "Анализ excel файла", "")
    startProcessExcel.state = ImportEvent.STATES.START
    sendSuccessfulEvent(startProcessExcel)

    ExcelObject excelObject = new ExcelObject(is)

    doProgress(25, "Идет анализ структуры данных файла...")

    ImportEvent endProcessExcel = new ImportEvent("Анализ excel файла", "Анализ excel файла", "")
    endProcessExcel.state = ImportEvent.STATES.SUCCESSFUL
    sendSuccessfulEvent(endProcessExcel)

    //СОХРАНЕНИЕ ФИЛЬТРОВ.
    ImportEvent createFilters = new ImportEvent("Сохранение фильтров", "Анализ фильтров", "")
    createFilters.state = ImportEvent.STATES.START
    sendSuccessfulEvent(createFilters)
    try {

      FilterEntity.withTransaction {
        processFilters(excelObject)
      }

      doProgress(50, "Идет анализ фильтров товаров...")

      ImportEvent endCreateFilters = new ImportEvent("Сохранение фильтров", "Анализ фильтров", "")
      endCreateFilters.state = ImportEvent.STATES.SUCCESSFUL
      sendSuccessfulEvent(endCreateFilters)

    } catch (ImportException ex) {
      ImportEvent errorEvent = new ImportEvent("Сохранение фильтров", "Анализ фильтров", "")
      errorEvent.state = ImportEvent.STATES.ERROR
      errorEvent.addErrorMessage(ex.message)
      sendErrorEvent(errorEvent)
    }

    //ПОСТРОЕНИЕ ДЕРЕВА КАТЕГОРИЙ.
    ImportEvent createTreeCategories = new ImportEvent("Построение дерева категорий", "Построение дерева категорий", "")
    createTreeCategories.state = ImportEvent.STATES.START
    sendSuccessfulEvent(createTreeCategories)
    try {

      CategoryEntity.withTransaction {
        processCategories(excelObject)
      }

      doProgress(75, "Идет построение дерева категорий...")

      ImportEvent endCreateTreeCategories = new ImportEvent("Построение дерева категорий", "Построение дерева категорий", "")
      endCreateTreeCategories.state = ImportEvent.STATES.SUCCESSFUL
      sendSuccessfulEvent(endCreateTreeCategories)

    } catch (ImportException ex) {
      ImportEvent errorEvent = new ImportEvent("Построение дерева категорий", "Построение дерева категорий", "")
      errorEvent.state = ImportEvent.STATES.ERROR
      errorEvent.addErrorMessage(ex.message)
      sendErrorEvent(errorEvent)
    }

    //ИМПОРТ ТОВАРОВ.
    ImportEvent startProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
    startProcess.state = ImportEvent.STATES.START
    sendSuccessfulEvent(startProcess)
    try {

      ProductEntity.withTransaction {
        processProducts(excelObject)
      }

      doProgress(100, "Идет загрузка товаров...")

      if (imageErrors.size() > 0) {
        ImportEvent errorEvent = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
        errorEvent.state = ImportEvent.STATES.ERROR
        errorEvent.addAllErrors(imageErrors)
        sendErrorEvent(errorEvent)
      }
      else
      {
        ImportEvent successfulProcess = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
        successfulProcess.state = ImportEvent.STATES.SUCCESSFUL
        sendSuccessfulEvent(successfulProcess)
      }

    } catch (ImportException ex) {
      ImportEvent errorEvent = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
      errorEvent.state = ImportEvent.STATES.ERROR
      errorEvent.addErrorMessage(ex.message)
      sendErrorEvent(errorEvent)
    }

    complete("Импорт завершен!")

  }

  /**
   * Обработка категорий.
   * @param excelObject - объект содержащий данные из excel.
   */
  void processCategories(ExcelObject excelObject) {
    String sheetName = ""
    int rowNumber = 0
    try {

      excelObject.each { String sheet, List<CellHandler> handlers ->

        sheetName = sheet

        //получаем корневую категорию из названия листа excel файла.
        CategoryEntity submenuCategory = saveUtils.getCategory(sheet, menuCategory, filtersCache.get(sheet))
        log.debug("сохранена категория: ${sheet}")

        Set<CategoryEntity> categories = new HashSet<CategoryEntity>()
        handlers.each { it ->
          rowNumber = it.rowNumber
          String categoryName = it.data.get("I") as String
          if (categoryName != null) {
            categories.add(saveUtils.getCategory(categoryName, submenuCategory, filtersCache.get(sheet)))
            log.debug("сохранена категория: ${categoryName}")
          }

        }

        categoryCache.put(sheetName, categories)

      }
    } catch (ImportException ex) {
      log.debug("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
      throw new ImportException("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
    }
  }

  /**
   * Обработка фильтров.
   * @param excelObject - объект содержащий данные из excel.
   */
  void processFilters(ExcelObject excelObject) {

    String sheetName = ""
    int rowNumber = 0

    FilterGroupEntity manufacturerGroup = saveUtils.manufacturerGroup
    FilterEntity manufacturerFilter = saveUtils.getFilter(manufacturer.name, manufacturerGroup)
    FilterGroupEntity usageGroup = saveUtils.usageGroup

    try {

      saveUtils.getFilter(manufacturer.name, manufacturerGroup)

      excelObject.each { String sheet, List<CellHandler> handlers ->

        sheetName = sheet
        Set<FilterEntity> filters = new HashSet<FilterEntity>()

        handlers.each { it ->
          rowNumber = it.rowNumber
          String filterName = it.data.get("C") as String
          if (filterName != null) {
            filters.add(saveUtils.getFilter(filterName, usageGroup))
            log.debug("сохранена категория: ${filterName}")
          }

        }
        filters.add(manufacturerFilter)
        filtersCache.put(sheetName, filters)

      }
    } catch (ImportException ex) {
      log.debug("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
      throw new ImportException("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
    }

  }

  /**
   * Загрузка товаров.
   * @param excelObject - объект содержащий данные из excel.
   */
  void processProducts(ExcelObject excelObject) {

    int sessionCleaner = 0

    String sheetName = ""
    int rowNumber = 0

    excelObject.each { String sheet, List<CellHandler> handlers ->

      sheetName = sheet

      handlers.each { it ->

        rowNumber = it.rowNumber

        ProductEntity product = createProduct(it)

        String categoryName = it.getData().get("I") as String
        String imagePath = "${menuCategory}/${manufacturer}/${sheetName}${categoryName != null ? "/${categoryName}" : ""}"

        String to = "${imagePath}/${product?.article}_${product.name}"
        product.setImagePath(to)

        //TODO: включить в категории подменю.
        categoryCache.get(sheetName).each {CategoryEntity category ->
          if (category.name.equals(categoryName))
            product.addToCategories(category)
        }

        //TODO: включить в фильтр производителя.
        String filterName = it.data.get("C") as String
        filtersCache.get(sheetName).each {FilterEntity filter ->
          if (filter.name.equals(filterName))
            product.addToFilters(filter)
        }

        if (product.validate()){
          product.save(flush: true)
          log.debug("товара ${product.name} сохранен.")
        } else {
          log.debug("ошибка сохраненения товара страница: ${sheetName} строка: ${rowNumber}")
          throw new ImportException("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
        }

        String from = it.getData().get("F") as String

        //загружем изображение товара.
        boolean isDownloaded = imageService.downloadImages(from, to)
        if (!isDownloaded){
          log.debug("ошибка загрузке изображения товара страница: ${sheetName} строка: ${rowNumber}")
          imageErrors.add("ошибка загрузке изображения товара страница: ${sheetName} строка: ${rowNumber}")
        }

        sessionCleaner++
        if (sessionCleaner > 10) {
          cleanUpGorm()
          sessionCleaner = 0
        }

      }

    }

  }

  ProductEntity createProduct(CellHandler cellHandler) {

    ProductEntity product = new ProductEntity(
        article: cellHandler.data.get("A") as String,
        name: cellHandler.data.get("B") as String,
        volume: cellHandler.data.get("D") as String,
        price: cellHandler.data.get("E") as Float,
        description: cellHandler.data.get("G") as String,
        usage: cellHandler.data.get("H") as String,
        manufacturer: manufacturer
    )

    return product

  }

  void setManufacturer(String manufacturerName) {
    this.manufacturer = saveUtils.getManufacturer(manufacturerName)
  }

  void setMenuCategory(String menuCategoryName) {
    this.menuCategory = saveUtils.saveCategory(menuCategoryName)
  }

}
