package locon

class ProductEntity {

    String article
    String name
    String description
    String volume
    Float price
    
    ProductPropertyEntity productProperty    
    CategoryEntity category
    ManufacturerEntity manufacturer
    ProductFilterEntity productFilter

    static mapping = {
        table: 'product'
        version: false
        columns {
            id column: 'product_id'
            article column: 'product_article'
            name column: 'product_name'
            description column:  'product_description'
            volume column: 'product_volume'
            productProperty column: 'product_productproperty_id'
            price column: 'product_price'
            category column: 'product_category_id'
            manufacturer column: 'product_manufacturer_id'
            productFilter column: 'product_productfilter'
        }
    }

    static constraints = {
        article nullable: true
        name nullable: true
        description nullable: true
        volume nullable: true
        price nullable: true
        productProperty nullable: true
        category nullable: true
        manufacturer nullable: true
        productFilter nullable: true
    }
}
