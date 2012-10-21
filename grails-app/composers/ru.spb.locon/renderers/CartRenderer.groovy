package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell

import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem

import cart.CartItem
import ru.spb.locon.ProductEntity
import org.zkoss.zkplus.databind.BindingListModelList
import locon.CartService
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Label
import org.zkoss.zul.Hbox
import org.zkoss.zul.Image

/**
 * User: Gleb
 * Date: 23.09.12
 * Time: 14:54
 */
class CartRenderer implements ListitemRenderer<CartItem> {

  BindingListModelList<CartItem> cartModel

  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  CartRenderer(BindingListModelList<CartItem> cartModel) {
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
    Hbox hbox = new Hbox()
    Label label = new Label()
    label.setValue(Long.toString(count))
    Image plus = new Image("/images/plus.png")
    plus.setAttribute("label", label)
    plus.setAttribute("value", Long.toString(count))
    plus.addEventListener(Events.ON_CLICK, plusListener)
    Image minus = new Image("/images/minus.png")
    minus.addEventListener(Events.ON_CLICK, minusListener)
    minus.setAttribute("label", label)
    minus.setAttribute("value", Long.toString(count))
    hbox.appendChild(label)
    hbox.appendChild(plus)
    hbox.appendChild(minus)
    productsCount.appendChild(hbox)
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
      cartService.removeFromCart(value)
    }
  }

  EventListener plusListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component image = t.target
      Listitem parent = (Listitem) image.parent.parent.parent
      CartItem value = (CartItem) parent.getValue()
      cartService.addToCart(value.product)
      //updateCartModel()
    }
  }

  EventListener minusListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component image = t.target
      Listitem parent = (Listitem) image.parent.parent.parent
      CartItem value = (CartItem) parent.getValue()
      if (value.count != 0) {
        cartService.decrementProduct(value)
        updateCartModel()
      }
    }
  }

  private void updateCartModel(){
    cartModel.clear()
    cartModel.addAll(cartService.getCartProducts())
  }

}


