package ru.spb.locon.zulModels.cabinet

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zul.Div
import org.zkoss.zul.Image
import ru.spb.locon.ImageService
import ru.spb.locon.LoginService
import ru.spb.locon.UserEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/4/13
 * Time: 12:18 AM
 * To change this template use File | Settings | File Templates.
 */
class CabinetViewModel {

  String fio
  String phone
  String email
  String address

  LoginService loginService = ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService
  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init(){
    UserEntity user = loginService.getCurrentUser()
    fio = user.fio
    phone = user.phone
    email = user.email
    address = user.address
  }

  @Command
  public void index(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    Div container = event.getTarget() as Div
    Executions.createComponents("/zul/cabinet/map.zul", container, new HashMap<Object, Object>())
  }

  @Command
  public void uploadImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    UploadEvent uploadEvent = event as UploadEvent
    Image image = event.getTarget().getSpaceOwner().getFellow("targetImage") as Image
    AImage media = uploadEvent.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]
    UserEntity user = loginService.currentUser

    imageService.cleanStore(new File("${imageService.userPictures}\\${user.id}"))
    imageService.saveUserPic(media.getStreamData(), user.id,"1", ext)
    imageService.batchResizeImage("${imageService.userPictures}\\${user.id}", "1", ".${ext}")
    image.setContent(new AImage("${imageService.userPictures}\\${user.id}\\1-150${ext}"))

  }

  @Command
  public void orders(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Executions.sendRedirect("/cabinet/orders")
  }

  @Command
  public void changeCredentials(){

  }

  @Command
  public void changePersonData(){

  }

}
