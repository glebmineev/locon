package ru.spb.locon.admin.wrapper

import org.zkoss.zul.ListModelList
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/11/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
class ProductWrapper implements Cloneable {

  Long productID
  String name
  String article
  Long price
  Long countToStock
  ManufacturerEntity manufacturer

  ProductWrapper memento

  ListModelList<ManufacturerEntity> manufacturers

  boolean editingStatus = false

  ProductWrapper(ProductEntity product) {
    this.productID = product.id
    this.name = product.name
    this.article = product.article
    this.price = product.price
    this.countToStock = product.countToStock
    this.manufacturer = product.manufacturer

    manufacturers = new ListModelList<ManufacturerEntity>(ManufacturerEntity.list(sort: "name"))
    manufacturers.addToSelection(manufacturer)

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

  Long getProductID() {
    return productID
  }

  void setProductID(Long productID) {
    this.productID = productID
  }

  boolean getEditingStatus() {
    return editingStatus
  }

  void setEditingStatus(boolean editingStatus) {
    this.editingStatus = editingStatus
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
  }

  String getArticle() {
    return article
  }

  void setArticle(String article) {
    this.article = article
  }

  Long getPrice() {
    return price
  }

  void setPrice(Long price) {
    this.price = price
  }

  Long getCountToStock() {
    return countToStock
  }

  void setCountToStock(Long countToStock) {
    this.countToStock = countToStock
  }

  ManufacturerEntity getManufacturer() {
    return manufacturers.getSelection().first()
  }

  void setManufacturer(ManufacturerEntity manufacturer) {
    this.manufacturer = manufacturer
  }

  ListModelList<ManufacturerEntity> getManufacturers() {
    return manufacturers
  }

  void setManufacturers(ListModelList<ManufacturerEntity> manufacturers) {
    this.manufacturers = manufacturers
  }

  ProductWrapper getMemento() {
    return memento
  }

  void setMemento(ProductWrapper memento) {
    this.memento = memento
  }

}
