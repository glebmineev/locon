package ru.spb.locon.zulModels.admin

import com.google.common.base.Function
import com.google.common.base.Strings
import com.google.common.collect.Collections2
import org.apache.commons.io.FileUtils
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.Div
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Vlayout
import org.zkoss.zul.Window
import ru.spb.locon.RoleEntity
import ru.spb.locon.UserEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.wrappers.RoleWrapper
import ru.spb.locon.wrappers.UserWrapper
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

@Init(superclass=true)
class UsersViewModel extends DownloadImageViewModel {

  ListModelList<UserWrapper> usersModel

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.USER_NAME.getName()
    std_image_size = STD_IMAGE_SIZES.MIDDLE.getSize()
    targetImage = ""
  }

  @Override
  void initialize() {
    List<UserWrapper> models = new ArrayList<UserWrapper>()
    UserEntity.list(sort: "login").each { it ->
      UserWrapper model = new UserWrapper(it)
      model.setMemento(model.clone() as UserWrapper)
      models.add(model)
    }

    usersModel = new ListModelList<UserWrapper>(models)
  }

  @Command
  public void changeEditableStatus(@BindingParam("model") UserWrapper wrapper) {
    targetImage = wrapper.id
    wrapper.setEditingStatus(!wrapper.getEditingStatus())
    refreshRowTemplate(wrapper)
  }

  @Command
  public void refreshRowTemplate(UserWrapper wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "editingStatus");
  }

  @Command
  public void updateUser(@BindingParam("model") UserWrapper wrapper) {

    UserEntity toSave = UserEntity.get(wrapper.id)
    String oldEmail = toSave.getEmail()

    UserEntity.withTransaction {
      toSave.setLogin(wrapper.getLogin())
      if (!Strings.isNullOrEmpty(wrapper.getPassword()))
        toSave.setPassword(wrapper.getPassword().encodeAsSHA1())
      toSave.setAddress(wrapper.getAddress())
      toSave.setFio(wrapper.getFio())
      toSave.setEmail(wrapper.getEmail())
      toSave.setAddress(wrapper.getAddress())
      RoleEntity.list().each {role ->
        toSave.removeFromGroups(role)
      }

      if (toSave.validate())
        toSave.save(flush: true)

      //Тянем все группы.
      List<RoleEntity> allRoles = Collections2.transform(wrapper.roles.getSelection(), new Function<RoleWrapper, RoleEntity>() {
        @Override
        RoleEntity apply(RoleWrapper wr) {
          return RoleEntity.get(wr.id)
        }
      }) as List<RoleEntity>

      allRoles.each {role ->
        toSave.addToGroups(role)
      }

      if (toSave.validate()) {
        toSave.save(flush: true)

        File store = new File(new PathBuilder()
            .appendPath(serverFoldersService.userPics)
            .appendString(toSave.email)
            .build())

        if (!store.exists())
          store.mkdirs()

        //File oldPics = new File(new PathBuilder()
        //    .appendPath(serverFoldersService.userPics)
        //    .appendString(oldEmail)
        //    .build())
        //FileUtils.copyDirectory(oldPics, store)
        //imageService.cleanStore(oldPics)

        if (uuid != null) {
          File temp = new File(new PathBuilder()
              .appendPath(serverFoldersService.temp)
              .appendString(uuid)
              .build())
          FileUtils.copyDirectory(temp, store)
        }

      }
    }

    changeEditableStatus(wrapper)
  }

  @Command
  @NotifyChange(["usersModel"])
  public void deleteUser(@BindingParam("model") UserWrapper wrapper) {

    UserEntity.withTransaction {
      UserEntity toDelete = UserEntity.get(wrapper.id)

      toDelete.groups.each {role ->
        RoleEntity retrived = RoleEntity.get(role.id)
        retrived.removeFromUsers(UserEntity.get(toDelete.id))
        if (retrived.validate())
          retrived.save(flush: true)
      }

      toDelete.delete(flush: true)
    }

    imageService.cleanStore(new File(new PathBuilder()
        .appendPath(serverFoldersService.userPics)
        .appendString(wrapper.email)
        .build()))

    List<UserWrapper> models = new ArrayList<UserWrapper>()
    UserEntity.list(sort: "email").each { it ->
      UserWrapper model = new UserWrapper(it)
      model.setMemento(model.clone() as UserWrapper)
      models.add(model)
    }

    usersModel.clear()
    usersModel.addAll(models)

  }

  @Command
  public void cancelEditing(@BindingParam("model") UserWrapper wrapper) {
    wrapper.restore()
    changeEditableStatus(wrapper)
  }

  @Command
  public void createNew(){

    Window wnd = new Window()
    wnd.setWidth("80%")
    wnd.setHeight("500px")
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    Div div = new Div()
    div.setStyle("overflow: auto;")
    div.setWidth("100%")
    div.setHeight("500px")
    wnd.appendChild(div)
    Vlayout panel = new Vlayout()
    div.appendChild(panel)

    Map<Object, Object> params = new HashMap<Object, Object>()
    Executions.createComponents("/zul/admin/windows/createNewUserWnd.zul", panel, params) as Window
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)
  }
}
