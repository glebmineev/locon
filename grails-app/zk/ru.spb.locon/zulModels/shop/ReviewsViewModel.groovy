package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.*
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Window
import ru.spb.locon.*
import ru.spb.locon.captcha.RandomStringGenerator

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/5/13
 * Time: 1:37 AM
 * To change this template use File | Settings | File Templates.
 */
class ReviewsViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ReviewsViewModel.class)

  RandomStringGenerator rsg = new RandomStringGenerator(4);

  String captcha = rsg.getRandomString()
  String captchaInput
  String fio
  String review
  String email

  String productID

  LoginService loginService = ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService

  @Init
  public void init(){
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    productID = arg.get("id") as Long

    if (loginService.isLogged()) {
      UserEntity user = loginService.currentUser
      fio = user.fio
      email = user.email
    }

  }

  @Command
  public void addReview(){
    ProductEntity product = ProductEntity.get(productID)
    if (captchaInput.equals(captcha)) {
      ReviewsEntity reviews = new ReviewsEntity()
      ReviewsEntity.withTransaction {
        reviews.setFio(fio)
        reviews.setEmail(email)
        reviews.setContent(review)
        reviews.setDate(new Date())

        product.addToReviews(reviews)
        if (product.validate()) {
          product.save(flush: true)
          Executions.sendRedirect("/shop/product?product=${product.id}")
        }
      }
    }
  }

  @Command
  @NotifyChange(["captcha"])
  public void regenerate(){
    captcha = rsg.getRandomString()
  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    try {
      Window wnd = event.getTarget().getSpaceOwner() as Window
      Window owner = wnd as Window
      owner.detach()
    } catch (Exception ex) {
      log.debug(ex.getMessage())
    }

  }

}
