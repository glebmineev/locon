package ru.spb.locon.zulModels.auth

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.LoginService
import ru.spb.locon.ZulService
import ru.spb.locon.login.URLUtils

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
class LoginViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(LoginViewModel.class)

  String email
  String password

  LoginService loginService = SpringUtil.getApplicationContext().getBean("loginService") as LoginService
  ZulService zulService = SpringUtil.getApplicationContext().getBean("zulService") as ZulService

  @Init
  public void init() {

  }

  @Command
  public void logging(){
    loginService.login(email, password)
    if (!loginService.isLogged())
      zulService.showErrors("Неправильная комбинация email и пароля.")
    else
    {
      def params = loginService.params
      String url = "/"
      if (params != null)
        url = URLUtils.buildURL(params)
      Executions.sendRedirect(url)
    }
  }

}
