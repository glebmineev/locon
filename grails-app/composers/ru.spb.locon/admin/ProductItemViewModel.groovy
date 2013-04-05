package ru.spb.locon.admin

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.BindingParam
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
import ru.spb.locon.importer.ConverterRU_EN
import ru.spb.locon.tree.node.CategoryTreeNode

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/18/13
 * Time: 12:25 AM
 * To change this template use File | Settings | File Templates.
 */
class ProductItemViewModel {

  Long categoryID
  String uuid

  ListModelList<Object> productItemModel = new ListModelList<Object>()

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

    initComboboxes()

    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    Long productID

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

  }

  public void initComboboxes() {
    filterModel = new ListModelList<FilterEntity>(FilterEntity.list(sort: "name"))
    manufacturerModel = new ListModelList<ManufacturerEntity>(ManufacturerEntity.list(sort: "name"))
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
    imageService.resizeImage("${imageService.temp}\\${uuid}", "1", ext)

    image.setContent(new AImage("${imageService.temp}\\${uuid}\\1-300.${ext}"))

  }

  @Command
  public void saveItem(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    ProductEntity product = new ProductEntity()
    product.addToCategories(CategoryEntity.get(categoryID))
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

    Executions.sendRedirect("/admin/editor")
  }

  String getProductImagePath() {

    CategoryEntity category = CategoryEntity.get(categoryID)
    List<CategoryEntity> categories = new ArrayList<CategoryEntity>()
    fillCategories(category, categories)
    List<CategoryEntity> reverse = categories.reverse()

    String imagePath = "${reverse.first()}/${selectedManufacturer.name}"

    CategoryEntity[] array = reverse.toArray()
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

  ListModelList<Object> getProductItemModel() {
    return productItemModel
  }

  void setProductItemModel(ListModelList<Object> productItemModel) {
    this.productItemModel = productItemModel
  }

  ListModelList<FilterEntity> getFilterModel() {
    return filterModel
  }

  void setFilterModel(ListModelList<FilterEntity> filterModel) {
    this.filterModel = filterModel
  }

  ListModelList<ManufacturerEntity> getManufacturerModel() {
    return manufacturerModel
  }

  void setManufacturerModel(ListModelList<ManufacturerEntity> manufacturerModel) {
    this.manufacturerModel = manufacturerModel
  }

  String getArticle() {
    return article
  }

  void setArticle(String article) {
    this.article = article
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
  }

  String getDescription() {
    return description
  }

  void setDescription(String description) {
    this.description = description
  }

  String getPrice() {
    return price
  }

  void setPrice(String price) {
    this.price = price
  }

  String getVolume() {
    return volume
  }

  void setVolume(String volume) {
    this.volume = volume
  }

  ImageService getImageService() {
    return imageService
  }

  void setImageService(ImageService imageService) {
    this.imageService = imageService
  }

  boolean getIsModal() {
    return isModal
  }

  void setIsModal(boolean modal) {
    isModal = modal
  }

  ManufacturerEntity getSelectedManufacturer() {
    return selectedManufacturer
  }

  void setSelectedManufacturer(ManufacturerEntity selectedManufacturer) {
    this.selectedManufacturer = selectedManufacturer
  }

  FilterEntity getSelectedFilter() {
    return selectedFilter
  }

  void setSelectedFilter(FilterEntity selectedFilter) {
    this.selectedFilter = selectedFilter
  }
}
