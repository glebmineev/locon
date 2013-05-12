package ru.spb.locon.zulModels.admin.filters.components

import org.zkoss.zul.Textbox
import org.zkoss.zk.ui.event.*
import ru.spb.locon.zulModels.admin.filters.data.FilterBean
import ru.spb.locon.zulModels.admin.filters.data.ObjectFilter

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 20:07
 */
class TextBoxFilter extends Textbox {

  FilterBean filterBean

  TextBoxFilter(){
  }

  FilterBean getFilterBean() {
    return filterBean
  }

  void setFilterBean(FilterBean filterBean) {
    this.filterBean = filterBean

    this.setWidth("80%")
    this.setInstant(true)

    this.addEventListener(Events.ON_CHANGING, new EventListener() {

      @Override
      void onEvent(Event t) {
        InputEvent inputEvent = (InputEvent) t

        filterBean.callback.changed(new ObjectFilter(filterBean.field, inputEvent.value, null))

        setSelectionRange(inputEvent.start, inputEvent.start)
      }

    })

  }

}
