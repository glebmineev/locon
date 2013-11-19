package ru.spb.locon.validators

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.ValidationContext
import org.zkoss.bind.validator.AbstractValidator
import ru.spb.locon.LoginService

class ChangePassValidator extends AbstractValidator {

  LoginService loginService =
    ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService

  @Override
  void validate(ValidationContext ctx) {

    validateOldPassword(ctx, ctx.getValidatorArg("oldPass") as String)
    validateNewPassword(ctx, ctx.getValidatorArg("newPass") as String,
        ctx.getValidatorArg("retypeNewPass") as String)
  }

  public void validateOldPassword(ValidationContext ctx, String password) {
    if(password == null)
      this.addInvalidMessage(ctx, "oldPass", "Не введен старый пароль");
    if(!loginService.getCurrentUser().getPassword().equals(password.encodeAsSHA1()))
      this.addInvalidMessage(ctx, "oldPass", "Старый пароль введен не верно");
  }

  public void validateNewPassword(ValidationContext ctx, String password, String repassword){
    if(repassword == null || password == null || !password.equals(repassword))
      this.addInvalidMessage(ctx, "newPass", "Пароли не совпадают");
  }

}
