package locon

class CategoryProductEntity {

  CategoryEntity category
  ProductEntity product

  static mapping = {
    table: 'categoryproduct'
    columns {
      id column: 'categoryproduct_id'
      category column: 'categoryproduct_category_id'
      product column: 'categoryproduct_product_id'
    }
  }

  static constraints = {
    category nullable: true
    product nullable: true
  }

  public String toString() {
    return ""
  }

}
