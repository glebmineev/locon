package ru.spb.locon.zulModels.admin.filters.components

import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zul.Intbox
import ru.spb.locon.zulModels.admin.filters.data.FilterBean
import ru.spb.locon.zulModels.admin.filters.data.ObjectFilter

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/14/13
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
class NumberBoxFilter extends Intbox {

  FilterBean filterBean

  NumberBoxFilter(){
    super()
  }

  FilterBean getFilterBean() {
    return filterBean
  }

  void setFilterBean(FilterBean filterBean) {
    this.filterBean = filterBean

    this.setWidth("100%")
    this.setInstant(true)

    this.addEventListener(Events.ON_CHANGING, new org.zkoss.zk.ui.event.EventListener() {

      @Override
      void onEvent(Event t) {
        InputEvent inputEvent = (InputEvent) t

        filterBean.callback.changed(new ObjectFilter(filterBean.field, inputEvent.value, null))

        setSelectionRange(inputEvent.start, inputEvent.start)
      }

    })

  }

}
