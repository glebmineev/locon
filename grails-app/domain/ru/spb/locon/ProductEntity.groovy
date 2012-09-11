package ru.spb.locon

class ProductEntity {

  String article
  String name
  String description
  String volume
  Float price

  ProductPropertyEntity productProperty
  ManufacturerEntity manufacturer
  ProductFilterEntity productFilter
  CartEntity cart

  static mapping = {


    table: 'product'
    columns {
      id column: 'product_id'
      article column: 'product_article'
      name column: 'product_name'
      description column: 'product_description'
      volume column: 'product_volume'
      productProperty column: 'product_productproperty_id'
      price column: 'product_price'
      manufacturer column: 'product_manufacturer_id'
      productFilter column: 'product_productfilter'
      cart column: 'product_cart'
    }

    sort name:"desc"
    version: false

    listCategoryProductList lazy: false,  cascade: 'all-delete-orphan'
    listCartProduct lazy: false,  cascade: 'all-delete-orphan'
  }

  static hasMany = [
      listCategoryProductList: CategoryProductEntity,
      listCartProduct: CartProductEntity
  ]

  static constraints = {
    article nullable: true
    name nullable: true
    description nullable: true
    volume nullable: true
    price nullable: true
    productProperty nullable: true
    manufacturer nullable: true
    productFilter nullable: true
    cart nullable: true
  }

  public String toString() {
    return name
  }
}
