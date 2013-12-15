package ru.spb.locon.zulModels.cabinet

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import ru.spb.locon.LoginService
import ru.spb.locon.UserEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

@Init(superclass=true)
class ProfileViewModel extends DownloadImageViewModel {

  String fio
  String phone
  String email
  String address

  LoginService loginService = ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.PRODUCT_NAME.getName()
    std_image_size = STD_IMAGE_SIZES.MIDDLE.getSize()
    targetImage = "targetImage"
  }

  @Override
  void initialize() {
    UserEntity user = loginService.getCurrentUser()
    fio = user.fio
    phone = user.phone
    email = user.email
    address = user.address
  }

  @Command
  public void save() {
    UserEntity user = loginService.getCurrentUser()
    if (uuid != null) {
      File temp = new File(new PathBuilder()
          .appendPath(serverFoldersService.temp)
          .appendString(uuid)
          .build())

      File store = new File(new PathBuilder()
          .appendPath(serverFoldersService.userPics)
          .appendString(user.email)
          .build())
      if (!store.exists())
        store.mkdirs()
      FileUtils.copyDirectory(temp, store)
    }

    user.fio = fio
    user.phone = phone
    user.email = email
    user.address = address

    Executions.sendRedirect("/cabinet/index")

  }

}
