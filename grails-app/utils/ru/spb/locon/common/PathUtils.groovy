package ru.spb.locon.common

/**
 * User: Gleb
 * Date: 14.11.12
 * Time: 18:01
 */
class PathUtils {

  static String fileSeparator = System.getProperty("file.separator")

  String getStringAt(String source, String at){
    int of = source.indexOf(at)
    return source.substring(0, of)
  }

  /**
   * Вырезает строку из исходной на определенное количество слешей назад.
   * @param slashCount
   * @param source
   * @return
   */
  String buildPath(int slashCount, String source) {
    String result = source
    (0 .. slashCount).each {i ->
      int of = result.lastIndexOf("\\")
      if (of != -1)
        result = result.substring(0, of)
    }
    return  result
  }

  /**
   * Ищем расширение в строке с именем файла.
   * @param url - строка с именем файла
   * @return - расширение.
   */
  public static String fileExt(String source) {
    int lastPoint = source.lastIndexOf(".")
    return source.substring(lastPoint)
  }

  /**
   * Ищем расширирение файла картинки.
   * @param url - строка с именем директории картинок товара
   * @return - расширение.
   */
  public static String getImageExtension(String dir, String std_name) {
    String ext = ".jpg"
    File imageDir = new File(dir)
    if (imageDir.exists())
      imageDir.eachFile { it ->

        String[] arr = it.name.split("${fileSeparator}.")
        if (std_name.equals(arr[0]))
          ext = "${arr[1]}"
      }
    return ext
  }

}
