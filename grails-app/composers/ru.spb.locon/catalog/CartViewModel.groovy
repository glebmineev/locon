package ru.spb.locon.catalog

import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zhtml.Messagebox
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Spinner
import ru.spb.locon.CartService
import ru.spb.locon.ProductEntity
import ru.spb.locon.cart.CartItem
import ru.spb.locon.tree.node.CategoryTreeNode

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/20/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class CartViewModel {

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
    cartService.removeFromCart(productModel.productEntity)
    cartProduct.remove(productModel)
  }

  @Command
  public void processCount(@BindingParam("productModel") ProductModel productModel,
                           @BindingParam("inputEvent") InputEvent event){
    boolean direct = (event.value as Long) > (event.previousValue as Long)
    long mark = direct ? 1L : -1L
    cartService.incrementCount(productModel, mark)
    (event.getTarget() as Spinner).setValue(cartService.getProductCount(productModel.getProductEntity().id) as Integer)
    BindUtils.postNotifyChange(null, null, productModel, "totalPrice");
  }

}
