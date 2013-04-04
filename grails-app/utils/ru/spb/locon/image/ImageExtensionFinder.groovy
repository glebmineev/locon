package ru.spb.locon.image

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/4/13
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
class ImageExtensionFinder {

  public static String getImageExtension(String dir){
    String ext = ".jpg"
    File imageDir = new File(dir)
    imageDir.eachFile {it ->

      String[] arr = it.name.split("\\.")
      if ("1".equals(arr[0]))
        ext = arr[1]
    }
    return ext
  }

}
