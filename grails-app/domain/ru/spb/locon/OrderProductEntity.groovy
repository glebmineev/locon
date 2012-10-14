package ru.spb.locon

class OrderProductEntity {

  ProductEntity product
  Long countProduct
  OrderEntity order

  static mapping = {

    datasource 'ALL'

    table: 'orderproduct'
    columns {
      id column: 'orderproduct_id'
      product fetch: "join", column: 'orderproduct_product_id'
      countProduct column: 'orderproduct_countproduct_id'
      order fetch: "join", column: 'orderproduct_order_id'
    }

    version false
  }
  static constraints = {
    product nullable: true
    countProduct nullable: true
    order nullable: true
  }
}
