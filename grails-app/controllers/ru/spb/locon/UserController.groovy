package ru.spb.locon

import login.URLUtils

class UserController {

  def initService
  def loginService

  def orders() {
    return [mainCategoties: initService.categories]
  }

  def orderItem() {
    return [mainCategoties: initService.categories]
  }

  def beforeInterceptor = {
    List<String> groups = loginService.getUserGroups()
    if (!groups.contains("USER")) {
      loginService.setParams(params)
      redirect(url: URLUtils.getHostUrl(request) + createLink(controller: "auth", action: "login"))
      return
    }
  }

}
