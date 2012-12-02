package ru.spb.locon.catalog

import org.zkoss.zul.ListitemRenderer
import ru.spb.locon.FilterEntity
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Listcell

/**
 * User: Gleb
 * Date: 20.10.12
 * Time: 18:24
 */
class FilterRenderer implements ListitemRenderer<FilterEntity> {
  
  @Override
  void render(org.zkoss.zul.Listitem listitem, FilterEntity t, int i) {
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
