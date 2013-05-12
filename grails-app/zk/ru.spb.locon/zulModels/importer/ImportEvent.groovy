package ru.spb.locon.zulModels.importer

import org.zkoss.zk.ui.event.Event

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 15:50
 */
class ImportEvent extends Event {

  enum STATES{
    START("/images/spinner.gif"),
    ERROR("images/failed.png"),
    SUCCESSFUL("images/success.gif")

    String id

    STATES(String id) {
      this.id = id
    }

    String getId() {
      return id
    }

  }

  STATES state
  String id
  String name
  String article
  List<String> errors = new ArrayList<String>()

  ImportEvent(String id, String name, String article) {
    super(name)
    this.id = id
    this.name = name
    this.article = article
  }

  void addErrorMessage(String error) {
    errors.add(error)
  }

  void addAllErrors(List<String> errors) {
    this.errors.addAll(errors)
  }

}
