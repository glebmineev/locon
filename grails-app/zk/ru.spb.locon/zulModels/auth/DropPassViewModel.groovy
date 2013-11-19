package ru.spb.locon.zulModels.auth

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import ru.spb.locon.EmailService
import ru.spb.locon.UserEntity
import ru.spb.locon.common.PasswordGenerator


class DropPassViewModel {

  //Почта пользователя.
  String email
  //Генератор пароля
  PasswordGenerator pg = new PasswordGenerator(9)
  //Сервис отсыла письма с паролем
  EmailService emailService =
    ApplicationHolder.getApplication().getMainContext().getBean("emailService") as EmailService

  @Init
  public void init(){ }

  @Command
  public void dropPass(){

    UserEntity user = UserEntity.findByEmail(email)
    String new_pass = pg.getRandomString()
    if (user != null) {
      UserEntity.withTransaction {status ->
        user.setPassword(new_pass.encodeAsSHA1())
        user.save(flush: true)
      }
      emailService.sendPassEmail(this.email, new_pass)
    }

    Executions.sendRedirect("/shop/success")

  }

}
