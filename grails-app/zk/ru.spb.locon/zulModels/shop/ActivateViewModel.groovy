package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import ru.spb.locon.LoginService
import ru.spb.locon.UserEntity

class ActivateViewModel {

  boolean success = false

  LoginService loginService =
    ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService

  @Init
  public void init(){
    String activateCode = Executions.getCurrent().getParameter("activateCode") as String
    UserEntity toActivate = UserEntity.findByActivateCode(activateCode)
    if (toActivate != null) {
      toActivate.setIsActive(true)
      UserEntity saved = toActivate.save(flush: true)
      loginService.login(saved)

      success = true
      Executions.sendRedirect("/cabinet")
    }
  }

}
