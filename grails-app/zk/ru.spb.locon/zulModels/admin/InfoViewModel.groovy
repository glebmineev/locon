package ru.spb.locon.zulModels.admin

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.event.Event
import ru.spb.locon.InfoEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/26/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
class InfoViewModel {

  InfoEntity infoEntity;

  String about;
  String contacts;
  String delivery;
  String details;

  @Init
  public void init(){
    infoEntity = InfoEntity.first()
    about = infoEntity.about
    contacts = infoEntity.contacts
    delivery = infoEntity.delivery
    details = infoEntity.details
  }

  @Command
  public void saveContacts(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    InfoEntity.withTransaction {
      infoEntity.setContacts(getFormattedString(event.getData() as String))
      infoEntity.save(flush: true)
    }
  }

  @Command
  public void saveAbout(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    InfoEntity.withTransaction {
      infoEntity.setAbout(getFormattedString(event.getData() as String))
      infoEntity.save(flush: true)
    }
  }

  @Command
  public void saveDelivery(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    InfoEntity.withTransaction {
      infoEntity.setDelivery(getFormattedString(event.getData() as String))
      infoEntity.save(flush: true)
    }
  }

  @Command
  public void saveDetails(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    InfoEntity.withTransaction {
      infoEntity.setDetails(getFormattedString(event.getData() as String))
      infoEntity.save(flush: true)
    }
  }

 String getFormattedString(String html){
   String formatted = html.replaceAll("(\\n\\t|\\n)", "");
   return formatted;
 }

}
