package ru.spb.locon

import com.google.common.base.Strings
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import ru.spb.locon.excel.CellHandler
import ru.spb.locon.excel.ExcelObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ru.spb.locon.zulModels.importer.ImportComposer
import ru.spb.locon.excel.ImportException
import ru.spb.locon.importer.ConverterRU_EN
import ru.spb.locon.importer.IImporterService
import ru.spb.locon.importer.SaveUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.zulModels.importer.ImportEvent

class ImportService extends IImporterService implements ApplicationContextAware {

  static scope = "prototype"

  ApplicationContext applicationContext

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImportService.class)

  ManufacturerEntity manufacturer
  CategoryEntity menuCategory

  SaveUtils saveUtils = new SaveUtils()
  Map<String, Set<Long>> filtersCache = new HashMap<String, Set<Long>>()
  Map<String, Set<Long>> categoryCache = new HashMap<String, Set<Long>>()

  List<String> imageErrors = new ArrayList<String>()

  ImportService() {
    ImportComposer.class.metaClass.runAsync = { Runnable runme ->
      ApplicationHolder.getApplication().getMainContext().executorService.withPersistence(runme)
    }
  }

  void doImport(InputStream is) {

    doProgress(0, "Идет анализ структуры данных файла...")

    //АНАЛИЗ ФАЙЛА.
    ImportEvent startProcessExcel = new ImportEvent("Анализ excel файла", "Анализ excel файла", "")
    startProcessExcel.state = ImportEvent.STATES.START
    sendSuccessfulEvent(startProcessExcel)

    ExcelObject excelObject = new ExcelObject(is)

    doProgress(25, "Идет анализ фильтров товаров...")

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
        cleanUpGorm()
      }

      doProgress(50, "Идет построение дерева категорий...")

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
        cleanUpGorm()
      }

      doProgress(75, "Идет загрузка товаров...")

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
        cleanUpGorm()
      }

      doProgress(100, "Идет загрузка товаров...")

      if (imageErrors.size() > 0) {
        ImportEvent errorEvent = new ImportEvent("Импорт товаров начат", "Импорт товаров", "")
        errorEvent.state = ImportEvent.STATES.ERROR
        errorEvent.addAllErrors(imageErrors)
        sendErrorEvent(errorEvent)
      } else {
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

    categoryCache.clear()
    filtersCache.clear()
    initService.refreshShop()
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
        CategoryEntity submenuCategory = saveUtils.saveCategory(sheet, menuCategory)
        log.debug("сохранена категория: ${sheet}")

        Set<CategoryEntity> categories = new HashSet<CategoryEntity>()
        handlers.each { it ->
          rowNumber = it.rowNumber
          String categoryName = it.data.get("I") as String
          String filterName = it.data.get("C") as String
          if (!Strings.isNullOrEmpty(categoryName) && !categoryName.equals(sheetName)) {
            try {
              //if (!categories.name.contains(categoryName)) {
              CategoryEntity category = saveUtils.saveCategory(categoryName, submenuCategory, FilterEntity.findByName(filterName))
              categories.add(CategoryEntity.get(category.id))
              log.debug("сохранена категория: ${categoryName}")
              //}
            } catch (Throwable tr) {
              log.debug("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
              throw new ImportException("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
            }

          }

        }

        //TODO: если категорий нет прикрепляем товары к самой верхней.
        if (categories.size() == 0)
          filtersCache.get(sheet).each { Long filterID ->
            submenuCategory.addToFilters(FilterEntity.get(filterID))
            submenuCategory.save(flush: true)
          }

        categoryCache.put(sheetName, categories.id as Set<Long>)

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

    //FilterGroupEntity manufacturerGroup = saveUtils.manufacturerGroup
    //FilterEntity manufacturerFilter = saveUtils.saveFilter(manufacturer.name, manufacturerGroup)
    FilterGroupEntity usageGroup = saveUtils.usageGroup

    try {


      excelObject.each { String sheet, List<CellHandler> handlers ->

        sheetName = sheet
        Set<FilterEntity> filters = new HashSet<FilterEntity>()

        handlers.each { it ->
          rowNumber = it.rowNumber
          String filterName = it.data.get("C") as String
          if (filterName != null) {
            try {
              if (!filters.name.contains(filterName)) {
                FilterEntity filter = saveUtils.saveFilter(filterName, usageGroup)
                filters.add(FilterEntity.get(filter.id))
                log.debug("сохранен фильтр: ${filterName}")
              }
            } catch (Throwable ex) {
              log.debug("ошибка сохраненения фильтра страница: ${sheetName} строка: ${rowNumber}")
              throw new ImportException("ошибка сохраненения фильтра страница: ${sheetName} строка: ${rowNumber}")
            }
          }

        }
        //filters.add(manufacturerFilter)
        filtersCache.put(sheetName, filters.id as Set<Long>)

      }
    } catch (ImportException ex) {
      log.debug("ошибка сохраненения фильтра страница: ${sheetName} строка: ${rowNumber}")
      throw new ImportException("ошибка сохраненения фильтра страница: ${sheetName} строка: ${rowNumber}")
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
        try {
          rowNumber = it.rowNumber

          ProductEntity product = getProduct(it)

          String categoryName = it.getData().get("I") as String
          String imagePath = "${menuCategory}/${manufacturer}/${sheetName}${categoryName != null ? "/${categoryName}" : ""}"

          String to = "${imagePath}/${product?.article}_${product.name}"
          //product.setImagePath(to)
          String translited = ConverterRU_EN.translit(to)
          product.setEngImagePath(translited)

          /*categoryCache.get(sheetName).each { CategoryEntity category ->
            if (category.name.equals(categoryName))
              product.addToCategories(CategoryEntity.get(category.id))
          }*/
          if (!Strings.isNullOrEmpty(categoryName) && !categoryName.equals(sheetName))
            product.setCategory(CategoryEntity.findByName(categoryName))
          else
            product.setCategory(CategoryEntity.findByName(sheet))


          String filterName = it.data.get("C") as String
          filtersCache.get(sheetName).each { Long filterID ->
            FilterEntity filter = FilterEntity.get(filterID)
            if (filter.name.equals(filterName))
              product.setFilter(filter)//addToFilters(FilterEntity.get(filter.id))
            //if (filter.name.equals(manufacturer.name))
            //  product.addToFilters(FilterEntity.get(filter.id))
          }

          if (product.validate()) {
            try {
              product.save(flush: true)
              log.debug("товара ${product.name} сохранен.")
            } catch (Throwable tr) {
              log.debug("ошибка сохраненения товара страница: ${sheetName} строка: ${rowNumber}")
              throw new ImportException("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
            }
          } else {
            log.debug("ошибка сохраненения товара страница: ${sheetName} строка: ${rowNumber}")
            throw new ImportException("ошибка сохраненения категории страница: ${sheetName} строка: ${rowNumber}")
          }

          String from = it.getData().get("F") as String

          //загружем изображение товара.
          boolean isDownloaded = imageService.downloadImages(from,
              translited)
          if (!isDownloaded) {
            log.debug("ошибка загрузке изображения товара страница: ${sheetName} строка: ${rowNumber}")
            imageErrors.add("ошибка загрузке изображения товара страница: ${sheetName} строка: ${rowNumber}")
          }

          sessionCleaner++
          if (sessionCleaner > 10) {
            cleanUpGorm()
            sessionCleaner = 0
          }
        } catch (Exception e) {
          new ImportException("Ошибка заполнения товара с листа: ${sheet} строки ${it.rowNumber}")
        }
      }

    }

  }

  ProductEntity getProduct(CellHandler cellHandler) {
    ProductEntity product
    try {
      product = ProductEntity.findByNameAndArticle(cellHandler.data.get("B") as String, cellHandler.data.get("A") as String)
      if (product == null)
        product = ProductEntity.newInstance()
      if (cellHandler.validate())
        product = new ProductEntity(
            article: cellHandler.data.get("A") as String,
            name: cellHandler.data.get("B") as String,
            volume: cellHandler.data.get("D") as String,
            price: Math.round(cellHandler.data.get("E") as Float),
            description: cellHandler.data.get("G") as String,
            usage: cellHandler.data.get("H") as String,
            manufacturer: manufacturer
        )
      else
        throw new ImportException()
    } catch (Exception ex) {
      log.error(ex)
    }

    return product

  }

  void setManufacturer(String manufacturerName) {
    this.manufacturer = saveUtils.getManufacturer(manufacturerName)
  }

  void setMenuCategory(String menuCategoryName) {
    this.menuCategory = saveUtils.saveCategory(menuCategoryName)
  }

}
