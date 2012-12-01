package ru.spb.locon

import org.apache.commons.io.FileUtils
import ru.spb.locon.importer.ConverterRU_EN
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import ru.spb.locon.importer.ImageHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.zul.Image
import ru.spb.locon.common.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import static org.imgscalr.Scalr.*
import org.imgscalr.Scalr
import java.awt.Graphics2D
import java.awt.Transparency
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
    File storeFolder = new File(store)
    if (storeFolder.exists()) {
      ProductEntity.list().each {ProductEntity product ->
        String serverPath = ConverterRU_EN.translit("${images}\\${product.imagePath}").replace(" ", "").replace("%", "")
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
  }

  void syncWithServer(ProductEntity product) {
    //директория с томкатом.
    String serverPath = ConverterRU_EN.translit("${images}\\${product.imagePath}").replace(" ", "").replace("%", "")
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

      writeImage(source, new File("${path}\\${fileName}-150.${ext}"), 150, ext)
      writeImage(source, new File("${path}\\${fileName}-300.${ext}"), 300, ext)
      writeImage(source, new File("${path}\\${fileName}-500.${ext}"), 500, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}/${fileName}.${ext}")
    }
  }

  void writeImage(BufferedImage source, File dest, int size, String ext) {

    int width = source.width
    int height = source.height

    float scale = 1

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

  void writeJpeg(BufferedImage image, File destFile, float quality)
  throws IOException {
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

  Image getProductImage(ProductEntity product, String size) {
    Image image = new Image()
    image.setHeight("${size}px")
    String imagePath = ConverterRU_EN.translit(product.imagePath).replace(" ", "").replace("%", "")
    String path = "${images}\\${imagePath}"
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
