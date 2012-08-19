package locon

class ProductPropertyEntity {

  String name

  static mapping = {
    table: 'productproperty'
    version: false
    columns {
      id column: 'productproperty_id'
      name column: 'productproperty_name'
    }

    productList cascade: 'all-delete-orphan'

  }

  static hasMany = [
      productList: ProductEntity
  ]

  static constraints = {
    name nullable: true
  }
}
