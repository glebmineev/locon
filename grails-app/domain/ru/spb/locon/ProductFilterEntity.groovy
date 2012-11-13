package ru.spb.locon

class ProductFilterEntity {

  String name
  ProductFilterGroupEntity productFilterGroup
  
  static mapping = {

    datasource 'ALL'

    table: 'productfilter'
    columns {
      id column: 'productfilter_id'
      name column: 'productfilter_name'
      productFilterGroup column: 'productfilter_productfiltergroup'
    }

    version false
    productProductFilterList lazy: false, cascade: 'all-delete-orphan'
    productFilterCategoryList lazy: false, cascade: 'all-delete-orphan'
  }

  static constraints = {
    name nullable: true
    productFilterGroup nullable: true
  }

  static hasMany = [
      productProductFilterList: ProductProductFilterEntity,
      productFilterCategoryList: ProductFilterCategoryEntity
  ]

  public String toString(){
    return name
  }
}
