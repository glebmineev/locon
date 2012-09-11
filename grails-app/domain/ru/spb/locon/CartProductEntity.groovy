package ru.spb.locon

class CartProductEntity {

  CartEntity cart
  ProductEntity product

  static mapping = {

    datasource 'ALL'

    table: 'cartproduct'
    columns {
      id column: 'cartproduct_id'
      cart column: 'cartproduct_cart_id'
      product column: 'cartproduct_product_id'
    }

    product fetch: "join"
    cart fetch: "join"

    version: false
  }
  
  static constraints = {
    cart nullable: true
    product nullable: true
  }

  public String toString() {
    return ""
  }

}
