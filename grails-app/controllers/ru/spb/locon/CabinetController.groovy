package ru.spb.locon

import ru.spb.locon.login.URLUtils

class CabinetController {

  def initService
  def loginService

  def index(){
    return [mainCategoties: initService.categories]
  }

  def profile(){
    return [mainCategoties: initService.categories]
  }

  def orders() {
    return [mainCategoties: initService.categories]
  }

  def orderItem() {
    return [mainCategoties: initService.categories]
  }

  def changePass() {
    return [mainCategoties: initService.categories]
  }

  def successOperation() {
    return [mainCategoties: initService.categories, message: "Ваш пароль успешно изменен!"]
  }

  def beforeInterceptor = {
    //List<String> groups = loginService.getUserGroups()
    //if (!groups.contains("USER")) {
    UserEntity user = loginService.getCurrentUser()
    if (user == null) {
      loginService.setParams(params)
      redirect(url: URLUtils.getHostUrl(request) + createLink(controller: "auth", action: "login"))
      return
    } else if (!user.isActive) {
      loginService.setParams(params)
      redirect(url: URLUtils.getHostUrl(request) + createLink(controller: "shop", action: "nonActivate"))
      return
    }



  }

}
