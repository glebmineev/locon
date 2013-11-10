package ru.spb.locon

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.zkoss.image.AImage
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.image.ImageUtils
import org.slf4j.*
import ru.spb.locon.common.PathUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ImageService {

  static scope = "prototype"
  //Логгер
  static Logger log = LoggerFactory.getLogger(ImageService.class)

  ServerFoldersService serverFoldersService =
    ApplicationHolder.getApplication().getMainContext().getBean("serverFoldersService") as ServerFoldersService

  /**
   * Запись картинки в темповую директорию.
   * @param is - входной поток.
   * @param fileName - имя файла.
   * @param ext - расширение.
   * @return уникальный идентификатор папки в темповой директории.
   */
  String saveImageInTemp(InputStream is, String fileName, String ext) {
    String UUID = UUID.randomUUID()

    String fileDir = new PathBuilder()
        .appendPath(serverFoldersService.temp)
        .appendString(UUID)
        .checkDir()
        .build()

    File newFile = new File(new PathBuilder()
        .appendPath(fileDir)
        .appendString(fileName)
        .appendExt(ext)
        .build());

    boolean isCreate = newFile.createNewFile()
    if (isCreate)
      try {
        IOUtils.write(is.getBytes(), new FileOutputStream(newFile))
      } catch (IOException e) {
        log.error(e.getMessage())
      }

    return UUID

  }

  /**
   * Получаем картинку из хранилища.
   * @param path - путь до каталога в хранилище.
   * @param std_name - стандартное имя.
   * @param size - размер.
   * @return картинку.
   */
  AImage getImageFile(String path, String std_name, int size) {
    AImage aImage

    String ext = PathUtils.getImageExtension(path, std_name)
    File image = new File(new PathBuilder()
        .appendPath(path)
        .appendString("${std_name}-${size}")
        .appendExt(ext)
        .build())
    if (image.exists())
      aImage = new AImage(image.path)
    else
      aImage = new AImage(new PathBuilder()
                            .appendPath(serverFoldersService.images)
                            .appendString(STD_FILE_NAMES.EMPTY_IMAGE.getName())
                            .build())

    return aImage
  }

  /**
   * Загрузка изображения с сайта.
   * @param from - откуда загружать(путь до изображения на сайте).
   * @param to - куда ложить на сервере.
   * @return - //TODO:: заменить код ошибки на выбрасывание ошибки.
   */
  boolean downloadImages(String from, String to) {
    boolean isDownloaded = false
    try {

      String downloadDir = new PathBuilder()
          .appendPath(serverFoldersService.productImages)
          .appendString(to)
          .checkDir()
          .build()

      URL website = new URL(from)
      String ext = PathUtils.fileExt(from)
      File image = new File(new PathBuilder()
          .appendPath(downloadDir)
          .appendString(STD_FILE_NAMES.PRODUCT_NAME.getName())
          .appendExt(ext).build())

      FileUtils.copyURLToFile(website, image)
      ImageUtils.tripleResizeImage(downloadDir, STD_FILE_NAMES.PRODUCT_NAME.getName(), ext)

      isDownloaded = true

    } catch (Exception e) {
      log.error("Error download image from ${from}")
    }
    return isDownloaded
  }

  /**
   * Удаление из каталога productImages картинок с товаром.
   * @param product - товар картинки которого необходимоудалить.
   */
  void cleanStore(ProductEntity product) {
    File store = new File(new PathBuilder()
        .appendPath(serverFoldersService.productImages)
        .appendString(product.engImagePath)
        .build())

    if (store.exists()) {
      store.eachFile { it ->
        it.delete()
      }
      store.eachDir { it ->
        it.delete()
      }
      store.delete()
    }

  }

  /**
   * Удаление из каталога productImages картинок с товаром.
   * @param store - каталог товара.
   */
  void cleanStore(File store) {
    try {
      if (store.exists()) {
        store.eachFile { it ->
          it.delete()
        }
        store.eachDir { it ->
          it.delete()
        }
        store.delete()
      }
    } catch (Exception ex) {
      log.debug("Ошибка удаления из хранилища изображений: ${ex.getMessage()}")
    }

  }

}
