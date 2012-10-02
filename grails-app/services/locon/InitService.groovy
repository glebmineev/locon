package locon

import ru.spb.locon.CategoryEntity
import ru.spb.locon.ManufacturerEntity
import org.springframework.beans.factory.InitializingBean

class InitService implements InitializingBean{

  static transactional = true

  List<CategoryEntity> categories
  List<ManufacturerEntity> manufacturers

  void afterPropertiesSet() {
    categories = CategoryEntity.findAllWhere(parentCategory: null)
    manufacturers = ManufacturerEntity.list()

  }

}
