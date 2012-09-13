package domain

import ru.spb.locon.CategoryEntity
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import java.lang.reflect.Type
import ru.spb.locon.CartEntity
import ru.spb.locon.ProductEntity

class DomainUtils {

  def cookieService

  public static List<CategoryEntity> getChildCategories(String parentName) {
    CategoryEntity parent = CategoryEntity.findByName(parentName)
    List<CategoryEntity> result = CategoryEntity.findAllByParentCategory(parent)
    return result
  }

  public static List<String> fieldInfo(def domain) {
    List<String> result = new ArrayList<String>()
    domain.constraints.each {String name, ConstrainedProperty property ->
      Type type = property.getPropertyType()
      String typeName = type.getName()
      if (typeName.contains("Float") || typeName.contains("String")) {
        result.add(name)
      }
    }
    return result
  }

  public static String parseTo(Object value) {
    String result = null
    if (value instanceof Float) {
      result = Float.valueOf(value)
    }
    if (value instanceof String) {
      result = value
    }
    return result
  }

}
