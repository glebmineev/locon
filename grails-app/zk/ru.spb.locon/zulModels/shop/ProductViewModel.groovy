package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.*
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.Window
import ru.spb.locon.*
import ru.spb.locon.common.PathHandler
import ru.spb.locon.windows.ImageWindow
import ru.spb.locon.wrappers.HrefWrapper
import ru.spb.locon.wrappers.ProductWrapper

class ProductViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ProductViewModel.class)

  Long productId
  List<HrefWrapper> hrefs
  List<ReviewsEntity> reviews

  ProductWrapper model

  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService
  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init() {
    hrefs = new LinkedList<HrefWrapper>()
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    buildNavPath()
    initGrid()
    initReviews()
  }

  public void initGrid() {
    model = new ProductWrapper(ProductEntity.get(productId))
    cartService.initAsCartItem(model)
  }

  public void buildNavPath() {
    ProductEntity product = ProductEntity.get(productId)
    CategoryEntity category = CategoryEntity.get(product.getCategory().id)
    List<CategoryEntity> categories = PathHandler.getCategoryPath(category)
    hrefs.add(new HrefWrapper("Главная", "/shop"))
    hrefs.add(new HrefWrapper("Каталог товаров", "/shop/catalog?category=${CategoryEntity.findByName("Для волос").id}"))
    categories.each { it ->
      hrefs.add(new HrefWrapper(it.name, "/shop/catalog?category=${it.id}"))
    }
    hrefs.add(new HrefWrapper(product.name, "/shop/product?product=${product.id}"))
  }

  public void initReviews() {
    ProductEntity product = ProductEntity.get(productId)
    reviews = product.reviews as List<ReviewsEntity>
  }

  @Command
  public void zoomImage() {
    ProductEntity productEntity = ProductEntity.get(productId)
    AImage aImage = imageService.getImageFile(productEntity, "500")
    ImageWindow imageWindow = new ImageWindow(aImage, productEntity.name)
    imageWindow.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    imageWindow.doModal()
  }

  @Command
  public void addReviews() {
    Map<Object, Object> params = new HashMap<Object, Object>()
    params.put("productID", productId)

    Window wnd = Executions.createComponents("/zul/shop/reviewsWnd.zul", null, params) as Window
    wnd.setWidth("50%")
    wnd.setHeight("400px")
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)
  }

  @Command
  public void addToCart() {
    model.setInCart(true)
    cartService.addToCart(ProductEntity.get(productId))
    BindUtils.postNotifyChange(null, null, model, "inCart");
    /*Window wnd = new Window()
    wnd.setWidth("60%")
    wnd.setHeight("450px")
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())

    Map<Object, Object> params = new HashMap<Object, Object>()
    params.put("productID", productId)

    Executions.createComponents("/zul/shop/successWnd.zul", wnd, params)
    wnd.doModal()
    wnd.setVisible(true)*/

  }

}
