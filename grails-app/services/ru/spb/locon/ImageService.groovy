package ru.spb.locon

import org.apache.commons.io.FileUtils
import ru.spb.locon.importer.ConverterRU_EN
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Graphics2D
import ru.spb.locon.importer.ImageHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Image
import ru.spb.locon.common.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ImageService {

  static scope = "prototype"

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImageService.class)

  ImageHandler dirUtils = new ImageHandler()
  StringUtils stringUtils = new StringUtils()
  String store
  String images

  ImageService() {
    String root = ApplicationHolder.application.mainContext.servletContext.getRealPath("/")
    images = "${root}\\images\\catalog"
    store = "${stringUtils.buildPath(2, root)}\\productImages"
  }

  void syncAllImagesWithServer() {
    ProductEntity.list().each {ProductEntity product ->
      String serverPath = ConverterRU_EN.translit("${images}\\${product.imagePath}").replace(" ", "")
      File src = new File("${store}\\${product.imagePath}")
      File dest = new File(serverPath)
      //копируем содержимое папки productImages в images на сервере.
      FileUtils.copyDirectory(src, dest)

      //делаем изображения разной величины.
      List<String> images = dirUtils.findImages(serverPath)
      images.each {String fileName ->
        if (!fileName.contains("-")) {
          String[] arr = fileName.split("\\.")
          resizeImage(serverPath, arr[0], arr[1])
        }
      }
    }
  }

  void syncWithServer(ProductEntity product) {
    //директория с томкатом.
    String serverPath = ConverterRU_EN.translit("${images}\\${product.imagePath}").replace(" ", "")
    //productImages.
    File src = new File("${store}\\${product.imagePath}")
    //locon/images.
    File dest = new File(serverPath)
    if (!dest.exists())
      dest.mkdirs()
    //копируем.
    FileUtils.copyDirectory(src, dest)

    List<String> images = dirUtils.findImages(serverPath)
    images.each {String fileName ->
      if (!fileName.contains("-")) {
        String[] arr = fileName.split("\\.")
        resizeImage(serverPath, arr[0], arr[1])
      }
    }
  }

  void resizeImage(String path, String fileName, String ext) {
    try {
      BufferedImage source = ImageIO.read(new File("${path}/${fileName}.${ext}"))

      writeImage(source, new File("${path}\\${fileName}-100.${ext}"), 100, ext)
      writeImage(source, new File("${path}\\${fileName}-228.${ext}"), 228, ext)
      writeImage(source, new File("${path}\\${fileName}-40.${ext}"), 40, ext)
      writeImage(source, new File("${path}\\${fileName}-500.${ext}"), 500, ext)
      writeImage(source, new File("${path}\\${fileName}-80.${ext}"), 80, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}/${fileName}.${ext}")
    }
  }

  void writeImage(BufferedImage source, File dest, int size, String ext) {

    int width = source.width
    int height = source.height

    float k = 1

    if (width > height)
      k = (width / size)
    else
      k = (height / size)

    int new_h = (height / k)
    int new_w = (width / k)

    BufferedImage image = resizeImage(source, new_w, new_h, source.getType())
    ImageIO.write(image, ext.toUpperCase(), dest)
  }

  BufferedImage resizeImage(BufferedImage originalImage, int ing_width, int img_height, int type) {
    BufferedImage resizedImage = new BufferedImage(ing_width, img_height, type)
    Graphics2D g = resizedImage.createGraphics()
    g.drawImage(originalImage, 0, 0, ing_width, img_height, null)
    g.dispose()
    return resizedImage
  }

  Image getProductImage(ProductEntity product, String size) {
    Image image = new Image()
    image.setHeight("${size}px")
    String imagePath = ConverterRU_EN.translit(product.imagePath).replace(" ", "")
    String applicationPath = Executions.current.nativeRequest.getSession().getServletContext().getRealPath("/")
    String path = "${applicationPath}\\images\\catalog\\${imagePath}"
    ImageHandler dirUtils = new ImageHandler()
    List<String> images = dirUtils.findImages(path)
    if (images.size() > 0)
      image.setSrc("/images/catalog/${imagePath}/1-${size}.jpg")
    else
      image.setSrc("/images/empty.png")
    return image
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

}
