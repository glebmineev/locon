package ru.spb.locon.common

import ru.spb.locon.CategoryEntity

class CategoryPathHandler {

  public static List<CategoryEntity> getCategoryPath(CategoryEntity category) {
    List<CategoryEntity> categories = new LinkedList<CategoryEntity>()
    fillCategories(category, categories)
    List<CategoryEntity> reverse = categories.reverse()
    return reverse
  }

  public static String generatePathAsString(List<CategoryEntity> categories) {
    PathBuilder builder = new PathBuilder()
    categories.each{it ->
      builder.appendPath(it.name)
    }
    return builder.build()
  }

  /**
   * Формирует иерархию категорий начиная с самой последней.
   * @param category - предыдущая катеория.
   */
  private static void fillCategories(CategoryEntity category, List<CategoryEntity> categories) {
    categories.add(category)
    if (category != null && category.parentCategory != null)
      fillCategories(category.parentCategory, categories)
  }

}
