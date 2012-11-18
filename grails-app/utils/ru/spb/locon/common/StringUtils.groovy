package ru.spb.locon.common

/**
 * User: Gleb
 * Date: 14.11.12
 * Time: 18:01
 */
class StringUtils {
  
  String getStringAt(String source, String at){
    int of = source.indexOf(at)
    return source.substring(0, of)
  }
  
  String buildPath(int slashCount, String source) {
    String result = source
    (0 .. slashCount).each {i ->
      int of = result.lastIndexOf("\\")
      if (of != -1)
        result = result.substring(0, of)
    }
    return  result
  }
  
}
