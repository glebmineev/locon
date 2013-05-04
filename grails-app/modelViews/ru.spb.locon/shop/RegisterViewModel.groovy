package ru.spb.locon.shop

import com.google.common.base.Strings
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.RoleEntity
import ru.spb.locon.UserEntity
import ru.spb.locon.ZulService

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
class RegisterViewModel {

  String login
  String password
  String fio
  String phone
  String email
  String address

  ZulService zulService = (ZulService) SpringUtil.getApplicationContext().getBean("zulService")

  @Init
  public void init() {

  }

  @Command
  public void register(){
    saveUser()
  }

  void saveUser(){
    if (findByEmail() != null)
      zulService.showErrors("Пользователь с указанным e-mail уже существует")
    else
      createNewUser()
  }

  UserEntity findByEmail(){
    UserEntity user = null
    if (!Strings.isNullOrEmpty(email))
      user = UserEntity.findByEmail(email)
    return user
  }

  void createNewUser(){
    UserEntity.withTransaction {
      UserEntity user = new UserEntity()
      if (!Strings.isNullOrEmpty(login))
        user.setLogin(login)
      if (!Strings.isNullOrEmpty(password))
        user.setPassword(password.encodeAsSHA1())
      if (!Strings.isNullOrEmpty(fio))
        user.setFio(fio)
      if (!Strings.isNullOrEmpty(phone))
        user.setPhone(phone)
      if (!Strings.isNullOrEmpty(email))
        user.setEmail(email)
      if (!Strings.isNullOrEmpty(address))
        user.setAddress(address)

      RoleEntity group = RoleEntity.findByName("USER")
      user.addToGroups(group)

      if (user.validate()) {
        user.save(flush: true)
        Executions.sendRedirect("/shop")
      }
      else
        zulService.showErrors(user.errors.allErrors)
    }
  }

}
