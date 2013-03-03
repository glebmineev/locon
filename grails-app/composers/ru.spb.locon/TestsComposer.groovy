package ru.spb.locon

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zhtml.Textarea
import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Button
import org.zkoss.zul.Window

/**
 * Created with IntelliJ IDEA.
 * User: Gleb-PC
 * Date: 10.02.13
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
class TestsComposer extends GrailsComposer{

/*    @Init
    public void init() {
        Clients.evalJavaScript("alert(\$('.result').html())")
    }*/

    Button save

    def afterCompose = {Window window ->
    save.addEventListener(Events.ON_CLICK, uploadLister)
    }

    org.zkoss.zk.ui.event.EventListener uploadLister = new org.zkoss.zk.ui.event.EventListener() {
        @Override
        void onEvent(Event t) {
            Clients.evalJavaScript("\$('.result').val(CKEDITOR.instances['ckeditor'].getData())")
        }
    }


}
