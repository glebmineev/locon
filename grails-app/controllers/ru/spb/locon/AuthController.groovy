package ru.spb.locon

class AuthController {

  def initService

  def index() {
    return [mainCategoties: initService.categories,
        manufacturers: initService.manufacturers]}

  def login(){

  }

  def logout() {

  }
}
