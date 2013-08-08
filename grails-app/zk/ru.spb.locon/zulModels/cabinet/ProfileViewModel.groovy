package ru.spb.locon.zulModels.cabinet

import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.codecs.SHA1BytesCodec
import org.codehaus.groovy.grails.plugins.codecs.SHA1Codec
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zul.Image
import ru.spb.locon.ImageService
import ru.spb.locon.LoginService
import ru.spb.locon.UserEntity

import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 8/1/13
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
class ProfileViewModel {

  String fio
  String phone
  String email
  String address
  String password
  String repassword

  String uuid

  String fileSeparator = System.getProperty("file.separator")

  LoginService loginService = ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService
  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init(){
    UserEntity user = loginService.getCurrentUser()
    fio = user.fio
    phone = user.phone
    email = user.email
    address = user.address

    String shaPassword = user.password

    SHA1Codec sha1Codec = new SHA1Codec()
    MessageDigest md = MessageDigest.getInstance("SHA1");
    md.reset()
    byte[] digest = md.digest(shaPassword.getBytes("UTF-8"))
    String h = new String(digest, Charset.forName("UTF-8"))


    password = user.password
    repassword = user.password
  }

  @Command
  public void uploadImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    UploadEvent uploadEvent = event as UploadEvent
    Image image = event.getTarget().getSpaceOwner().getFellow("targetImage") as Image
    AImage media = uploadEvent.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]

    uuid = imageService.saveImageInTemp(media.getStreamData(), "1", ext)
    imageService.resizeImage("${imageService.temp}${fileSeparator}${uuid}", "1", ".${ext}", 150I)
    image.setContent(new AImage("${imageService.temp}${fileSeparator}${uuid}${fileSeparator}1-150.${ext}"))
  }

  @Command
  public void save() {
    if (uuid != null) {
      File temp = new File("${imageService.temp}${fileSeparator}${uuid}")
      File store = new File("${imageService.userPictures}${fileSeparator}${loginService.getCurrentUser().id}")
      if (!store.exists())
        store.mkdirs()
      FileUtils.copyDirectory(temp, store)
    }

    UserEntity user = loginService.getCurrentUser()
    user.fio = fio
    user.phone = phone
    user.email = email
    user.address = address
    user.password = password.encodeAsSHA1()

    Executions.sendRedirect("/cabinet/index")

  }

}
