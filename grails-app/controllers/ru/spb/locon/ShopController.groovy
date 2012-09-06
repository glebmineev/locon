package ru.spb.locon

class ShopController {

    def cookieService

    def index() {
        //cookieService.set(response,"username","cookieUser123",604800)

        List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
        List<ManufacturerEntity> manufacturers = ManufacturerEntity.list()

        return [mainCategoties: categories,
                manufacturers: manufacturers]
    }

    def catalog() {
      List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
      List<ManufacturerEntity> manufacturers = ManufacturerEntity.list()

      return [mainCategoties: categories,
          manufacturers: manufacturers]
    }

    def product(){
      List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
      List<ManufacturerEntity> manufacturers = ManufacturerEntity.list()

      return [mainCategoties: categories,
          manufacturers: manufacturers]
    }

    def register() {
        //TODO: Форма регисрации.
    }

    def cabinet() {
        //TODO: Личный кабинет.
    }

    def about() {
      //TODO: О нас.
    }
}
