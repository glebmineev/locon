package ru.spb.locon

import ru.spb.locon.login.URLUtils

class AdminController {

  def loginService

  def index() { }

  def editor() { }

  def orders() { }

  def orderItem() { }

  def importCatalog() { }

  def products() { }

  def productItem() { }

  def manufacturers() {}

  def filters() { }

  def users() { }

  def info() { }

  def categories() { }

  def saveInfo(){ }

  def beforeInterceptor = {
    List<String> groups = loginService.getUserGroups()
    if (!groups.contains("MANAGER")) {
      loginService.setParams(params)
      redirect(url: URLUtils.getHostUrl(request) + createLink(controller: "auth", action: "login"))
      return
    }
  }
}
