package ru.spb.locon.renderers

import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell
import domain.DomainUtils
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Image
import org.zkoss.zul.Div

class ProductRenderer implements ListitemRenderer {

  @Override
  void render(org.zkoss.zul.Listitem listitem, Object t, int i) {
    ProductEntity entity = (ProductEntity) t
    listitem.setValue(entity)

    Listcell imageCell = new Listcell()
    imageCell.appendChild(getImage(entity))
    imageCell.setParent(listitem)

    Listcell descCell = new Listcell(entity.name)
    descCell.setParent(listitem)
  }

  private Image getImage(ProductEntity entity){
    Image image = new Image()
    image.setSrc("/images/empty.png")
    image.setWidth("120px")
    return image
  }

  //TODO: сделать контейнер содержания.
  private Div getDescription(ProductEntity entity){
    return new Div()
  }

}

