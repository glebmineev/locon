import ru.spb.locon.*

class BootStrap {

  def initService
  def imageService

  def init = { servletContext ->

    ProductPropertyEntity.withTransaction {
      ProductPropertyEntity.findOrSaveWhere(name: "Объем")
    }

    FilterGroupEntity.withTransaction {
      //FilterGroupEntity.findOrSaveWhere(name: "Производитель")
      FilterGroupEntity.findOrSaveWhere(name: "Применение")
    }

    CategoryEntity.withTransaction {
      CategoryEntity.findOrSaveWhere(name: "Для волос")
      CategoryEntity.findOrSaveWhere(name: "Для тела")
      CategoryEntity.findOrSaveWhere(name: "Для рук")
      CategoryEntity.findOrSaveWhere(name: "Депиляция")
      CategoryEntity.findOrSaveWhere(name: "Для мужчин")
      CategoryEntity.findOrSaveWhere(name: "Аксессуары")
    }

    ManufacturerEntity.withTransaction {
      ManufacturerEntity.findOrSaveWhere(name: "Indola", shortName: 'Indola')
      ManufacturerEntity.findOrSaveWhere(name: "Periche", shortName: 'Periche')
      ManufacturerEntity.findOrSaveWhere(name: "Hair light", shortName: 'Hair')
      ManufacturerEntity.findOrSaveWhere(name: "OLLIN Professional", shortName: 'OLLIN')
      ManufacturerEntity.findOrSaveWhere(name: "Schwarzkopf professional", shortName: 'Schwarzkopf')
      ManufacturerEntity.findOrSaveWhere(name: "Screen", shortName: 'Screen')
    }

    RoleEntity.withTransaction {
      RoleEntity.findOrSaveWhere(name: "MANAGER")
      RoleEntity.findOrSaveWhere(name: "USER")
    }

    UserEntity.withTransaction {
      UserEntity user = UserEntity.findOrSaveWhere(
          login: "admin",
          password: "admin".encodeAsSHA1(),
          email: "admin@admin.ru",
          fio: "fio",
          address: "SPb"
      )

      user.addToGroups(RoleEntity.findWhere(name: "MANAGER"))
      user.save()

    }

    InfoEntity.withTransaction {status ->
      List<InfoEntity> list = InfoEntity.list()
      if (list == null || list.size() == 0){
        InfoEntity infoEntity = new InfoEntity()
        infoEntity.save(flush: true)
      }

    }

    OrderIdentEntity.withTransaction {
      OrderIdentEntity.findOrSaveWhere(ident: 100000000L)
    }

    //imageService.syncAllImagesWithServer()

  }

  def destroy = {
  }
}
