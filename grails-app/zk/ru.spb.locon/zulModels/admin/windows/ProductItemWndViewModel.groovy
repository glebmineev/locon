package ru.spb.locon.zulModels.admin.windows

import org.apache.commons.io.FileUtils
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.*
import org.zkoss.zk.ui.event.*
import org.zkoss.zul.*
import ru.spb.locon.*
import ru.spb.locon.common.CategoryPathHandler
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.importer.ConverterRU_EN
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

/**
 * Модель формы редактирования товара.
 */
@Init(superclass=true)
class ProductItemWndViewModel extends DownloadImageViewModel {

  Long categoryID
  Long productID

  //Комбобоксы
  ListModelList<FilterEntity> filterModel
  ListModelList<ManufacturerEntity> manufacturerModel

  ManufacturerEntity selectedManufacturer
  FilterEntity selectedFilter

  //Простые поля.
  String article
  String name
  String description
  String price
  String volume

  //Отображение компонентов в случае если диалоговое окно
  boolean isModal

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.PRODUCT_NAME.getName()
    targetImage = "targetImage"
    std_image_size = STD_IMAGE_SIZES.MIDDLE.getSize()
  }

  @Override
  void initialize() {
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>

    if (arg.size() > 0) {
      categoryID = arg.get("category") as Long
      productID = arg.get("product") as Long
      isModal = true
    } else {
      Map<String, String[]> map = Executions.getCurrent().getParameterMap()
      categoryID = map.get("category")[0] as Long
      productID = map.get("product")[0] as Long
      isModal = false
    }

    if (productID != null)
      initItem(ProductEntity.get(productID))

    initComboboxes()
  }

  public void initComboboxes() {
    List<FilterEntity> filters = new ArrayList<FilterEntity>()
    collectAllFilters(CategoryEntity.get(categoryID), filters)
    filterModel = new ListModelList<FilterEntity>(filters)
    manufacturerModel = new ListModelList<ManufacturerEntity>(ManufacturerEntity.list(sort: "name"))
  }

  void collectAllFilters(CategoryEntity category, List<FilterEntity> filters) {
    List<CategoryEntity> categories = category.listCategory as List<CategoryEntity>
    if (categories != null && categories.size() > 0)
      categories.each { CategoryEntity it ->
        if (it.listCategory != null && it.listCategory.size() > 0)
          collectAllFilters(it, filters)
        else
          filters.addAll(it.filters as List<FilterEntity>)
      }
    else
      filters.addAll(category.filters as List<FilterEntity>)
  }

  public void initItem(ProductEntity product) {
    article = product.article
    name = product.name
    description = product.description
    price = product.price
    volume = product.volume

    selectedManufacturer = product.manufacturer
    selectedFilter = product.filter
  }

/*  *//**
   * Загрузка изображения товара в темповую дерикторию сервера.
   * @param event
   *//*
  @Command
  public void uploadImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    UploadEvent uploadEvent = event as UploadEvent
    Image image = event.getTarget().getSpaceOwner().getFellow("targetImage") as Image
    AImage media = uploadEvent.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]

    uuid = imageService.saveImageInTemp(media.getStreamData(), STD_FILE_NAMES.PRODUCT_NAME.getName(), ext)
    ImageUtils.tripleResizeImage(uuid, STD_FILE_NAMES.PRODUCT_NAME.getName(), ext)
    image.setContent(new AImage(new PathBuilder()
        .appendPath(serverFoldersService.temp)
        .appendPath(uuid)
        .appendString("${STD_FILE_NAMES.PRODUCT_NAME.getName()}-${STD_IMAGE_SIZES.MIDDLE.getSize()}")
        .appendExt(ext).build()))

  }*/

  @Command
  public void saveItem() {
    ProductEntity product = ProductEntity.get(productID)
    if (product == null)
      saveProduct(new ProductEntity())
    else
      updateProduct(product)

    Executions.sendRedirect("/admin/editor")
  }

  /**
   * Сохранение продукта.
   * @param product
   */
  public void saveProduct(ProductEntity product) {
    ProductEntity.withTransaction {
      product.setCategory(CategoryEntity.get(categoryID))
      product.setName(name)
      product.setArticle(article)
      product.setManufacturer(selectedManufacturer)
      product.setDescription(description)
      product.setFilter(selectedFilter)
      //если валидно копируемфайл картинки из темповой дериктории.
      if (product.validate()) {
        File src = new File(new PathBuilder()
            .appendPath(serverFoldersService.temp)
            .appendString(uuid).build())

        String dirPath = generateProductImagePath()
        String filePath = new PathBuilder()
                              .appendPath(dirPath)
                              .appendString("${article}_${name}")
                              .build()
        String translit = ConverterRU_EN.translit("${filePath}")

        product.setImagePath(filePath)
        product.setEngImagePath(translit)

        product.save(flush: true)

        if (uuid != null)
          FileUtils.copyDirectory(src, new File(new PathBuilder()
                                                      .appendPath(serverFoldersService.productImages)
                                                      .appendString(translit)
                                                      .checkDir()
                                                      .build()))

      }

    }
  }

  /**
   * Апдейт продукта.
   * @param product
   */
  public void updateProduct(ProductEntity product) {

    ProductEntity.withTransaction {
      product.setCategory(CategoryEntity.get(categoryID))
      product.setName(name)
      product.setArticle(article)
      product.setManufacturer(selectedManufacturer)
      product.setDescription(description)
      product.setFilter(selectedFilter)
      //если валидно копируемфайл картинки из темповой дериктории.
      if (product.validate()) {

        String oldPath = product.engImagePath
        File src = new File(new PathBuilder()
            .appendPath(serverFoldersService.productImages)
            .appendString(oldPath).build())

        if (uuid != null)
          src = new File(new PathBuilder()
              .appendPath(serverFoldersService.temp)
              .appendString(uuid).build())

        String dirPath = generateProductImagePath()
        String filePath = new PathBuilder()
            .appendPath(dirPath)
            .appendString("${article}_${name}").build()
        String translit = ConverterRU_EN.translit("${filePath}")

        product.setImagePath(filePath)
        product.setEngImagePath(translit)
        product.save(flush: true)

        File store = new File(new PathBuilder()
            .appendPath(serverFoldersService.productImages)
            .appendString(translit)
            .checkDir()
            .build())

        FileUtils.copyDirectory(src, store)
        imageService.cleanStore(src)
      }
    }
  }

  /**
   * Формируем путь до файла с изображением.
   * @return - путь в файловой системе.
   */
  public String generateProductImagePath() {

    CategoryEntity category = CategoryEntity.get(categoryID)
    List<CategoryEntity> categories = CategoryPathHandler.getCategoryPath(category)

    String imagePath = new PathBuilder()
      .appendPath(categories.first().name)
      .appendString(selectedManufacturer.name).build()

    CategoryEntity[] array = categories.toArray()
    for (int i = 1; i < array.length; i++)
      imagePath = new PathBuilder()
                  .appendPath(imagePath)
                  .appendString(array[i].name)

    return imagePath

  }

  /**
   * Формирует иерархию категорий начиная с самой последней.
   * @param category - предыдущая катеория.
   */
  void fillCategories(CategoryEntity category, List<CategoryEntity> categories) {
    categories.add(category)
    if (category != null && category.parentCategory != null)
      fillCategories(category.parentCategory, categories)
  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Window wnd = event.getTarget().getSpaceOwner() as Window
    Window owner = wnd.getParent().getParent().getParent() as Window
    owner.detach()
  }

}
