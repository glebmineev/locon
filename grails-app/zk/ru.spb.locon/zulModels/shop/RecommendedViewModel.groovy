package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.ListModelList
import ru.spb.locon.*
import ru.spb.locon.wrappers.ProductWrapper

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/26/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
class RecommendedViewModel {

  ListModelList<ProductWrapper> products;

  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService
  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService

  @Init
  public void init(){
    products = new ListModelList<ProductWrapper>()
    initService.recommended.each {it ->
      ProductWrapper model = new ProductWrapper(it)
      cartService.initAsCartItem(model)
      products.add(model)
    }
  }

  @Command
  public void redirectToProductItem(@BindingParam("productModel") ProductWrapper productModel){
    Executions.sendRedirect("/shop/product?product=${productModel.id}")
  }

  @Command
  public void addToCart(@BindingParam("productModel") ProductWrapper productModel){
    productModel.setInCart(true)
    cartService.addToCart(ProductEntity.get(productModel.id))
    BindUtils.postNotifyChange(null, null, productModel, "inCart");
  }

}
