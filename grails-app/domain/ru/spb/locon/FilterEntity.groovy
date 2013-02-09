package ru.spb.locon

class FilterEntity implements Comparable {

  String name
  FilterGroupEntity filterGroup
  
  static mapping = {

    datasource 'ALL'

    table: 'filter'
    columns {
      id column: 'filter_id'
      name column: 'filter_name'
      filterGroup column: 'filter_filtergroup'
      categories joinTable: [name: 'category_filter', key: 'filter_id']
      products joinTable: [name: 'product_filter', key: 'filter_id']
    }

    version false
    //products cascade: 'all-delete-orphan'
    //categories cascade: 'all-delete-orphan'
  }

  static constraints = {
    name nullable: true
    filterGroup nullable: true
  }

  static hasMany = [
      products: ProductEntity,
      categories: CategoryEntity
  ]

  public String toString(){
    return name
  }

  @Override
  int compareTo(Object o) {
    FilterEntity item = (FilterEntity) o
    if (item.name > this.name)
      return 1
    if (item.name < this.name)
      return -1
    if (item.name.equals(this.name))
      return 0

  }

}
