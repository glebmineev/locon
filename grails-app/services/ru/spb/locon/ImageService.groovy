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

  PathUtils stringUtils = new PathUtils()
  String store
  String images
  String manufacturers
  String categories
  String userPictures
  String temp

  String fileSeparator = System.getProperty("file.separator")

  ServerFoldersService serverFoldersService =
    ApplicationHolder.getApplication().getMainContext().getBean("serverFoldersService") as ServerFoldersService

  ImageService() {
    images = serverFoldersService.images
    userPictures = serverFoldersService.userPics
    store = serverFoldersService.productImages
    manufacturers = serverFoldersService.manufacturersPics
    categories = serverFoldersService.categoriesPics
    temp = serverFoldersService.temp
  }

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
   * Запись пользовательской картинки.
   * @param is - поток с каритнкой.
   * @param id - йд пользователя.
   * @param fileName - имя файла.
   * @param ext - расширение файла.
   */
  @Deprecated
  void saveUserPic(InputStream is, Long id,String fileName, String ext) {
    new File("${userPictures}${fileSeparator}${id}").mkdirs()
    File newFile = new File("${userPictures}${fileSeparator}${id}\\${fileName}${ext}");
    boolean isCreate = newFile.createNewFile()
    if (isCreate) {
      OutputStream out = new FileOutputStream(newFile);
      byte[] buf = new byte[1024];
      int len;
      while ((len = is.read(buf)) > 0)
        out.write(buf, 0, len);
      out.close();
      is.close();
    }

  }

  /**
   * Получение картинки производителя из хранилища.
   * @param manufacturer - производитель.
   * @return - объект картинки.
   */
  @Deprecated
  AImage getManufIcon(ManufacturerEntity manufacturer) {
    File picture = new File("${manufacturers}${fileSeparator}${manufacturer.id}")

    String ext = getImageExtension("${manufacturers}${fileSeparator}${manufacturer.id}")

    AImage aImage
    if (picture.exists())
      aImage = new AImage("${manufacturers}${fileSeparator}${manufacturer.id}${fileSeparator}1-100${ext}")
    else
      aImage = new AImage("${images}${fileSeparator}help.png")

    return aImage
  }

  @Deprecated
  AImage getCategoryImage(CategoryEntity category) {
    File picture = new File("${categories}${fileSeparator}${category.id}")

    String ext = getImageExtension("${categories}${fileSeparator}${category.id}")

    AImage aImage
    if (picture.exists())
      aImage = new AImage("${categories}${fileSeparator}${category.id}${fileSeparator}1-150${ext}")
    else
      aImage = new AImage("${images}${fileSeparator}help.png")

    return aImage
  }

  @Deprecated
  void resizeImage(String path, String fileName, String ext, int size) {
    ImageUtils.resizeImage(path, fileName, ext, size)
  }

  @Deprecated
  AImage getImageFile(ProductEntity product, String size) {
    AImage aImage
    String imageDir = product.engImagePath;
    String ext = getImageExtension("${store}${fileSeparator}${imageDir}")
    File image = new File("${store}${fileSeparator}${imageDir}${fileSeparator}1-${size}${ext}")
    if (image.exists())
      aImage = new AImage(image.path)
    else
      aImage = new AImage("${images}${fileSeparator}empty.png")

    return aImage
  }

  @Deprecated
  AImage getUserPicture(UserEntity user) {
    File picture = new File("${userPictures}${fileSeparator}${user.id}")

    String ext = getImageExtension("${userPictures}${fileSeparator}${user.id}")

    AImage aImage
    if (picture.exists())
      aImage = new AImage("${userPictures}${fileSeparator}${user.id}${fileSeparator}1-150${ext}")
    else
      aImage = new AImage("${images}${fileSeparator}help.png")

    return aImage
  }

  @Deprecated
  public String getImageExtension(String dir) {
    String ext = ".jpg"
    File imageDir = new File(dir)
    if (imageDir.exists())
      imageDir.eachFile { it ->

        String[] arr = it.name.split("${fileSeparator}.")
        if ("1".equals(arr[0]))
          ext = ".${arr[1]}"
      }
    return ext
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
    File store = new File("${store}${fileSeparator}${product.engImagePath}")

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
