package importer

import java.math.RoundingMode

/**
 * User: Gleb
 * Date: 15.09.12
 * Time: 23:39
 */
class ConvertUtils {

  public static String formatString(String source){
    String result = source.trim()
    return result
  }

  public static float roundFloat(Float value){
    return new BigDecimal(value).setScale(1, RoundingMode.UP).floatValue()
  }

}
