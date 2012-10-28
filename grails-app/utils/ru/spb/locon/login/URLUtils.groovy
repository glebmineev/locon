package ru.spb.locon.login

import javax.servlet.http.HttpServletRequest

/**
 * User: Gleb
 * Date: 14.10.12
 * Time: 14:33
 */
class URLUtils {
  static getHostUrl(HttpServletRequest request) {
    return "${request.getScheme()}://${request.getServerName()}${request.getServerPort() == 80 ? "" : ":" + request.getServerPort()}"
  }
  
  static String buildURL(def params){
    StringBuilder result = new StringBuilder()
    result.append("/")

    String controller = params.get("controller")
    result.append(controller)
    result.append("/")

    String action = params.get("action") != null ? "${params.get("action")}?" : ""
    result.append(action)

    params.each {key, value ->
      if (!"controller".equals(key) &&
          !"action".equals(key)){
        if (!"null".equals(value))
          result.append("${key}=${value}&")
      }

    }
    return result.toString()
  }
  
}

