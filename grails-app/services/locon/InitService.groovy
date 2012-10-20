package locon

import ru.spb.locon.CategoryEntity
import ru.spb.locon.ManufacturerEntity
import org.springframework.beans.factory.InitializingBean
import ru.spb.locon.ProductEntity

class InitService implements InitializingBean{

  static transactional = true

  List<ProductEntity> recommended
  List<CategoryEntity> categories
  List<ManufacturerEntity> manufacturers

  void afterPropertiesSet() {
    categories = CategoryEntity.findAllWhere(parentCategory: null)
    manufacturers = ManufacturerEntity.list()
    recommended = ProductEntity.list(max: 4)

  }

}
