package ru.spb.locon.wrappers

import ru.spb.locon.CategoryEntity

class CategoryWrapper implements Wrapper {

  Long id
  String name
  String parentName
  String description

  List<CategoryWrapper> children = new ArrayList<CategoryWrapper>()

  CategoryWrapper(CategoryEntity entity) {
    this.id = entity.id
    this.name = entity.name
    this.parentName = entity.parentCategory != null ? entity.parentCategory.name : ""
  }

  public void addChild(CategoryWrapper wrapper){
    children.add(wrapper)
  }

  @Override
  void restore() {

  }

}
