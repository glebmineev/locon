package ru.spb.locon

import locon.CategoryEntity
import locon.ManufacturerEntity

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

    }

    def register() {
        //TODO: Форма регисрации.
    }

    def cabinet() {
        //TODO: Личный кабинет.
    }
}
