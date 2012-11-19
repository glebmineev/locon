package ru.spb.locon

class ProductProductFilterEntity {

  ProductEntity product
  ProductFilterEntity productFilter

  static mapping = {

    datasource 'ALL'

    table: 'productproductfilter'
    columns {
      id column: 'productproductfilter_id'
      product fetch: "join", column: 'productproductfilter_product_id'
      productFilter fetch: "join", column: 'productproductfilter_productfilter_id'
    }

    version false

  }

  static constraints = {
    product nullable: true
    productFilter nullable: true
  }

}
