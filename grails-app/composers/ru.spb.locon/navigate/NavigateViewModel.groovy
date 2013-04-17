package ru.spb.locon.navigate

import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import ru.spb.locon.CategoryEntity
import ru.spb.locon.common.PathHandler

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

  List<HrefObject> links = new LinkedList<HrefObject>()

  @Init
  public void init() {
    String uri = Executions.getCurrent().getAttribute("javax.servlet.forward.request_uri") as String

    String name = uris.get(uri.replace("/locon/shop/", ""))
    links.add(new HrefObject("Главная", "/shop"))
    links.add(new HrefObject("Каталог товаров", "/catalog"))


  }

  @NotifyChange(["links"])
  public void rebuildPath(Map<String, Object> params){
    List<CategoryEntity> categories = PathHandler.getCategoryPath(CategoryEntity.get(params.get("categoryID")))
    links.clear()
    links.add(new HrefObject("dasdas", "/shop"))
    links.add(new HrefObject("sdasda", "/catalog"))
  }

  List<String> getLinks() {
    return links
  }

  void setLinks(List<String> links) {
    this.links = links
  }
}
