package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.*
import org.zkoss.zkplus.spring.SpringUtil
import locon.LoginService
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import login.URLUtils

/**
 * User: Gleb
 * Date: 14.10.12
 * Time: 14:59
 */
class LoginComposer extends GrailsComposer {

  Textbox login
  Textbox password

  Label errors

  Button loginButton
  Button cancelButton

  Window loginWnd

  LoginService loginService = (LoginService) SpringUtil.getApplicationContext().getBean("loginService")

  def afterCompose = {Window window ->
    loginButton.addEventListener(Events.ON_CLICK, loginButtonLister)
    cancelButton.addEventListener(Events.ON_CLICK, cancelButtonLister)
  }

  EventListener loginButtonLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      String email = login.getValue()
      String pass = password.getValue()
      if (validateData(email, pass))
        loginService.login(email, pass)

      if (!loginService.isLogged()){
        errors.setVisible(true)
        errors.setStyle("color: red;")
        errors.setValue("Неправильная комбинация email и пароля.")
      }
      else
      {
        def params = loginService.params
        String url = URLUtils.buildURL(params)
        Executions.sendRedirect(url)
      }
    }
  }

  EventListener cancelButtonLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      def params = loginService.params
      String url = URLUtils.buildURL(params)
      Executions.sendRedirect(url)
    }
  }

  private boolean validateData(String login, String password) {
    return (!login.isEmpty() &&
            !password.isEmpty())
  }

}
