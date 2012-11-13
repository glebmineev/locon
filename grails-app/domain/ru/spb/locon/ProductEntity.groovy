package ru.spb.locon

class ProductEntity {

  static searchable = [except: '']

  String imagePath
  String article
  String name
  String description
  String usage
  String volume
  Float price

  ProductPropertyEntity productProperty
  ManufacturerEntity manufacturer

  static mapping = {

    table: 'product'
    columns {
      id column: 'product_id'
      imagePath column: 'product_imagepath', type: 'text'
      article column: 'product_article'
      name column: 'product_name'
      description column: 'product_description'
      usage column: 'product_usage'
      volume column: 'product_volume'
      productProperty column: 'product_productproperty_id'
      price column: 'product_price'
      manufacturer fetch: "join", column: 'product_manufacturer_id'
    }

    version false

    listCategoryProduct lazy: false,  cascade: 'all-delete-orphan'
    listOrderProduct lazy: false,  cascade: 'all-delete-orphan'
    productProductFilterList lazy: false, cascade: 'all-delete-orphan'
  }

  static hasMany = [
      listCategoryProduct: CategoryProductEntity,
      listOrderProduct: OrderProductEntity,
      productProductFilterList: ProductProductFilterEntity
  ]

  static constraints = {
    imagePath maxSize: 65535, nullable: true
    article nullable: true
    name maxSize: 65535, nullable: true
    description maxSize: 65535, nullable: true
    usage maxSize: 65535, nullable: true
    volume nullable: true
    price nullable: true
    productProperty nullable: true
    manufacturer nullable: true
  }


  public String toString() {
    return name
  }

}
