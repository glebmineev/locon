package ru.spb.locon.importer

import ru.spb.locon.cart.CartItem
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell
import org.zkoss.zul.Label
import org.zkoss.zul.RowRenderer

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:32
 */
class ImportResultRenderer implements ListitemRenderer<ResultItem> {

  @Override
  void render(org.zkoss.zul.Listitem listitem, ResultItem t, int i) {

    listitem.setValue(t)
    listitem.setId(t.id)

    Listcell articleCell = new Listcell()
    articleCell.appendChild(new Label(t.article))
    articleCell.setParent(listitem)

    Listcell nameCell = new Listcell()
    nameCell.appendChild(new Label(t.name))
    nameCell.setParent(listitem)

    Listcell imageCell = new ResultCell("item_${t.id}_PROCESS")
    imageCell.setParent(listitem)

  }

}
