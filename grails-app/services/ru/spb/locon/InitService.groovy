package ru.spb.locon

import com.google.common.base.Function
import com.google.common.collect.Collections2
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.beans.factory.InitializingBean
import ru.spb.locon.wrappers.ProductWrapper

class InitService implements InitializingBean {

  static transactional = true

  List<ProductEntity> recommended
  List<ProductEntity> popular
  List<ProductEntity> hits
  List<CategoryEntity> categories
  List<ManufacturerEntity> manufacturers
  InfoEntity info

  void afterPropertiesSet() {
    categories = CategoryEntity.findAllWhere(parentCategory: null)
    info = InfoEntity.first()
    manufacturers = ManufacturerEntity.list()
    recommended = ProductEntity.list(max: 4)
    popular = ProductEntity.list(max: 6)
    hits = ProductEntity.list(max: 5)
  }

  void refreshShop() {
    afterPropertiesSet()
  }

}
