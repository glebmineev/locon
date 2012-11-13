package ru.spb.locon.catalog

import org.zkoss.zul.ListitemRenderer
import ru.spb.locon.ProductFilterEntity
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Listcell
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import ru.spb.locon.ProductEntity
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.CheckEvent
import org.zkoss.zkplus.databind.BindingListModelList
import ru.spb.locon.CategoryEntity
import org.zkoss.zul.Listgroup

/**
 * User: Gleb
 * Date: 20.10.12
 * Time: 18:24
 */
class FilterRenderer implements ListitemRenderer<ProductFilterEntity> {
  
  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductFilterEntity t, int i) {
    listitem.setValue(t)

    Checkbox checkbox = new Checkbox()
    checkbox.setId("checkbox_${t.name}")
    checkbox.setDisabled(true)
    Listcell checkboxCell = new Listcell()
    checkboxCell.appendChild(checkbox)
    checkboxCell.setParent(listitem)
    
    Listcell labelCell = new Listcell(t.name)
    labelCell.setParent(listitem)

  }

}
