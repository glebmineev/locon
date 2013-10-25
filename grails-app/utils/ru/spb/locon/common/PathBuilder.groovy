package ru.spb.locon.common

/**
 * Класс строитель путей до директорий.
 */
class PathBuilder {

  String result = ""

  String fileSeparator = System.getProperty("file.separator")

  /**
   * Добавляет частицу пити.
   * @param path - строка с частицей пути.
   * @return строитель
   */
  public PathBuilder appendPath(String path) {
    result = result.concat("${path}${fileSeparator}")
    return this
  }

  public PathBuilder appendString(String value) {
    result = result.concat(value)
    return this
  }

  /**
   * Добавляет расширение.
   * @param ext - необходимое расширение.
   * @return строитель
   */
  public appendExt(String ext) {
    result = result.concat(".${ext}")
    return this
  }

  public void checkDir(){
    File dir = new File(result)
    if (!dir.exists())
      dir.mkdirs()
  }

  /**
   * Выодит строку результат построения.
   * @return строку.
   */
  public String build(){
    return result
  }

}
