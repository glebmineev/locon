package ru.spb.locon.search

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zul.Textbox
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Executions

/**
 * User: Gleb
 * Date: 14.11.12
 * Time: 12:52
 */
class SearchComposer extends GrailsComposer {

  Textbox request
  Button sendRequest

  def afterCompose = {Window window ->

  }

  public void onClick_sendRequest(Event event){
    String sValue = request.value
    Executions.sendRedirect("/search/index?keyword=${sValue}")
  }

}
