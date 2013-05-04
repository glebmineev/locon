package ru.spb.locon.cabinet

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Div
import ru.spb.locon.LoginService
import ru.spb.locon.UserEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/4/13
 * Time: 12:18 AM
 * To change this template use File | Settings | File Templates.
 */
class CabinetViewModel {

  String fio
  String phone
  String email
  String address

  LoginService loginService = SpringUtil.getApplicationContext().getBean("loginService") as LoginService

  @Init
  public void init(){
    UserEntity user = loginService.getCurrentUser()
    fio = user.fio
    phone = user.phone
    email = user.email
    address = user.address
  }

  @Command
  public void index(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    Div container = event.getTarget() as Div
    Executions.createComponents("/zul/cabinet/map.zul", container, new HashMap<Object, Object>())
  }

  @Command
  public void orders(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Executions.sendRedirect("/cabinet/orders")
  }

  @Command
  public void changeCredentials(){

  }

  @Command
  public void changePersonData(){

  }

}
