package ru.spb.locon.importer

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:17
 */
class DownloadUtils {

  //Логгер
  static Logger log = LoggerFactory.getLogger(DownloadUtils.class)

  boolean downloadImages(String from, String to) {

    boolean isDownloaded = false

    try {
      File dir = new File(to)
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
