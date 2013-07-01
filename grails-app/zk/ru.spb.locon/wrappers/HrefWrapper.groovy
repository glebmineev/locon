package ru.spb.locon.wrappers

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/28/13
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
class HrefWrapper {

  String name
  String href

  HrefWrapper(String name, String href) {
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
