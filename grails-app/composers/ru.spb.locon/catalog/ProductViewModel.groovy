package ru.spb.locon.catalog

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

  @Init
  public void init() {
    hrefs = new LinkedList<HrefObject>()
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    categoryId = Long.parseLong(Executions.getCurrent().getParameter("category"))
    buildNavPath()
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
}
