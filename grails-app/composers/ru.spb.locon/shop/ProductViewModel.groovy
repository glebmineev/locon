package ru.spb.locon.shop

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ImageService
import ru.spb.locon.ProductEntity
import ru.spb.locon.common.PathHandler
import ru.spb.locon.navigate.HrefObject
import ru.spb.locon.windows.ImageWindow

class ProductViewModel {

  Long productId
  Long categoryId
  ImageService imageService = (ImageService) SpringUtil.getApplicationContext().getBean("imageService")
  List<HrefObject> hrefs

  String usage
  String description
  String name
  String price
  String volume

  @Init
  public void init() {
    hrefs = new LinkedList<HrefObject>()
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    categoryId = Long.parseLong(Executions.getCurrent().getParameter("category"))
    buildNavPath()
    initGrid()
  }

  public void initGrid(){
    ProductEntity productEntity = ProductEntity.get(productId)
    usage = productEntity.getUsage()
    description = productEntity.getDescription()
    name = productEntity.getName()
    price = productEntity.getPrice() as String
    volume = productEntity.getVolume()
  }

  public void buildNavPath(){
    ProductEntity product = ProductEntity.get(productId)
    List<CategoryEntity> categories = PathHandler.getCategoryPath(CategoryEntity.get(categoryId))
    hrefs.add(new HrefObject("Главная", "/shop"))
    hrefs.add(new HrefObject("Каталог товаров", "/catalog"))
    categories.each {it ->
      hrefs.add(new HrefObject(it.name, "/shop/catalog?category=${it.id}"))
    }
    hrefs.add(new HrefObject(product.name, "/shop/product?category=${categoryId}&product=${product.id}"))
  }

  @Command
  public void zoomImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    ProductEntity productEntity = ProductEntity.get(productId)
    AImage aImage = imageService.getImageFile(productEntity, "500")
    ImageWindow imageWindow = new ImageWindow(aImage, productEntity.name)
    imageWindow.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    imageWindow.doModal()
  }

  Long getProductId() {
    return productId
  }

  void setProductId(Long productId) {
    this.productId = productId
  }

  List<HrefObject> getHrefs() {
    return hrefs
  }

  void setHrefs(List<HrefObject> hrefs) {
    this.hrefs = hrefs
  }

  String getUsage() {
    return usage
  }

  void setUsage(String usage) {
    this.usage = usage
  }

  String getDescription() {
    return description
  }

  void setDescription(String description) {
    this.description = description
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
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

}
