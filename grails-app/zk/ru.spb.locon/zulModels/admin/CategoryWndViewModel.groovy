package ru.spb.locon.zulModels.admin

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zul.Image
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ImageService

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/11/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
class CategoryWndViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(CategoryWndViewModel.class)

  String name
  String parent
  Long parentID

  String uuid

  ImageService imageService =
    ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init() {
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    parentID = arg.get("parentID") as Long
    if (parentID != null)
      this.parent = CategoryEntity.get(parentID).name
    else
      this.parent = "Корневая категория"
  }

  @Command
  public void uploadImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    UploadEvent uploadEvent = event as UploadEvent
    Image image = event.getTarget().getSpaceOwner().getFellow("targetImage") as Image
    AImage media = uploadEvent.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]

    uuid = imageService.saveImageInTemp(media.getStreamData(), "1", ext)
    imageService.resizeImage("${imageService.temp}\\${uuid}", "1", ".${ext}", 80I)
    image.setContent(new AImage("${imageService.temp}\\${uuid}\\1-80.${ext}"))
  }

  @Command
  public void addCategory(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    CategoryEntity.withTransaction {
      CategoryEntity categoryEntity = new CategoryEntity()
      categoryEntity.setName(name)
      if (parent != null)
        categoryEntity.setParentCategory(CategoryEntity.get(parentID))

      if (categoryEntity.validate()) {
        categoryEntity.save(flush: true)

        if (uuid != null) {
          File temp = new File("${imageService.temp}\\${uuid}")
          File store = new File("${imageService.manufacturers}\\${categoryEntity.id}")
          if (!store.exists())
            store.mkdirs()

          FileUtils.copyDirectory(temp, store)
        }

      }

      Executions.sendRedirect("/admin/editor?categoryID=${categoryEntity.id}")

    }

  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    try {
      Window wnd = event.getTarget().getSpaceOwner() as Window
      Window owner = wnd as Window
      owner.detach()
    } catch (Exception ex) {
      log.debug(ex.getMessage())
    }
  }

}
