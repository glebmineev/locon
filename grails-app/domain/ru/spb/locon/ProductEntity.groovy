package ru.spb.locon

class ProductEntity implements Comparable{

  String imagePath
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
      imagePath column: 'product_imagepath', type: 'text'
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

    listCategoryProduct lazy: false,  cascade: 'all-delete-orphan'
    listCartProduct lazy: false,  cascade: 'all-delete-orphan'
  }

  static hasMany = [
      listCategoryProduct: CategoryProductEntity,
      listCartProduct: CartProductEntity
  ]

  static constraints = {
    imagePath maxSize: 65535, nullable: true
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

  @Override
  int compareTo(Object o) {
    name <=> o?.name
  }
}
