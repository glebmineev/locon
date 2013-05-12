package ru.spb.locon.zulModels.admin

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.Image
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import ru.spb.locon.ImageService
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.wrappers.ManufacturerModel
import ru.spb.locon.wrappers.ProductModel

class ManufacturersViewModel {

  String uuid

  ListModelList<ManufacturerModel> manufacturersModel

  ImageService imageService =
    ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init() {
    List<ManufacturerModel> models = new ArrayList<ManufacturerModel>()
    ManufacturerEntity.list(sort: "name").each { it ->
      ManufacturerModel model = new ManufacturerModel(it)
      model.setMemento(model.clone())
      models.add(model)
    }

    manufacturersModel = new ListModelList<ManufacturerModel>(models)
  }

  @Command
  public void uploadLogo(@BindingParam("model") ManufacturerModel model,
                         @BindingParam("inputEvent") UploadEvent event) {
    Image image = event.getTarget().getSpaceOwner().getFellow("${model.id}") as Image
    AImage media = event.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]

    imageService.cleanStore(new File("${imageService.manufacturers}\\${model.id}"))
    uuid = imageService.saveImageInTemp(media.getStreamData(), "1", ext)
    imageService.resizeImage("${imageService.temp}\\${uuid}", "1", ".${ext}", 80I)
    image.setContent(new AImage("${imageService.temp}\\${uuid}\\1-80.${ext}"))
  }

  @Command
  public void changeEditableStatus(@BindingParam("model") ManufacturerModel wrapper) {
    wrapper.setEditingStatus(!wrapper.getEditingStatus())
    refreshRowTemplate(wrapper)
  }

  @Command
  public void refreshRowTemplate(ManufacturerModel wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "editingStatus");
  }

  @Command
  public void updateProduct(@BindingParam("model") ManufacturerModel wrapper) {

    ManufacturerEntity.withTransaction {
      ManufacturerEntity toSave = ManufacturerEntity.get(wrapper.id)
      toSave.setName(wrapper.getName())
      toSave.setShortName(wrapper.shortName)
      toSave.setDescription(wrapper.description)
      if (toSave.validate()) {
        toSave.save(flush: true)

        if (uuid != null) {
          File temp = new File("${imageService.temp}\\${uuid}")
          File store = new File("${imageService.manufacturers}\\${wrapper.id}")
          if (!store.exists())
            store.mkdirs()

          FileUtils.copyDirectory(temp, store)
        }

      }
    }

    changeEditableStatus(wrapper)
  }

  @Command
  @NotifyChange(["manufacturersModel"])
  public void deleteProduct(@BindingParam("model") ManufacturerModel wrapper) {

    ManufacturerEntity.withTransaction {
      ManufacturerEntity toDelete = ManufacturerEntity.get(wrapper.id)
      toDelete.delete(flush: true)
    }

    imageService.cleanStore(new File("${imageService.manufacturers}\\${wrapper.id}"))

    List<ManufacturerModel> models = new ArrayList<ManufacturerModel>()
    ManufacturerEntity.list(sort: "name").each { it ->
      ManufacturerModel model = new ManufacturerModel(it)
      model.setMemento(model.clone() as ManufacturerModel)
      models.add(model)
    }

    manufacturersModel.clear()
    manufacturersModel.addAll(models)

  }

  @Command
  public void cancelEditing(@BindingParam("model") ManufacturerModel wrapper) {
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
