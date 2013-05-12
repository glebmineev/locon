package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.ListModelList
import ru.spb.locon.*
import ru.spb.locon.wrappers.ProductModel

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/26/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
class RecommendedViewModel {

  ListModelList<ProductModel> products;

  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService
  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService

  @Init
  public void init(){
    products = new ListModelList<ProductModel>()
    initService.recommended.each {it ->
      ProductModel model = new ProductModel(it)
      model.initAsCartItem()
      products.add(model)
    }
  }

  @Command
  public void redirectToProductItem(@BindingParam("productModel") ProductModel productModel){
    Executions.sendRedirect("/shop/product?product=${productModel.productID}")
  }

  @Command
  public void addToCart(@BindingParam("productModel") ProductModel productModel){
    productModel.setInCart(true)
    cartService.addToCart(ProductEntity.get(productModel.productID))
    BindUtils.postNotifyChange(null, null, productModel, "inCart");
  }

}
