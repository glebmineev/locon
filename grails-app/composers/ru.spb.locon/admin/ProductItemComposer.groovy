package ru.spb.locon.admin

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zul.Window

/**
 * User: Gleb
 * Date: 01.12.12
 * Time: 19:00
 */
class ProductItemComposer extends GrailsComposer {

  def afterCompose = {Window window ->
    int r = 0
  }

  public void onUser_info(Event event){
    ForwardEvent eventx = (ForwardEvent) event;
    println(eventx.getOrigin().getData())
  }

}
