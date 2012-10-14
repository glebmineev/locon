package ru.spb.locon

import login.URLUtils

class AuthController {

  def initService
  def loginService

  def login(){
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]
  }

  def logout() {
    loginService.logout()
    redirect(url: URLUtils.getHostUrl(request) + createLink(
        controller: loginService.params.controller,
        action: loginService.params.action,
        params: loginService.params))
  }
}
