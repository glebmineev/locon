package ru.spb.locon.navigate

import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/8/13
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
class NavigateViewModel {

  static Map<String, String> uris = new HashMap<String, String>()

  static {
    uris.put("catalog", "Каталог товаров")
  }

  List<LinkObject> links = new LinkedList<LinkObject>()

  @Init
  public void init() {
    String uri = Executions.getCurrent().getAttribute("javax.servlet.forward.request_uri") as String

    String name = uris.get(uri.replace("/locon/shop/", ""))
    links.add(new LinkObject("Главная", "/shop"))
    links.add(new LinkObject(name, "/catalog"))

  }

  List<String> getLinks() {
    return links
  }

  void setLinks(List<String> links) {
    this.links = links
  }
}
