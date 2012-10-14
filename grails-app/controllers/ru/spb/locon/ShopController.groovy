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
    return [mainCategoties: initService.categories]
  }

  def product() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def checkout() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def success() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def register() {
    loginService.setParams(params)
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def cart() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def about() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }
}
