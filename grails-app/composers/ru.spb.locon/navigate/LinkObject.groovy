package ru.spb.locon.navigate

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/8/13
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
class LinkObject {

  String name
  String href

  LinkObject(String name, String href) {
    this.name = name
    this.href = href
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
  }

  String getHref() {
    return href
  }

  void setHref(String link) {
    this.href = link
  }
}
