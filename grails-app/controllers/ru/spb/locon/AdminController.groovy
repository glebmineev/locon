package ru.spb.locon

import ru.spb.locon.login.URLUtils
import ru.spb.locon.common.StringUtils

class AdminController {

  def loginService

  def index() { }

  def editor() { }

  def orders() { }

  def orderItem() { }

  def importCatalog() { }

  def manufacturers() {}

  def info() {}

  def beforeInterceptor = {
    List<String> groups = loginService.getUserGroups()
    if (!groups.contains("MANAGER")) {
      loginService.setParams(params)
      redirect(url: URLUtils.getHostUrl(request) + createLink(controller: "auth", action: "login"))
      return
    }
  }
}
