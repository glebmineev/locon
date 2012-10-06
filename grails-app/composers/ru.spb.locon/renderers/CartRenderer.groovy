package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell
import org.zkoss.zul.Textbox

import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem

import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zul.ListModelList

import cart.CartItem
import ru.spb.locon.ProductEntity
import cart.SessionUtils
import importer.ConvertUtils

/**
 * User: Gleb
 * Date: 23.09.12
 * Time: 14:54
 */
class CartRenderer implements ListitemRenderer<CartItem> {

  ListModelList<CartItem> cartModel

  CartRenderer(ListModelList<CartItem> cartModel) {
    this.cartModel = cartModel
  }

  @Override
  void render(Listitem listitem, CartItem object, int i) {
    CartItem cartItem = (CartItem) object
    listitem.setValue(cartItem)

    ProductEntity product = cartItem.getProduct()
    Long count = cartItem.getCount()
    //Товар
    Listcell productCell = new Listcell(product.name)
    productCell.setParent(listitem)

    //Цена
    Float price = product.price != null ? product.price : 0.0
    Listcell priceCell = new Listcell(price.toString())
    priceCell.setParent(listitem)

    //Количество
    Listcell productsCount = new Listcell()
    Textbox textbox = new Textbox()
    textbox.addEventListener(Events.ON_CHANGE, updateListener)

    textbox.setValue(Long.toString(count))
    productsCount.appendChild(textbox)
    productsCount.setParent(listitem)

    //Стоимость
    Float roundPrice = (product.price * Float.parseFloat(count.toString()))
    Listcell allPriceCell = new Listcell(roundPrice.toString())
    allPriceCell.setParent(listitem)

    //Дейсвия
    Button deleteFromCart = new Button("Удалить")
    deleteFromCart.addEventListener(Events.ON_CLICK, deleteButtonListener)

    Listcell actionsCell = new Listcell()
    actionsCell.appendChild(deleteFromCart)
    actionsCell.setParent(listitem)
  }

  EventListener deleteButtonListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component button = t.target
      Listitem parent = (Listitem) button.parent.parent
      CartItem value = (CartItem) parent.getValue()
      cartModel.remove(value)
      SessionUtils.removeFromCart(value)
    }
  }

  EventListener updateListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      InputEvent event = (InputEvent) t

      Component button = event.target
      Listitem parent = (Listitem) button.parent.parent
      CartItem value = (CartItem) parent.getValue()

      //TODO сдесь обновлять корзину
    }
  }
}


