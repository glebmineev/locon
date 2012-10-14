package ru.spb.locon

class ManufacturerEntity {

  String name
  String description

  static mapping = {

    datasource 'ALL'

    table: 'manufacturer'
    columns {
      id column: 'manufacturer_id'
      name column: 'manufacturer_name'
      description column: 'manufacturer_description'
    }
    version false
    productList cascade: 'all-delete-orphan'
  }

  static hasMany = [
      productList: ProductEntity
  ]

  static constraints = {
    name nullable: true
    description nullable: true
  }

  public String toString() {
    return name
  }
}
