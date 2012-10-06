package ru.spb.locon.renderers

import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell

import org.zkoss.zul.Image
import org.zkoss.zul.Div
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem

import org.zkoss.zk.ui.event.Events

import cart.SessionUtils

class ProductRenderer implements ListitemRenderer {

  @Override
  void render(org.zkoss.zul.Listitem listitem, Object t, int i) {
    ProductEntity entity = (ProductEntity) t
    listitem.setValue(entity)

    Listcell imageCell = new Listcell()
    imageCell.setStyle("width:130px;")
    imageCell.appendChild(getImage(entity))
    imageCell.setParent(listitem)

    Listcell descCell = new Listcell(entity.name)
    Button addToCart = new Button("Добавить в корзину")
    addToCart.addEventListener(Events.ON_CLICK, addToCartListener)
    descCell.appendChild(addToCart)
    descCell.setParent(listitem)
  }

  private Image getImage(ProductEntity entity){
    Image image = new Image()
    image.setSrc("/images/empty.png")
    return image
  }

  EventListener addToCartListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component button = t.target
      Listitem parent = (Listitem) button.parent.parent
      ProductEntity value = (ProductEntity) parent.getValue()
      SessionUtils.addToCart(value)
    }
  }

  //TODO: сделать контейнер содержания.
  private Div getDescription(ProductEntity entity){
    return new Div()
  }

}

