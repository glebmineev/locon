package ru.spb.locon.image

import com.mortennobel.imagescaling.AdvancedResizeOp
import com.mortennobel.imagescaling.ResampleFilters
import com.mortennobel.imagescaling.ResampleOp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_IMAGE_SIZES

import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.FileImageOutputStream
import java.awt.image.BufferedImage

/**
 * Утилита для работы с изображениями.
 */
class ImageUtils {

  //Логгер
  static Logger log = LoggerFactory.getLogger(ImageUtils.class)

  /**
   * размеры рисунков.
   */
  private static final int small = STD_IMAGE_SIZES.SMALL.getSize()
  private static final int middle = STD_IMAGE_SIZES.MIDDLE.getSize()
  private static final int large = STD_IMAGE_SIZES.LARGE.getSize()

  /**
   * Загрузка картинки на сервер с нужными размерами.
   * @param source - исходник картинки.
   * @param dest - файл на сервере в который будет произведена запись картинки.
   * @param size - новые размеры картинки.
   * @param ext - расширение файла.
   */
  private static void writeImage(BufferedImage source, File dest, int size, String ext) {

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

  public static void resizeImage(String path, String fileName, String ext, int size) {
    try {
      BufferedImage source = ImageIO.read(new File(new PathBuilder()
          .appendPath(path)
          .appendString(fileName)
          .appendExt(ext).build()))

      writeImage(source, new File(new PathBuilder()
          .appendPath(path)
          .appendString("${fileName}-${size}")
          .appendExt(ext).build()), size, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки " + new PathBuilder()
          .appendPath(path)
          .appendString(fileName)
          .appendExt(ext).build())
    }
  }

  public static void tripleResizeImage(String path, String fileName, String ext) {
    try {
      BufferedImage source = ImageIO.read(new File(new PathBuilder()
          .appendPath(path)
          .appendString(fileName)
          .appendExt(ext).build()))

      writeImage(source, new File(new PathBuilder()
          .appendPath(path)
          .appendString("${fileName}-${small}")
          .appendExt(ext).build()), small, ext)

      writeImage(source, new File(new PathBuilder()
          .appendPath(path)
          .appendString("${fileName}-${middle}")
          .appendExt(ext).build()), middle, ext)

      writeImage(source, new File(new PathBuilder()
          .appendPath(path)
          .appendString("${fileName}-${large}")
          .appendExt(ext).build()), large, ext)

    } catch (IOException ex) {
      log.error("Ошибка обработки картинки " + new PathBuilder()
          .appendPath(path)
          .appendString("${fileName}")
          .appendExt(ext).build())
    }
  }

  private static void writeJpeg(BufferedImage image, File destFile, float quality) throws IOException {
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

}
