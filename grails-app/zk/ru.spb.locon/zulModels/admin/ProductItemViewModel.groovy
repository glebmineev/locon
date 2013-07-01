package ru.spb.locon.zulModels.admin

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.*
import org.zkoss.zk.ui.event.*
import org.zkoss.zul.*
import ru.spb.locon.*
import ru.spb.locon.ImageService
import ru.spb.locon.common.PathHandler
import ru.spb.locon.importer.ConverterRU_EN

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/18/13
 * Time: 12:25 AM
 * To change this template use File | Settings | File Templates.
 */
class ProductItemViewModel {

  Long categoryID
  Long productID
  String uuid

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

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init() {

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

  @Command
  public void uploadImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    UploadEvent uploadEvent = event as UploadEvent
    Image image = event.getTarget().getSpaceOwner().getFellow("targetImage") as Image
    AImage media = uploadEvent.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]

    uuid = imageService.saveImageInTemp(media.getStreamData(), "1", ext)
    imageService.batchResizeImage("${imageService.temp}\\${uuid}", "1", ".${ext}")
    image.setContent(new AImage("${imageService.temp}\\${uuid}\\1-300.${ext}"))

  }

  @Command
  public void saveItem(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    ProductEntity product = ProductEntity.get(productID)
    if (product == null)
      saveProduct(new ProductEntity())
    else
      updateProduct(product)

    Executions.sendRedirect("/admin/editor")
  }

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
        File src = new File("${imageService.temp}\\${uuid}")

        String dirPath = getProductImagePath()
        String filePath = "${dirPath}/${article}_${name}"
        String translit = ConverterRU_EN.translit("${filePath}")

        product.setImagePath(filePath)
        product.setEngImagePath(translit)

        product.save(flush: true)

        if (uuid != null) {
          File store = new File("${imageService.store}\\${translit}")
          if (!store.exists())
            store.mkdirs()

          FileUtils.copyDirectory(src, store)
        }

      }

    }
  }

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
        File src = new File("${imageService.store}\\${oldPath}")
        if (uuid != null)
          src = new File("${imageService.temp}\\${uuid}")

        String dirPath = getProductImagePath()
        String filePath = "${dirPath}/${article}_${name}"
        String translit = ConverterRU_EN.translit("${filePath}")

        product.setImagePath(filePath)
        product.setEngImagePath(translit)
        product.save(flush: true)

        File store = new File("${imageService.store}\\${translit}")
        if (!store.exists())
          store.mkdirs()

        FileUtils.copyDirectory(src, store)
        imageService.cleanStore(src)
      }
    }
  }



  String getProductImagePath() {

    CategoryEntity category = CategoryEntity.get(categoryID)
    List<CategoryEntity> categories = PathHandler.getCategoryPath(category)

    String imagePath = "${categories.first()}/${selectedManufacturer.name}"

    CategoryEntity[] array = categories.toArray()
    for (int i = 1; i < array.length; i++) {
      imagePath = "${imagePath}/${array[i].name}"
    }

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
