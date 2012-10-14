package ru.spb.locon

class ShopController {

  def initService
  def loginService

  def index() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def catalog() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def product() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def checkout() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def success() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def register() {
    loginService.setParams(params)
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def cart() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def about() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }
}
