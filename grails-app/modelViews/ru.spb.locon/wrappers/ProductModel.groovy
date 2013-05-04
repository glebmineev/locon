package ru.spb.locon.wrappers

import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.CartService
import ru.spb.locon.ProductEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ProductModel {

  ProductEntity productEntity;
  Long totalPrice;
  Long count;

  CartService cartService =
    ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService;

  public ProductModel(ProductEntity productEntity) {
    this.productEntity = productEntity;

    long count = cartService.getProductCount(productEntity.id)

    this.totalPrice = (count * productEntity.price)
    this.count = count
  }

  ProductEntity getProductEntity() {
    return productEntity
  }

  void setProductEntity(ProductEntity productEntity) {
    this.productEntity = productEntity
  }

  Long getTotalPrice() {
    return totalPrice
  }

  void setTotalPrice(Long totalPrice) {
    this.totalPrice = totalPrice
  }

  Long getCount() {
    return count
  }

  void setCount(Long count) {
    this.count = count
  }

}
