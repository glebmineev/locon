package ru.spb.locon.zulModels.admin.filters.components

import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Combobox
import org.zkoss.zul.ListModelList
import ru.spb.locon.zulModels.admin.filters.data.FilterBean
import ru.spb.locon.zulModels.admin.filters.data.ObjectFilter

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/11/13
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
class ComboBoxFilter extends Combobox {

  FilterBean filterBean

  ComboBoxFilter() {
  }

  FilterBean getFilterBean() {
    return filterBean
  }

  void setFilterBean(FilterBean filterBean) {
    this.filterBean = filterBean

    ListModelList model = new ListModelList()
    //model.add(-1, null)
    model.addAll(filterBean.field.getType().newInstance().list())
    setModel(model)

    this.addEventListener(Events.ON_SELECT, new org.zkoss.zk.ui.event.EventListener() {

      @Override
      void onEvent(Event t) {
        filterBean.callback.changed(new ObjectFilter(filterBean.field, getSelectedItem().value, null))
      }

    })

  }

}
