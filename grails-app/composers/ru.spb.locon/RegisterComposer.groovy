package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import locon.ZulService
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.*
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Executions

/**
 * User: Gleb
 * Date: 07.10.12
 * Time: 17:45
 */
class RegisterComposer extends GrailsComposer {

  Textbox login
  Textbox password
  Textbox fio
  Textbox phone
  Textbox email
  Textbox address

  Button registerButton
  
  ZulService zulService = (ZulService) SpringUtil.getApplicationContext().getBean("zulService")

  def afterCompose = {Window window ->
    registerButton.addEventListener(Events.ON_CLICK, registerLister)
  }

  EventListener registerLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      saveUser()
    }
  }
  
  private void saveUser(){
    if (findByLogin() != null) {
      zulService.showErrors("Пользователь с указанным логином уже существует")
    } else {
      createNewUser()
    }
  }


  private UserEntity findByLogin(){
    UserEntity user = null
    if (validateField(login))
     user = UserEntity.findByLogin(login.getValue())
    return user
  }

  private createNewUser(){
    UserEntity.withTransaction {
      UserEntity user = new UserEntity()
      if (validateField(login))
        user.setLogin(login.getValue())
      if (validateField(password))
        user.setPassword(password.encodeAsSHA1())
      if (validateField(fio))
        user.setFio(fio.getValue())
      if (validateField(phone))
        user.setPhone(phone.getValue())
      if (validateField(email))
        user.setEmail(email.getValue())
      if (validateField(address))
        user.setAddress(address.getValue())

      UserGroupEntity userGroup = UserGroupEntity.findByName("user")
      user.setUserGroup(userGroup)

      if (user.validate()) {
        user.save(flush: true)
        Executions.sendRedirect("/shop")
      }
      else
        zulService.showErrors(user.errors.allErrors)
    }
  }

  private boolean validateField(Textbox textbox) {
    return !(textbox.getValue().isEmpty())
  }

}
