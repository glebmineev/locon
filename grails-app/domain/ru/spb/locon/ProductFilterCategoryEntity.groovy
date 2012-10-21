package ru.spb.locon

class ProductFilterCategoryEntity {

  CategoryEntity category
  ProductFilterEntity productFilter

  static mapping = {

    datasource 'ALL'

    table: 'productfiltercategory'
    columns {
      id column: 'productfiltercategory_id'
      category fetch: "join", column: 'productfiltercategory_category_id'
      productFilter fetch: "join", column: 'productfiltercategory_productfilter_id'
    }

    version false

  }

  static constraints = {
    category nullable: true
    productFilter nullable: true
  }
}
