package ru.spb.locon

class ProductPropertyEntity {

  String name

  static mapping = {

    datasource 'ALL'

    table: 'productproperty'
    columns {
      id column: 'productproperty_id'
      name column: 'productproperty_name'
    }

    version false

    productList cascade: 'all-delete-orphan'

  }

  static hasMany = [
      productList: ProductEntity
  ]

  static constraints = {
    name nullable: true
  }
}
