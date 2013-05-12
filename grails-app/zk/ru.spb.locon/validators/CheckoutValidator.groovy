package ru.spb.locon.validators

import com.google.common.base.Strings
import org.zkoss.bind.ValidationContext
import org.zkoss.bind.validator.AbstractValidator

class CheckoutValidator extends AbstractValidator {

  @Override
  void validate(ValidationContext ctx) {
    validateFio(ctx, ctx.getValidatorArg("fio") as String)
    validateAddress(ctx, ctx.getValidatorArg("address") as String)
    validateEmail(ctx, ctx.getValidatorArg("email") as String)
  }

  public void validateFio(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "fio", "Поле ФИО не может быть пустым");
  }

  public void validateAddress(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "address", "Поле Адрес не может быть пустым");
  }

  public void validateEmail(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "email", "Поле Email не может быть пустым");
  }

}
