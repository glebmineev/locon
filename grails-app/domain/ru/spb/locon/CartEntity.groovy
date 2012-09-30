package ru.spb.locon

class CartEntity {

  String uuid
  Date dateCreate

  static mapping = {

    datasource 'ALL'

    table: 'cart'
    columns {
      id column: 'cart_id'
      uuid column: 'cart_uuid'
    }

    version: false

    listCartProduct sort: "product", order: "desc", lazy: false, cascade: 'all-delete-orphan'
  }

  static hasMany = [
      listCartProduct: CartProductEntity
  ]

  static constraints = {
    uuid nullable: true
    dateCreate nullable: true
  }
}
