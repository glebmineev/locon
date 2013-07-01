package ru.spb.locon

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.GlobalCommand
import org.zkoss.bind.annotation.Init
import org.zkoss.zhtml.Textarea
import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zk.ui.util.GenericForwardComposer
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import org.zkoss.zul.Window
import ru.spb.locon.wrappers.CategoryTreeNode

/**
 * Created with IntelliJ IDEA.
 * User: Gleb-PC
 * Date: 10.02.13
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
class TestsComposer /*extends GrailsComposer*/  {

/*  @Override
  public void doAfterCompose(Component comp) throws Exception {
    super.doAfterCompose(comp);
  }*/

  @Command
  public void test(@BindingParam("value") String value){
    int r = 0;
/*    ForwardEvent eventx = (ForwardEvent) event;
    println(eventx.getOrigin().getData())*/
  }

}
