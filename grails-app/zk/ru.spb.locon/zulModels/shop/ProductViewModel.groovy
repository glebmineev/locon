package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.*
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.Window
import ru.spb.locon.*
import ru.spb.locon.common.CategoryPathHandler
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.windows.ImageWindow
import ru.spb.locon.wrappers.HrefWrapper
import ru.spb.locon.wrappers.ProductWrapper

class ProductViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ProductViewModel.class)

  Long productId
  List<ReviewsEntity> reviews

  ProductWrapper model

  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService
  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService
  ServerFoldersService serverFoldersService = ApplicationHolder.getApplication().getMainContext().
      getBean("serverFoldersService") as ServerFoldersService

  @Init
  public void init() {
    productId = Executions.getCurrent().getParameter("product") as Long
    if (productId != null) {
      initGrid()
      initReviews()
    }

  }

  public void initGrid() {
    model = new ProductWrapper(ProductEntity.get(productId))
    cartService.initAsCartItem(model)
  }

  public void initReviews() {
    ProductEntity product = ProductEntity.get(productId)
    reviews = product.reviews as List<ReviewsEntity>
  }

  @Command
  public void zoomImage() {
    ProductEntity productEntity = ProductEntity.get(productId)
    String path = new PathBuilder()
        .appendPath(serverFoldersService.productImages)
        .appendString(productEntity.engImagePath)
        .build()
    String std_name = STD_FILE_NAMES.PRODUCT_NAME.getName()
    int std_size = STD_IMAGE_SIZES.LARGE.getSize()

    AImage aImage = imageService.getImageFile(path, std_name, std_size)
    ImageWindow imageWindow = new ImageWindow(aImage, productEntity.name)
    imageWindow.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    imageWindow.doModal()
  }

  @Command
  public void addReviews() {
    Map<Object, Object> params = new HashMap<Object, Object>()
    params.put("id", productId)

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
    params.put("id", productId)

    Executions.createComponents("/zul/shop/successWnd.zul", wnd, params)
    wnd.doModal()
    wnd.setVisible(true)*/

  }

  @Command
  public void refreshFromBookMark(@BindingParam("event") Event event){
    int r = 0
  }

}
