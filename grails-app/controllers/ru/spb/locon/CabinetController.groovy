package ru.spb.locon

import ru.spb.locon.login.URLUtils

class CabinetController {

  def initService
  def loginService

  def cabinet(){
    return [mainCategoties: initService.categories]
  }

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