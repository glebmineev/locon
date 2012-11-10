package ru.spb.locon.importer

/**
 * User: Gleb
 * Date: 10.11.12
 * Time: 15:09
 */
class DirUtils {

  public List<String> findImages(String path) {
    List<String> images = new ArrayList<String>();
    File dir = new File(path);
    if (dir.exists()) {

      int y = 0

      images.addAll(dir.list() as List<String>)
    }

    return images
  }

}
