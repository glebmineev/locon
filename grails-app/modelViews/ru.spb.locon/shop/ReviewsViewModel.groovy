package ru.spb.locon.shop

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Window
import ru.spb.locon.captcha.RandomStringGenerator

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/5/13
 * Time: 1:37 AM
 * To change this template use File | Settings | File Templates.
 */
class ReviewsViewModel {

  RandomStringGenerator rsg = new RandomStringGenerator(4);

  String captcha = rsg.getRandomString()
  String captchaInput
  String fio
  String review

  @Init
  public void init(){

  }

  @Command
  public void addReview(){

  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    Window wnd = event.getTarget().getSpaceOwner() as Window
    Window owner = wnd as Window
    owner.detach()
  }

}
