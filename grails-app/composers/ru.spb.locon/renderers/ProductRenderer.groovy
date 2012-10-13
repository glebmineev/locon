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
import org.zkoss.zul.Groupbox
import org.zkoss.zul.Caption
import org.zkoss.zhtml.H2
import org.zkoss.zul.Label
import org.zkoss.zul.Vbox

class ProductRenderer implements ListitemRenderer<ProductEntity> {

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {
    listitem.setValue(t)

    //Ячейка с каритнкой
    Listcell imageCell = new Listcell()
    imageCell.appendChild(getImage(t))
    imageCell.setParent(listitem)

    //Ячейка с товаром
    Listcell descCell = new Listcell()
    descCell.appendChild(getProductCell(t))
    descCell.setParent(listitem)
  }

  private Image getImage(ProductEntity entity) {
    Image image = new Image()
    image.setSrc("/images/empty.png")
    return image
  }

  EventListener addToCartListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component button = t.target
      Listitem parent = (Listitem) button.parent.parent.parent.parent
      ProductEntity value = (ProductEntity) parent.getValue()
      SessionUtils.addToCart(value)
    }
  }

  private Div getProductCell(ProductEntity entity){
    
    Div productCell = new Div()

    Vbox vBox = new Vbox()

    Label header = new Label(entity.name)
    header.setStyle("font: 16pt")

    Button addToCart = new Button("Добавить в корзину")
    addToCart.addEventListener(Events.ON_CLICK, addToCartListener)

    vBox.appendChild(header)
    vBox.appendChild(addToCart)

    productCell.appendChild(vBox)

    return productCell
  }

  private Div getDescription(ProductEntity entity) {
    return new Div()
  }

}

