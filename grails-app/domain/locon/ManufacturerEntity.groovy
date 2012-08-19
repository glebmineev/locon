package locon

class ManufacturerEntity {

  String name
  String description

  static mapping = {
    table: 'manufacturer'
    columns {
      id column: 'manufacturer_id'
      name column: 'manufacturer_name'
      description column: 'manufacturer_description'
    }
    version: false
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
