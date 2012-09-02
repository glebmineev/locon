package locon

class ProductFilterEntity {

  String name

  static mapping = {

    datasource 'ALL'

    table: 'productfilter'
    columns {
      id column: 'productfilter_id'
      name column: 'productfilter_name'
    }
    version: false
    productList cascade: 'all-delete-orphan'
  }

  static constraints = {
    name nullable: true
  }

  static hasMany = [
      productList: ProductEntity
  ]

  public String toString(){
    return name
  }
}
