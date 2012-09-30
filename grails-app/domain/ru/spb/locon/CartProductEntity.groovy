package ru.spb.locon

class CartProductEntity {

  CartEntity cart
  ProductEntity product
  Integer productsCount

  static mapping = {

    datasource 'ALL'

    table: 'cartproduct'
    columns {
      id column: 'cartproduct_id'
      cart fetch: "join", column: 'cartproduct_cart_id'
      product fetch: "join", column: 'cartproduct_product_id'
      productsCount column:  'cartproduct_productscount'
    }

    version: false
  }
  
  static constraints = {
    cart nullable: true
    product nullable: true
    productsCount nullable: true
  }

  public String toString() {
    return ""
  }

}
