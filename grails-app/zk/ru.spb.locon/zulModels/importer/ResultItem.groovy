package ru.spb.locon.zulModels.importer

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:30
 */
class ResultItem {
  
  String id
  String name
  String article

  ResultItem(ImportEvent importEvent) {
    this.id = importEvent.id
    this.article = importEvent.article
    this.name = importEvent.name
  }

}
