package ru.spb.locon.shop

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Spinner
import ru.spb.locon.CartService
import ru.spb.locon.ImportService
import ru.spb.locon.wrappers.ProductModel

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/20/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class CartViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImportService.class)

  ListModelList<ProductModel> cartProduct

  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  @Init
  public void init(){
    List<ProductModel> models = new ArrayList<ProductModel>()
    cartService.getCartProducts().each {it ->
      models.add(new ProductModel(it))
    }
    cartProduct = new ListModelList<ProductModel>(models)
  }

  @Command
  @NotifyChange(["cartProduct"])
  public void removeItem(@BindingParam("productModel") ProductModel productModel){
    try {
      cartService.removeFromCart(productModel.productEntity)

      List<ProductModel> models = new ArrayList<ProductModel>()
      cartService.getCartProducts().each {it ->
        models.add(new ProductModel(it))
      }

      cartProduct.clear()
      cartProduct.addAll(models)

    } catch (Exception e) {
      log.debug(e.getMessage())
    }
  }

  @Command
  public void processCount(@BindingParam("productModel") ProductModel productModel,
                           @BindingParam("inputEvent") InputEvent event){
    try {
      Long currentValue = event.value as Long
      Long previousValue = event.previousValue as Long
      if (previousValue != currentValue) {
        boolean direct = currentValue > previousValue
        long mark = direct ? 1L : -1L
        cartService.incrementCount(productModel, mark)
        (event.getTarget() as Spinner).setValue(cartService.getProductCount(productModel.getProductEntity().id) as Integer)
        BindUtils.postNotifyChange(null, null, productModel, "totalPrice");
      }
    } catch (Exception e) {
      log.debug(e.getMessage())
    }
  }

  @Command
  public void checkout(){
    Executions.sendRedirect("/shop/checkout");
  }

}
