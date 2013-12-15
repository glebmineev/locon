package ru.spb.locon.zulModels.admin.common

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.image.AImage
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zul.Image
import ru.spb.locon.ImageService
import ru.spb.locon.ServerFoldersService
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.image.ImageUtils

/**
 * Класс для работы с загрузкой изображений.
 */
public abstract class DownloadImageViewModel {

  protected String uuid
  protected String std_name
  protected static int std_image_size = STD_IMAGE_SIZES.SMALL.getSize()
  protected static String targetImage = "targetImage"

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService
  ServerFoldersService serverFoldersService =
    ApplicationHolder.getApplication().getMainContext().getBean("serverFoldersService") as ServerFoldersService

  @Init
  public void init() {
    downloadParams()
    initialize()
  }

  public abstract void downloadParams()

  public abstract void initialize()


  /**
   * Загрузка изображения в темповую дерикторию сервера.
   * @param event
   */
  @Command
  public void uploadImage(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    UploadEvent uploadEvent = event as UploadEvent
    Image image = event.getTarget().getSpaceOwner().getFellow(targetImage) as Image
    AImage media = uploadEvent.getMedia() as AImage

    String fullFileName = media.getName()
    String ext = fullFileName.split("\\.")[1]

    uuid = imageService.saveImageInTemp(media.getStreamData(), std_name, ext)
    ImageUtils.resizeImage(new PathBuilder().appendPath(serverFoldersService.temp).appendString(uuid).build(), std_name, ext, std_image_size)
    ImageUtils.tripleResizeImage(new PathBuilder().appendPath(serverFoldersService.temp).appendString(uuid).build(), std_name, ext)
    image.setContent(new AImage(new PathBuilder()
        .appendPath(serverFoldersService.temp)
        .appendPath(uuid)
        .appendString("${std_name}-${std_image_size}")
        .appendExt(ext).build()))
  }

}
