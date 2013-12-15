package ru.spb.locon.zulModels.admin

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
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.wrappers.ManufacturerWrapper
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

@Init(superclass=true)
class ManufacturersViewModel extends DownloadImageViewModel {

  ListModelList<ManufacturerWrapper> manufacturersModel

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.MANUFACTURER_NAME.getName()
    std_image_size = STD_IMAGE_SIZES.SMALLEST.getSize()
    targetImage = ""
  }

  @Override
  void initialize() {
    List<ManufacturerWrapper> models = new ArrayList<ManufacturerWrapper>()
    ManufacturerEntity.list(sort: "name").each { it ->
      ManufacturerWrapper model = new ManufacturerWrapper(it)
      model.setMemento(model.clone())
      models.add(model)
    }

    manufacturersModel = new ListModelList<ManufacturerWrapper>(models)
  }

  @Command
  public void changeEditableStatus(@BindingParam("model") ManufacturerWrapper wrapper) {
    targetImage = wrapper.id
    wrapper.setEditingStatus(!wrapper.getEditingStatus())
    refreshRowTemplate(wrapper)
  }

  @Command
  public void refreshRowTemplate(ManufacturerWrapper wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "editingStatus");
  }

  @Command
  public void updateManufacturer(@BindingParam("model") ManufacturerWrapper wrapper) {

    ManufacturerEntity.withTransaction {
      ManufacturerEntity toSave = ManufacturerEntity.get(wrapper.id)
      toSave.setName(wrapper.getName())
      toSave.setShortName(wrapper.shortName)
      toSave.setDescription(wrapper.description)
      if (toSave.validate()) {
        toSave.save(flush: true)

        if (uuid != null) {
          File temp = new File(new PathBuilder()
              .appendPath(serverFoldersService.temp)
              .appendString(uuid)
              .build())

          File store = new File(new PathBuilder()
              .appendPath(serverFoldersService.manufacturersPics)
              .appendString(wrapper.name)
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
  public void deleteManufacturer(@BindingParam("model") ManufacturerWrapper wrapper) {

    ManufacturerEntity.withTransaction {
      ManufacturerEntity toDelete = ManufacturerEntity.get(wrapper.id)
      toDelete.delete(flush: true)
    }

    imageService.cleanStore(new File(new PathBuilder()
        .appendPath(serverFoldersService.manufacturersPics)
        .appendString(wrapper.name)
        .build()))

    List<ManufacturerWrapper> models = new ArrayList<ManufacturerWrapper>()
    ManufacturerEntity.list(sort: "name").each { it ->
      ManufacturerWrapper model = new ManufacturerWrapper(it)
      model.setMemento(model.clone() as ManufacturerWrapper)
      models.add(model)
    }

    manufacturersModel.clear()
    manufacturersModel.addAll(models)

  }

  @Command
  public void cancelEditing(@BindingParam("model") ManufacturerWrapper wrapper) {
    wrapper.restore()
    changeEditableStatus(wrapper)
  }

  @Command
  public void createNew(){
    Map<Object, Object> params = new HashMap<Object, Object>()
    Window wnd = Executions.createComponents("/zul/admin/windows/manufacturerWnd.zul", null, params) as Window
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)
  }

}
