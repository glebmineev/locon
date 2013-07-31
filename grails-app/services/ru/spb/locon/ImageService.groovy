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
  String categories
  String userPictures
  String temp

  String fileSeparator = System.getProperty("file.separator")

  ImageService() {
    String root = ApplicationHolder.application.mainContext.servletContext.getRealPath(fileSeparator)
    String twoLevelUp = stringUtils.buildPath(2, root)
    images = "${root}${fileSeparator}images"
    userPictures = "${twoLevelUp}${fileSeparator}userPics"
    store = "${twoLevelUp}${fileSeparator}productImages"
    manufacturers = "${twoLevelUp}${fileSeparator}manufacturers"
    categories = "${twoLevelUp}${fileSeparator}categories"
    temp = "${root}${fileSeparator}images${fileSeparator}temp"
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
    new File("${temp}${fileSeparator}${UUID}").mkdirs()
    File newFile = new File("${temp}${fileSeparator}${UUID}${fileSeparator}${fileName}.${ext}");
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
      BufferedImage source = ImageIO.read(new File("${path}${fileSeparator}${fileName}${ext}"))

      writeImage(source, new File("${path}${fileSeparator}${fileName}-${size}${ext}"), size, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}${fileSeparator}${fileName}${ext}")
    }
  }

  void batchResizeImage(String path, String fileName, String ext) {
    try {
      BufferedImage source = ImageIO.read(new File("${path}${fileSeparator}${fileName}${ext}"))

      writeImage(source, new File("${path}${fileSeparator}${fileName}-150${ext}"), 150, ext)
      writeImage(source, new File("${path}${fileSeparator}${fileName}-300${ext}"), 300, ext)
      writeImage(source, new File("${path}${fileSeparator}${fileName}-500${ext}"), 500, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}${fileSeparator}${fileName}${ext}")
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
    String ext = getImageExtension("${store}${fileSeparator}${imageDir}")
    File image = new File("${store}${fileSeparator}${imageDir}${fileSeparator}1-${size}${ext}")
    if (image.exists())
      aImage = new AImage(image.path)
    else
      aImage = new AImage("${images}${fileSeparator}empty.png")

    return aImage
  }

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

  boolean downloadImages(String from, String to) {
    boolean isDownloaded = false
    try {
      File dir = new File("${store}${fileSeparator}${to}")
      if (!dir.exists())
        dir.mkdirs()

      URL website = new URL(from)
      String type = fileType(from)
      File image = new File("${dir}${fileSeparator}1${type}")
      FileUtils.copyURLToFile(website, image)
      batchResizeImage("${store}${fileSeparator}${to}", "1", type)

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
