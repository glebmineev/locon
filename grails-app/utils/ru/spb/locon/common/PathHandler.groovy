package ru.spb.locon.common

import ru.spb.locon.CategoryEntity

class PathHandler {

  static List<CategoryEntity> getCategoryPath(CategoryEntity category) {
    List<CategoryEntity> categories = new LinkedList<CategoryEntity>()
    fillCategories(category, categories)
    List<CategoryEntity> reverse = categories.reverse()
    return reverse
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
