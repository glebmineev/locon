package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import ru.spb.locon.InfoEntity
import org.zkoss.zul.Textbox
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event

/**
 * User: Gleb
 * Date: 19.11.12
 * Time: 17:20
 */
class InfoComposer extends GrailsComposer {

  Textbox contacts
  Textbox about
  Textbox delivery
  Textbox details

  Button save

  InfoEntity infoEntity

  def afterCompose = {Window window ->

    List<InfoEntity> list = InfoEntity.list()
    if (list != null && list.size() > 0) {
      infoEntity = list.first()
      contacts.setValue(infoEntity.contacts)
      delivery.setValue(infoEntity.delivery)
      details.setValue(infoEntity.details)
      about.setValue(infoEntity.about)
    }

  }

  public void onClick_save(Event event) {
    if (infoEntity == null)
      infoEntity = new InfoEntity()
    InfoEntity.withTransaction {
      infoEntity.contacts = contacts.value
      infoEntity.delivery = delivery.value
      infoEntity.details = details.value
      infoEntity.about = about.value
      infoEntity.save(flush: true)
    }
  }

}
