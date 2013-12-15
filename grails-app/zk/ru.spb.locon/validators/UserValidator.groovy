package ru.spb.locon.validators

import com.google.common.base.Strings
import org.zkoss.bind.ValidationContext
import org.zkoss.bind.validator.AbstractValidator
import ru.spb.locon.UserEntity

/**
 * Валидатор при создании пользователя.
 */
class UserValidator extends AbstractValidator {

  @Override
  void validate(ValidationContext ctx) {
    validateLogin(ctx, ctx.getValidatorArg("login") as String)
    validateEmail(ctx, ctx.getValidatorArg("email") as String)
    validateFio(ctx, ctx.getValidatorArg("fio") as String)
    validateAddress(ctx, ctx.getValidatorArg("address") as String)
    validatePassword(ctx, ctx.getValidatorArg("password") as String,
        ctx.getValidatorArg("repassword") as String)
  }

  public void validateLogin(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "login", "Поле не может быть пустым");
  }

  public void validateFio(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "fio", "Поле не может быть пустым");
  }

  public void validateAddress(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "address", "Поле не может быть пустым");
  }

  public void validateEmail(ValidationContext ctx, String value){
    if (UserEntity.findByEmail(value))
      this.addInvalidMessage(ctx, "email", "Пользователь с таким email уже зарегестрирован");
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "email", "Поле не может быть пустым");
  }

  public void validatePassword(ValidationContext ctx, String password, String repassword){
    if(repassword == null || password == null || !password.equals(repassword))
      this.addInvalidMessage(ctx, "password", "Пароли не совпадают");
  }

}
