package ru.spb.locon.renderers

import locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell
import domain.DomainUtils

class ProductRenderer implements ListitemRenderer {

  @Override
  void render(org.zkoss.zul.Listitem listitem, Object t, int i) {
    ProductEntity entity = (ProductEntity) t
    listitem.setValue(entity)
    List<String> info = DomainUtils.fieldInfo(ProductEntity)
    info.each {String name ->
      new Listcell(DomainUtils.parseTo(entity."${name}")).setParent(listitem)
    }
  }
}

