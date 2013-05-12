package ru.spb.locon

import org.apache.commons.io.FileUtils
import org.zkoss.image.AImage

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import org.slf4j.*
import ru.spb.locon.common.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.mortennobel.imagescaling.ResampleOp
import com.mortennobel.imagescaling.ResampleFilters
import com.mortennobel.imagescaling.AdvancedResizeOp
import javax.imageio.ImageWriter
import javax.imageio.ImageWriteParam
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.IIOImage

class ImageService {

  static scope = "prototype"

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImageService.class)

  StringUtils stringUtils = new StringUtils()
  String store
  String images
  String manufacturers
  String userPictures
  String temp

  ImageService() {
    String root = ApplicationHolder.application.mainContext.servletContext.getRealPath("/")
    String twoLevelUp = stringUtils.buildPath(2, root)
    images = "${root}\\images"
    userPictures = "${twoLevelUp}\\userPics"
    store = "${twoLevelUp}\\productImages"
    manufacturers = "${twoLevelUp}\\manufacturers"
    temp = "${root}\\images\\temp"
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
    new File("${temp}\\${UUID}").mkdirs()
    File newFile = new File("${temp}\\${UUID}\\${fileName}.${ext}");
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

    return UUID

  }

  /**
   * Запись пользовательской картинки.
   * @param is - поток с каритнкой.
   * @param id - йд пользователя.
   * @param fileName - имя файла.
   * @param ext - расширение файла.
   */
  void saveUserPic(InputStream is, Long id,String fileName, String ext) {
    new File("${userPictures}\\${id}").mkdirs()
    File newFile = new File("${userPictures}\\${id}\\${fileName}.${ext}");
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
   * Запись логотипа производителя.
   * @param is - поток с каритнкой.
   * @param id - йд пользователя.
   * @param fileName - имя файла.
   * @param ext - расширение файла.
   */
  void saveManufPic(InputStream is, Long id, String fileName, String ext) {
    new File("${manufacturers}\\${id}").mkdirs()
    File newFile = new File("${manufacturers}\\${id}\\${fileName}.${ext}")
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

  AImage getManufIcon(ManufacturerEntity manufacturer) {
    File picture = new File("${manufacturers}\\${manufacturer.id}")

    String ext = getImageExtension("${manufacturers}\\${manufacturer.id}")

    AImage aImage
    if (picture.exists())
      aImage = new AImage("${manufacturers}\\${manufacturer.id}\\1-80${ext}")
    else
      aImage = new AImage("${images}/help.png")

    return aImage
  }

  void writeImage(BufferedImage source, File dest, int size, String ext) {

    int width = source.width
    int height = source.height

    float scale

    if (width > height)
      scale = (size / width)
    else
      scale = (size / height)

    int new_h = (height * scale)
    int new_w = (width * scale)

    ResampleOp resampleOp = new ResampleOp(new_w, new_h)
    resampleOp.setFilter(ResampleFilters.getLanczos3Filter())
    resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal)
    BufferedImage destImage = resampleOp.filter(source, null)
    writeJpeg(destImage, dest, 1)

  }

  void resizeImage(String path, String fileName, String ext, int size) {
    try {
      BufferedImage source = ImageIO.read(new File("${path}/${fileName}${ext}"))

      writeImage(source, new File("${path}\\${fileName}-80${ext}"), size, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}/${fileName}${ext}")
    }
  }

  void batchResizeImage(String path, String fileName, String ext) {
    try {
      BufferedImage source = ImageIO.read(new File("${path}/${fileName}${ext}"))

      writeImage(source, new File("${path}\\${fileName}-150${ext}"), 150, ext)
      writeImage(source, new File("${path}\\${fileName}-300${ext}"), 300, ext)
      writeImage(source, new File("${path}\\${fileName}-500${ext}"), 500, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}/${fileName}${ext}")
    }
  }

  void writeJpeg(BufferedImage image, File destFile, float quality) throws IOException {
    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    ImageWriteParam param = writer.getDefaultWriteParam()
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
    param.setCompressionQuality(quality)
    FileImageOutputStream output = new FileImageOutputStream(destFile)
    writer.setOutput(output)
    IIOImage iioImage = new IIOImage(image, null, null)
    writer.write(null, iioImage, param)
    writer.dispose()
  }

  AImage getImageFile(ProductEntity product, String size) {
    AImage aImage
    String imageDir = product.engImagePath;
    String ext = getImageExtension("${store}/${imageDir}")
    File image = new File("${store}/${imageDir}/1-${size}.${ext}")
    if (image.exists())
      aImage = new AImage(image.path)
    else
      aImage = new AImage("${images}/empty.png")

    return aImage
  }

  AImage getUserPicture(UserEntity user) {
    File picture = new File("${userPictures}\\${user.id}")

    String ext = getImageExtension("${userPictures}\\${user.id}")

    AImage aImage
    if (picture.exists())
      aImage = new AImage("${userPictures}\\${user.id}\\1-150.${ext}")
    else
      aImage = new AImage("${images}/help.png")

    return aImage
  }

  public String getImageExtension(String dir) {
    String ext = ".jpg"
    File imageDir = new File(dir)
    if (imageDir.exists())
      imageDir.eachFile { it ->

        String[] arr = it.name.split("\\.")
        if ("1".equals(arr[0]))
          ext = ".${arr[1]}"
      }
    return ext
  }

  boolean downloadImages(String from, String to) {
    boolean isDownloaded = false
    try {
      File dir = new File("${store}\\${to}")
      if (!dir.exists())
        dir.mkdirs()

      URL website = new URL(from)
      String type = fileType(from)
      File image = new File("${dir}/1${type}")
      FileUtils.copyURLToFile(website, image)
      batchResizeImage("${store}/${to}", "1", type)

      isDownloaded = true

    } catch (Exception e) {
      log.error("Error download image from ${from}")
    }
    return isDownloaded
  }

  String fileType(String url) {
    int lastPoint = url.lastIndexOf(".")
    return url.substring(lastPoint)
  }

  void cleanStore(ProductEntity product) {
    File store = new File("${store}\\${product.engImagePath}")

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
