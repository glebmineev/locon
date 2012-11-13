package ru.spb.locon.catalog

import ru.spb.locon.ProductFilterGroupEntity
import org.zkoss.zul.*

/**
 * User: Gleb
 * Date: 13.11.12
 * Time: 13:41
 */
class GroupFilterRenderer implements ComboitemRenderer<ProductFilterGroupEntity> {

  @Override
  void render(org.zkoss.zul.Comboitem comboitem, ProductFilterGroupEntity t, int i) {

    comboitem.appendChild(new Label(t.name))

  }

}
