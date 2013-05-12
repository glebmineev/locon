package ru.spb.locon.validators

import com.google.common.base.Strings
import org.zkoss.bind.ValidationContext
import org.zkoss.bind.validator.AbstractValidator

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/11/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
class StdWndValidator extends AbstractValidator {

  @Override
  void validate(ValidationContext ctx) {
    validateName(ctx, ctx.getValidatorArg("name") as String)
  }

  public void validateName(ValidationContext ctx, String value) {
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "name", "Поле не может быть пустым");
  }

}
