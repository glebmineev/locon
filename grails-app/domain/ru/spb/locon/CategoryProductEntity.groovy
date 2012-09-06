package ru.spb.locon

class CategoryProductEntity {

  CategoryEntity category
  ProductEntity product

  static mapping = {

    datasource 'ALL'

    table: 'categoryproduct'
    columns {
      id column: 'categoryproduct_id'
      category column: 'categoryproduct_category_id'
      product column: 'categoryproduct_product_id'
    }

    sort category:"desc"

    product fetch: "join"
    category fetch: "join"

    version: false
  }

  static constraints = {
    category nullable: true
    product nullable: true
  }

  public String toString() {
    return ""
  }

}
