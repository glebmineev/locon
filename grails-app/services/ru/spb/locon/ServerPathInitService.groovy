package ru.spb.locon

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.beans.factory.InitializingBean
import ru.spb.locon.common.StringUtils

/**
 * Бин отвечает за центролизованный доступ к путям до директорий приложения.
 */
class ServerPathInitService implements InitializingBean {

  static transactional = true

  String fileSeparator = System.getProperty("file.separator")

  @Override
  void afterPropertiesSet() throws Exception {
    StringUtils stringUtils = new StringUtils()
    String root = ApplicationHolder.application.mainContext.servletContext.getRealPath(fileSeparator)
    String twoLevelUp = stringUtils.buildPath(2, root)
    /**
     * Путь до каталога для выгрузки excel заказов.
     */
    String adminOrders = "${twoLevelUp}${fileSeparator}admin${fileSeparator}orders"
    checkPath(adminOrders)
  }

  /**
   * Проверяем есть ли указанный каталог, если нет создаем.
   * @param path - иерархия директорий.
   */
  void checkPath(String path) {
    File dir = new File(path)
    if (!dir.exists())
      dir.mkdirs()
  }

}
