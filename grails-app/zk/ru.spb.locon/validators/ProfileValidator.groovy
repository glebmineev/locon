package ru.spb.locon.validators

import com.google.common.base.Strings
import org.zkoss.bind.ValidationContext
import org.zkoss.bind.validator.AbstractValidator
import ru.spb.locon.UserEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 8/7/13
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
class ProfileValidator extends AbstractValidator {

  @Override
  void validate(ValidationContext ctx) {
    validateFio(ctx, ctx.getValidatorArg("fio") as String)
    validateAddress(ctx, ctx.getValidatorArg("address") as String)
  }

  public void validateFio(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "fio", "Поле не может быть пустым");
  }


  public void validateAddress(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "address", "Поле не может быть пустым");
  }

}
