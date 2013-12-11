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
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import ru.spb.locon.RoleEntity
import ru.spb.locon.UserEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.wrappers.RoleWrapper
import ru.spb.locon.wrappers.UserWrapper
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

class UsersViewModel extends DownloadImageViewModel {

  ListModelList<UserWrapper> usersModel

  @Init
  public void init(){
    configureInit()
  }

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

    UserEntity.withTransaction {
      UserEntity toSave = UserEntity.get(wrapper.id)
      toSave.setLogin(wrapper.getLogin())
      if (!Strings.isNullOrEmpty(wrapper.getPassword()))
        toSave.setPassword(wrapper.getPassword().encodeAsSHA1())
      toSave.setAddress(wrapper.getAddress())
      toSave.setFio(wrapper.getFio())
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

        if (uuid != null) {
          File temp = new File(new PathBuilder()
              .appendPath(serverFoldersService.temp)
              .appendString(uuid)
              .build())

          File store = new File(new PathBuilder()
              .appendPath(serverFoldersService.userPics)
              .appendString(wrapper.email)
              .build())
          if (!store.exists())
            store.mkdirs()

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
      toDelete.delete(flush: true)
    }

    imageService.cleanStore(new File(new PathBuilder()
        .appendPath(serverFoldersService.userPics)
        .appendString(wrapper.name)
        .build()))

    List<UserWrapper> models = new ArrayList<UserWrapper>()
    UserEntity.list(sort: "name").each { it ->
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
    Map<Object, Object> params = new HashMap<Object, Object>()
    Window wnd = Executions.createComponents("/zul/admin/windows/createNewUserWnd.zul", null, params) as Window
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)
  }
}
