package ru.spb.locon

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.io.FileUtils
import ru.spb.locon.importer.ConverterRU_EN
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Graphics2D
import ru.spb.locon.importer.ImageHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImageSyncService {

  static scope = "prototype"

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImageSyncService.class)

  ImageHandler dirUtils = new ImageHandler()
  String catalogPath = ConfigurationHolder.config.locon.store.catalog

  void syncWithServer(String applicationPath) {
    ProductEntity.list().each {ProductEntity product ->
      String storePath = "${catalogPath}\\${product.imagePath}"
      String serverPath = ConverterRU_EN.translit("${applicationPath}\\images\\catalog\\${product.imagePath}")
      File src = new File(storePath)
      File dest = new File(serverPath)
      FileUtils.copyDirectory(src, dest)

      List<String> images = dirUtils.findImages(serverPath)
      images.each {String fileName ->
        if (!fileName.contains("-")){
          String[] arr = fileName.split("\\.")
          resizeImage(serverPath, arr[0], arr[1])
        }
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
      log.error("Неправильное расширение ${path}/${fileName}.${ext}")
    }
  }

  void writeImage(BufferedImage source, File dest, int size, String ext){
    BufferedImage image40x40 = resizeImage(source, size, size, source.getType())
    ImageIO.write(image40x40, ext.toUpperCase(), dest)
  }

  BufferedImage resizeImage(BufferedImage originalImage, int ing_width, int img_height, int type) {
    BufferedImage resizedImage = new BufferedImage(ing_width, img_height, type)
    Graphics2D g = resizedImage.createGraphics()
    g.drawImage(originalImage, 0, 0, ing_width, img_height, null)
    g.dispose()
    return resizedImage
  }


}
