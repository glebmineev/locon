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
import org.zkoss.zul.Div
import org.zkoss.zul.Vlayout
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ImageService
import ru.spb.locon.ProductEntity
import ru.spb.locon.common.PathHandler
import ru.spb.locon.windows.ImageWindow
import ru.spb.locon.wrappers.HrefObject

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

  @Command
  public void tryShowReviews(){
    //TODO: show reviews/
  }

  @Command
  public void addReviews(){
    Window wnd = new Window()
    wnd.setWidth("50%")
    wnd.setHeight("400px")
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())

    Executions.createComponents("/zul/shop/reviewsWnd.zul", wnd, new HashMap<Object, Object>())
    wnd.doModal()
    wnd.setVisible(true)
  }

}
