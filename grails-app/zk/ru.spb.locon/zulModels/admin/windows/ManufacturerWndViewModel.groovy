package ru.spb.locon.zulModels.admin.windows

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Window
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

class ManufacturerWndViewModel extends DownloadImageViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ManufacturerWndViewModel.class)

  String name
  String shortName
  String description

  @Init(superclass=true)
  public void initMWnd(){ }

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.PRODUCT_NAME.getName()
    std_image_size = STD_IMAGE_SIZES.MIDDLE.getSize()
    targetImage = "targetImage"
  }

  @Override
  void initialize() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Command
  public void saveManufacturer(){
    ManufacturerEntity.withTransaction {
      ManufacturerEntity toSave = new ManufacturerEntity()
      toSave.setName(name)
      toSave.setShortName(shortName)
      toSave.setDescription(description)
      if (toSave.validate()) {
        toSave.save(flush: true)

        if (uuid != null) {
          File temp = new File(new PathBuilder()
              .appendPath(serverFoldersService.temp)
              .appendString(uuid)
              .build())
          File store = new File(new PathBuilder()
              .appendPath(serverFoldersService.manufacturersPics)
              .appendString(toSave.name)
              .build())
          if (!store.exists())
            store.mkdirs()

          FileUtils.copyDirectory(temp, store)
        }

      }
    }
    Executions.sendRedirect("/admin/manufacturers")
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
