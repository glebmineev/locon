package ru.spb.locon.zulModels.importer

import org.zkoss.zul.Div
import org.zkoss.zul.Image
import org.zkoss.zul.Html
import org.zkoss.zul.Popup

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:36
 */
class ResultCell extends Div{

  Image start
  Image error
  Image successful
  
  ResultCell(String id) {
    setId(id)

    start = new Image("/images/spinner.gif")
    start.setVisible(true)
    appendChild(start)

    error = new Image("/images/failed.png")
    error.setVisible(false)
    appendChild(error)

    successful = new Image("/images/success.gif")
    successful.setVisible(false)
    appendChild(successful)

  }

  void changeState(ImportEvent event){

    if (event.errors.size() > 0) {
      start.setVisible(false)
      error.setVisible(true)
      
      Html html = new Html()
      String content = "<span>"
      event.errors.each {String error ->
        content += "${error}</br>"
      }
      content += "</span>"
      html.setContent(content)

      Popup popup = new Popup()
      popup.appendChild(html)
      appendChild(popup)

      error.setTooltip(popup)

    } else {
      start.setVisible(false)
      successful.setVisible(true)
    }

  }

}
