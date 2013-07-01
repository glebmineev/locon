package ru.spb.locon.wrappers

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.zul.ListModelList
import ru.spb.locon.CartService
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ProductWrapper implements Wrapper {

  Long productID
  String name
  String article
  Long price
  String usage
  String description
  String volume
  Long countToStock
  ManufacturerEntity manufacturer

  ProductWrapper memento

  ListModelList<ManufacturerEntity> manufacturers

  boolean editingStatus = false

  Long totalPrice;
  Long count;

  boolean inCart = false

  CartService cartService =
    ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService;

  public ProductWrapper(ProductEntity productEntity) {

    this.productID = productEntity.id
    this.name = productEntity.name
    this.article = productEntity.article
    this.price = productEntity.price
    this.volume = productEntity.volume
    this.usage = productEntity.usage
    this.description = productEntity.description
    this.countToStock = productEntity.countToStock
    this.manufacturer = productEntity.manufacturer

    manufacturers = new ListModelList<ManufacturerEntity>(ManufacturerEntity.list(sort: "name"))
    manufacturers.addToSelection(manufacturer)

  }

  public initAsCartItem(){
    long count = cartService.getProductCount(productID)
    this.totalPrice = (count * ProductEntity.get(productID).price)
    this.count = count
    if (count > 0)
      this.inCart = true
  }

  public void restore(){
    this.productID = memento.productID
    this.name = memento.name
    this.article = memento.article
    this.price = memento.price
    this.countToStock = memento.countToStock
    this.manufacturer = memento.manufacturer

    manufacturers = new ListModelList<ManufacturerEntity>()
    manufacturers.addAll(ManufacturerEntity.list(sort: "name"))
    manufacturers.addToSelection(memento.manufacturer)
  }

}
