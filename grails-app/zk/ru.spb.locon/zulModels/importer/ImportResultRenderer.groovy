package ru.spb.locon.zulModels.importer

import org.zkoss.zul.Div
import org.zkoss.zul.Row
import org.zkoss.zul.Label
import org.zkoss.zul.RowRenderer

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:32
 */
class ImportResultRenderer implements RowRenderer<ResultItem> {

/*  @Override
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

  }*/

  @Override
  void render(Row row, ResultItem t, int i) throws Exception {
    row.setValue(t)
    row.setId(t.id)

    row.appendChild(new Label(t.article))

    row.appendChild(new Label(t.name))

    Div imageCell = new ResultCell("item_${t.id}_PROCESS")
    row.appendChild(imageCell)

  }
}
