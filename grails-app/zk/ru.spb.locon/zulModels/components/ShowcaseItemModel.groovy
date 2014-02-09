package ru.spb.locon.zulModels.components

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ExecutionArgParam
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import ru.spb.locon.CartService
import ru.spb.locon.ProductEntity
import ru.spb.locon.wrappers.ProductWrapper

public class ShowcaseItemModel {

  ProductWrapper productWrapper

  boolean inCart = false

  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService

  @Init
  public void init(@ExecutionArgParam("productWrapper") ProductWrapper productWrapper){

    this.productWrapper = productWrapper
    this.inCart = productWrapper.inCart
  }

  /**
   * Добавление в корзину.
   * @param wrapper
   */
  @Command
  @NotifyChange(["inCart"])
  public void toCart() {
    cartService.addToCart(ProductEntity.get(productWrapper.getId()))
    productWrapper.setInCart(true)
    inCart = true
  }

  @Command
  public void goToProduct(){
    Executions.getCurrent().getDesktop().setBookmark("catalog")
    Executions.sendRedirect("/shop/catalog?product=${productWrapper.id}")
  }

}
