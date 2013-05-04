package ru.spb.locon

import org.apache.commons.io.FileUtils
import org.zkoss.image.AImage
import ru.spb.locon.image.ImageExtensionFinder
import ru.spb.locon.importer.ConverterRU_EN
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import ru.spb.locon.importer.ImageHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.zul.Image
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
  String userPictures
  String temp

  ImageService() {
    String root = ApplicationHolder.application.mainContext.servletContext.getRealPath("/")
    images = "${root}\\images"
    userPictures = "${stringUtils.buildPath(2, root)}\\userPics"
    store = "${stringUtils.buildPath(2, root)}\\productImages"
    temp = "${root}\\images\\temp"
  }

  void resizeImage(String path, String fileName, String ext) {
    try {
      BufferedImage source = ImageIO.read(new File("${path}/${fileName}${ext}"))

      writeImage(source, new File("${path}\\${fileName}-150${ext}"), 150, ext)
      writeImage(source, new File("${path}\\${fileName}-300${ext}"), 300, ext)
      writeImage(source, new File("${path}\\${fileName}-500${ext}"), 500, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки ${path}/${fileName}${ext}")
    }
  }

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

  void overwriteImage(InputStream is, ProductEntity product, String ext) {
    File newFile = new File("${store}\\${product.engImagePath}\\1.${ext}");
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

  public String getImageExtension(String dir) {
    String ext = ".jpg"
    File imageDir = new File(dir)
    if (imageDir.exists())
      imageDir.eachFile { it ->

        String[] arr = it.name.split("\\.")
        if ("1".equals(arr[0]))
          ext = arr[1]
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
      resizeImage("${store}/${to}", "1", type)

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

  AImage getUserPicture(UserEntity user) {
    File picture = new File("${userPictures}\\${user.imagePath}")

    AImage aImage
    if (picture.exists())
      aImage = new AImage(picture.path)
    else
      aImage = new AImage("${images}/help.png")

    return aImage
  }

}
