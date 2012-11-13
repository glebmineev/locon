import ru.spb.locon.ProductPropertyEntity
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.UserEntity
import ru.spb.locon.UserGroupEntity
import ru.spb.locon.GroupEntity
import ru.spb.locon.ProductFilterGroupEntity

class BootStrap {

  def initService

  def init = { servletContext ->

    ProductPropertyEntity.withTransaction {
      ProductPropertyEntity.findOrSaveWhere(name: "Объем")
    }

    ProductFilterGroupEntity.withTransaction {
      ProductFilterGroupEntity.findOrSaveWhere(name: "Производитель")
      ProductFilterGroupEntity.findOrSaveWhere(name: "Применение")
    }

    CategoryEntity.withTransaction {
      CategoryEntity.findOrSaveWhere(name: "Для волос")
      CategoryEntity.findOrSaveWhere(name: "Для тела")
      CategoryEntity.findOrSaveWhere(name: "Для рук")
      CategoryEntity.findOrSaveWhere(name: "Депиляция")
    }

    ManufacturerEntity.withTransaction {
      ManufacturerEntity.findOrSaveWhere(name: "Indola", shortName: 'Indola')
      ManufacturerEntity.findOrSaveWhere(name: "Periche", shortName: 'Periche')
      ManufacturerEntity.findOrSaveWhere(name: "Hair light", shortName: 'Hair')
      ManufacturerEntity.findOrSaveWhere(name: "OLLIN Professional", shortName: 'OLLIN')
      ManufacturerEntity.findOrSaveWhere(name: "Schwarzkopf professional", shortName: 'Schwarzkopf')
      ManufacturerEntity.findOrSaveWhere(name: "Screen", shortName: 'Screen')
    }

    GroupEntity.withTransaction {
      GroupEntity.findOrSaveWhere(name: "MANAGER")
      GroupEntity.findOrSaveWhere(name: "USER")
    }

    UserEntity.withTransaction {
      UserEntity.findOrSaveWhere(
          login: "admin",
          password: "admin".encodeAsSHA1(),
          email: "admin@admin.ru",
          fio: "fio",
          address: "SPb"
      )
    }

    UserGroupEntity.withTransaction {
      UserGroupEntity.findOrSaveWhere(user: UserEntity.findByLogin("admin"), group: GroupEntity.findByName("MANAGER"))
    }
  }

  def destroy = {
  }
}
