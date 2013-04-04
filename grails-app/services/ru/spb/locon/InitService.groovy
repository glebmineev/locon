package ru.spb.locon

import org.springframework.beans.factory.InitializingBean

class InitService implements InitializingBean {

  static transactional = true

  List<ProductEntity> recommended
  List<CategoryEntity> categories
  List<ManufacturerEntity> manufacturers

  void afterPropertiesSet() {
    categories = CategoryEntity.findAllWhere(parentCategory: null)
    manufacturers = ManufacturerEntity.list()
    recommended = ProductEntity.list(max: 4)
  }

  void refreshShop() {
    afterPropertiesSet()
  }

}
