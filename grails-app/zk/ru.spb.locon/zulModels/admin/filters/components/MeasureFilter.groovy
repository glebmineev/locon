package ru.spb.locon.zulModels.admin.filters.components

import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zul.Hbox
import org.zkoss.zul.Image
import org.zkoss.zul.Intbox
import org.zkoss.zul.Vbox
import ru.spb.locon.zulModels.admin.filters.data.FilterBean
import ru.spb.locon.zulModels.admin.filters.data.ObjectFilter

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/14/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
class MeasureFilter extends Vbox {

  FilterBean filterBean

  Intbox startMeasure = new Intbox()
  Intbox endMeasure = new Intbox()

  MeasureFilter() {

  }

  void setFilterBean(FilterBean filterBean) {
    this.filterBean = filterBean

    this.setWidth("100%")

    String startValue = null
    String endValue = null

    Hbox startBox = new Hbox()
    startBox.appendChild(new Image("/images/better.png"))
    startBox.appendChild(startMeasure)
    startMeasure.setInstant(true)
    startMeasure.setWidth("100%")
    startMeasure.addEventListener(Events.ON_CHANGING, new org.zkoss.zk.ui.event.EventListener() {

      @Override
      void onEvent(Event t) {
        InputEvent inputEvent = (InputEvent) t
        startValue = inputEvent.value
        filterBean.callback.changed(new ObjectFilter(filterBean.field, startValue, endValue))
        startMeasure.setSelectionRange(inputEvent.start, inputEvent.start)
      }

    })

    Hbox endBox = new Hbox()
    endBox.appendChild(new Image("/images/less.png"))
    endBox.appendChild(endMeasure)
    endMeasure.setInstant(true)
    endMeasure.setWidth("100%")
    endMeasure.addEventListener(Events.ON_CHANGING, new org.zkoss.zk.ui.event.EventListener() {

      @Override
      void onEvent(Event t) {
        InputEvent inputEvent = (InputEvent) t
        endValue = inputEvent.value
        filterBean.callback.changed(new ObjectFilter(filterBean.field, startValue, endValue))
        endMeasure.setSelectionRange(inputEvent.start, inputEvent.start)
      }

    })

    appendChild(startBox)
    appendChild(endBox)

  }

}
