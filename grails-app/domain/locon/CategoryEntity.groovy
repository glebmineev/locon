package locon

class CategoryEntity {

    String name
    String description

    CategoryEntity parentCategory

    static mapping = {
        table: 'category'
        version: false
        columns {
            id column: 'category_id'
            name column: 'category_name'
            description column: 'category_description'
            parentCategory column: 'category_parentcategory_id'
        }

        listProduct cascade: 'all-delete-orphan'
        listCategory cascade: 'all-delete-orphan'
    }

    static hasMany = [
        listProduct: ProductEntity,
        listCategory: CategoryEntity
    ]

    static constraints = {
        name nullable: true
        description nullable: true
    }

    public String toString(){
        return name
    }
}
