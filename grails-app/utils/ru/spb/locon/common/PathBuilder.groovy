package ru.spb.locon.common

/**
 * Класс строитель путей до директорий.
 */
class PathBuilder {

  String result = ""

  String fileSeparator = System.getProperty("file.separator")

  /**
   * Добавляет строку с разделителем.
   * @param path - строка.
   * @return строитель
   */
  public PathBuilder appendPath(String path) {
    result = result.concat("${path}${fileSeparator}")
    return this
  }

  /**
   * Добавляет строку
   * @param value - строка
   * @return - строитель
   */
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

  /**
   * Проверяет есть ли такая директория на диске, если нет, то создает ее.
   */
  public PathBuilder checkDir(){
    File dir = new File(result)
    if (!dir.exists())
      dir.mkdirs()
    return this
  }

  /**
   * Выодит строку результат построения.
   * @return строку.
   */
  public String build(){
    return result
  }

}
