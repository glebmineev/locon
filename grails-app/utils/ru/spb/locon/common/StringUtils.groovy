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
  
}
