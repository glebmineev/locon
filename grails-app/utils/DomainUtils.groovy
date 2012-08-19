import locon.CategoryEntity

class DomainUtils {
    
    public static List<CategoryEntity> getChildCategories(String parentName) {
        CategoryEntity parent = CategoryEntity.findByName(parentName)
        List<CategoryEntity> result = CategoryEntity.findAllByParentCategory(parent)
        return result
    }

}
