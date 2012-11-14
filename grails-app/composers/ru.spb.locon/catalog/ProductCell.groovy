package ru.spb.locon.catalog

import org.zkoss.zul.Listcell
import org.zkoss.zul.Vbox
import org.zkoss.zul.Label
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Events
import ru.spb.locon.ProductEntity
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem
import ru.spb.locon.CartService
import org.zkoss.zkplus.spring.SpringUtil

/**
 * User: Gleb
 * Date: 13.11.12
 * Time: 19:31
 */
class ProductCell extends Listcell {

  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  ProductCell(ProductEntity entity) {
    Vbox vBox = new Vbox()
    vBox.setAlign("left")

    Label manufacturer = new Label(entity.manufacturer.name)
    manufacturer.setStyle("font-size: 14px;margin-top: 5px;margin-bottom: 5px;")

    Label productName = new Label(entity.name)
    productName.setStyle("font-size: 18px;margin-top: 5px;margin-bottom: 5px;")

    Label price = new Label()
    price.setStyle("font-size: 14px;margin-bottom: 5px;")
    price.setValue("Цена: ${Float.toString(entity.price)}")

    Button addToCart = new Button("Добавить в корзину")
    addToCart.addEventListener(Events.ON_CLICK, addToCartListener)

    vBox.appendChild(manufacturer)
    vBox.appendChild(productName)
    vBox.appendChild(price)
    vBox.appendChild(addToCart)
    appendChild(vBox)
  }

  EventListener addToCartListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component button = t.target
      Listitem parent = (Listitem) button.parent.parent.parent
      ProductEntity value = (ProductEntity) parent.getValue()
      cartService.addToCart(value)
    }
  }

}
