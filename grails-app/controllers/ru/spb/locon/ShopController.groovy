package ru.spb.locon

class ShopController {

  def initService

  def index() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def catalog() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def product() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def checkout() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def success() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def register() {
    //TODO: Форма регисрации.
  }

  def cabinet() {
    //TODO: Личный кабинет.
  }

  def cart() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def about() {
    //TODO: О нас.
  }
}
