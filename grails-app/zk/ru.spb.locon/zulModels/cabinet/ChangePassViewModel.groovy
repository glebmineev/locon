package ru.spb.locon.zulModels.cabinet

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import ru.spb.locon.LoginService
import ru.spb.locon.UserEntity

class ChangePassViewModel {

  /**
   * Старый пароль.
   */
  String oldPass

  /**
   * новый пароль.
   */
  String newPass

  /**
   * Повтрение нового пароля.
   */
  String retypeNewPass

  LoginService loginService =
    ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService

  @Init
  public void init(){

  }

  @Command
  public void changePass(){
    UserEntity user = loginService.getCurrentUser()
    UserEntity.withTransaction {status ->
      user.setPassword(newPass.encodeAsSHA1())
      user.save(flush: true)
    }
    Executions.sendRedirect("/cabinet/successOperation")
  }

}
