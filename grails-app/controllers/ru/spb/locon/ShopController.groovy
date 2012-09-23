package ru.spb.locon

import cart.CartUtils

class ShopController {

  def cookieService

  def index() {

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

  def product() {
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

  def cart(){
    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    List<ManufacturerEntity> manufacturers = ManufacturerEntity.list()

    return [mainCategoties: categories,
        manufacturers: manufacturers]
  }

  def about() {
    //TODO: О нас.
  }
}
