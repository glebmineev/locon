package ru.spb.locon

class FilterGroupEntity {

  String name

  static mapping = {

    datasource 'ALL'

    table: 'filtergroup'
    columns {
      id column: 'filtergroup_id'
      name column: 'filtergroup_name'
    }

    version false
    productFilterList cascade: 'all-delete-orphan'

  }

  static constraints = {
    name nullable: true
  }

  static hasMany = [
      productFilterList: FilterEntity
  ]

  public String toString(){
    return name
  }

}
