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
    }

    version false

    listCategoryProduct sort: "product", order: "desc", lazy: false, cascade: 'all-delete-orphan'
    listCategory sort: "name", order: "desc", lazy: false, cascade: 'all-delete-orphan'
  }

  static hasMany = [
      listCategoryProduct: CategoryProductEntity,
      listCategory: CategoryEntity
  ]

  static constraints = {
    name nullable: true
    description nullable: true
  }

  public String toString() {
    return name
  }
}
