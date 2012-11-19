package ru.spb.locon

class ProductFilterGroupEntity {

  String name

  static mapping = {

    datasource 'ALL'

    table: 'productfiltergroup'
    columns {
      id column: 'productfiltergroup_id'
      name column: 'productfiltergroup_name'
    }

    version false
    productFilterList lazy: false, cascade: 'all-delete-orphan'

  }

  static constraints = {
    name nullable: true
  }

  static hasMany = [
      productFilterList: ProductFilterEntity
  ]

  public String toString(){
    return name
  }

}
