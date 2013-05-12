package ru.spb.locon.validators

import com.google.common.base.Strings
import org.zkoss.bind.ValidationContext
import org.zkoss.bind.validator.AbstractValidator

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/5/13
 * Time: 6:19 PM
 * To change this template use File | Settings | File Templates.
 */
class ReviewValidator extends AbstractValidator {

  @Override
  void validate(ValidationContext ctx) {
    validateFio(ctx, ctx.getValidatorArg("fio") as String)
    validateReview(ctx, ctx.getValidatorArg("review") as String)
    validateCaptcha(ctx, ctx.getValidatorArg("captcha") as String,
                         ctx.getValidatorArg("captchaInput") as String)
  }

  public void validateFio(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "fio", "Поле ФИО не может быть пустым!");
  }

  public void validateReview(ValidationContext ctx, String value){
    if (Strings.isNullOrEmpty(value))
      this.addInvalidMessage(ctx, "review", "Поле Отзыв не может быть пустым!");
  }

  public void validateCaptcha(ValidationContext ctx, String captcha, String captchaInput){
    if(captchaInput == null || !captcha.equals(captchaInput))
      this.addInvalidMessage(ctx, "captcha", "Введенные символы неверны!");
  }


}
