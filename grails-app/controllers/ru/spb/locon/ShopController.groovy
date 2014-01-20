package ru.spb.locon

import org.zkoss.zk.ui.sys.Attributes

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

  def details() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def contacts() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def delivery() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def seeYouEmail() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def activate() {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

  def nonActivate () {
    loginService.setParams(params)
    return [mainCategoties: initService.categories]
  }

}
