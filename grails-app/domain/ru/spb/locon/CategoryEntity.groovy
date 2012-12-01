package ru.spb.locon

class CategoryEntity {

  String name
  String description

  CategoryEntity parentCategory

  static mapping = {

    datasource 'ALL'

    table: 'category'
    columns {
      id column: 'category_id'
      name column: 'category_name'
      description column: 'category_description'
      parentCategory column: 'category_parentcategory_id'
      products joinTable: [name: 'category_product', key: 'category_id']
    }

    version false

    products lazy: false, cascade: 'all-delete-orphan'
    //listCategoryProduct sort: "product", order: "desc", lazy: false, cascade: 'all-delete-orphan'
    listCategory sort: "name", order: "desc", lazy: false, cascade: 'all-delete-orphan'
    productFilterCategoryList sort: "category", lazy: false, cascade: 'all-delete-orphan'
  }

  static belongsTo = ProductEntity

  static hasMany = [
      products: ProductEntity,
      listCategory: CategoryEntity,
      productFilterCategoryList: ProductFilterCategoryEntity
  ]

  static constraints = {
    name nullable: true
    description nullable: true
  }

  public String toString() {
    return name
  }
}
