package ru.spb.locon.shop

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.ListModelList
import ru.spb.locon.*

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/26/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
class RecommendedViewModel {

  ListModelList<ProductEntity> products;

  InitService initService = (InitService) SpringUtil.getApplicationContext().getBean("initService")
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  @Init
  public void init(){
    products = new ListModelList<ProductEntity>(initService.recommended)
  }

  @Command
  public void redirectToProductItem(@BindingParam("product") ProductEntity product){
    ProductEntity retirived = ProductEntity.get(product.id)
    Executions.sendRedirect("/shop/product?category=${retirived.getCategories().first().id}&product=${retirived.id}")
  }

  @Command
  public void addToCart(@BindingParam("product") ProductEntity product){
    cartService.addToCart(product)
  }

}
