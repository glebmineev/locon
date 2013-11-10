package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import ru.spb.locon.CartService
import ru.spb.locon.ProductEntity

class CatalogRowViewModel {

  Long productID
  String name
  String manufacturer
  Long price
  boolean inCart = false

  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService

  @Init
  public void init() {
    def arg = Executions.getCurrent().getArg()
    productID = arg.get("id") as Long

    ProductEntity product = ProductEntity.get(productID)
    this.name = product.name
    this.manufacturer = product.manufacturer.name
    this.price = product.price

    if (cartService.findedInCart(productID))
      inCart = true

  }

  @Command
  public void redirectToProductItem(){
    Executions.sendRedirect("/shop/product?product=${productID}")
  }

  @Command
  @NotifyChange(["inCart"])
  public void toCart(){
    inCart = true
    cartService.addToCart(ProductEntity.get(productID))
  }

}
