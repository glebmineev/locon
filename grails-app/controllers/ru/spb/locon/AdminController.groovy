package ru.spb.locon

import ru.spb.locon.login.URLUtils
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AdminController {

  def loginService
  def imageSyncService

  def index() { }

  def editor() { }

  def orders() { }

  def orderItem() { }

  def importCatalog() { }

  def sync() {
    imageSyncService.syncWithServer(session.getServletContext().getRealPath("/"))
  }

  def beforeInterceptor = {
    List<String> groups = loginService.getUserGroups()
    if (!groups.contains("MANAGER")) {
      loginService.setParams(params)
      redirect(url: URLUtils.getHostUrl(request) + createLink(controller: "auth", action: "login"))
      return
    }
  }
}
