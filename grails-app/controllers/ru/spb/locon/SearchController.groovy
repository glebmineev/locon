package ru.spb.locon

import org.compass.core.engine.SearchEngineQueryParseException

class SearchController {

  def searchableService
  def initService

  def index() {
    if (!params.query?.trim()) {
      return [mainCategoties: initService.categories]
    }

    try {
      return [searchResult: searchableService.search(params.query, params), mainCategoties: initService.categories]
    } catch (SearchEngineQueryParseException ex) {
      return [parseException: true, mainCategoties: initService.categories]
    }

  }

  def indexAll = {
    Thread.start {
      searchableService.index()
    }
    render("Индексирование данных запущено в фоновом режиме.")
  }

  def unindexAll = {
    searchableService.unindex()
    render("Индексирование удалено.")
  }

}
