package ru.spb.locon.zulModels.admin.windows

import com.google.common.base.Function
import com.google.common.collect.Collections2
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import ru.spb.locon.LoginService
import ru.spb.locon.RoleEntity
import ru.spb.locon.UserEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.wrappers.RoleWrapper
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

@Init(superclass=true)
class NewUserWndViewModel extends DownloadImageViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ManufacturerWndViewModel.class)

  String login
  String password
  String repassword

  String fio
  String phone
  String email
  String address

  boolean isActive = false
  ListModelList<RoleWrapper> roles

  LoginService loginService =
    ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.USER_NAME.getName()
    std_image_size = STD_IMAGE_SIZES.MIDDLE.getSize()
    targetImage = "targetImage"
  }

  @Override
  void initialize() {
    List<RoleWrapper> allRoles = Collections2.transform(RoleEntity.list(), new Function<RoleEntity, RoleWrapper>() {
      @Override
      RoleWrapper apply(RoleEntity entity) {
        return new RoleWrapper(RoleEntity.get(entity.id))
      }
    }) as List<RoleWrapper>
    roles = new ListModelList<RoleWrapper>(allRoles)
    roles.setMultiple(true)
  }

  @Command
  public void saveUser() {

    UserEntity toSave = new UserEntity()
    toSave.setLogin(login)
    toSave.setPassword(password.encodeAsSHA1())
    toSave.setEmail(email)
    toSave.setAddress(address)
    toSave.setIsActive(isActive)

    UserEntity user = loginService.getCurrentUser()

    toSave.setActivateCode("created by::${user.login}")
    toSave.setFio(fio)
    toSave.setPhone(phone)

    roles.getSelection().each {it ->
      toSave.addToGroups(RoleEntity.get(it.id))
    }

    if (toSave.validate()) {
      toSave.save(flush: true)

      if (uuid != null) {
        File temp = new File(new PathBuilder()
            .appendPath(serverFoldersService.temp)
            .appendString(uuid)
            .build())

        File store = new File(new PathBuilder()
            .appendPath(serverFoldersService.userPics)
            .appendString(toSave.email)
            .build())
        if (!store.exists())
          store.mkdirs()

        FileUtils.copyDirectory(temp, store)
      }

    }
    Executions.sendRedirect("/admin/users")
  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Window wnd = event.getTarget().getSpaceOwner() as Window
    Window owner = wnd.getParent().getParent().getParent() as Window
    owner.detach()
  }


}
