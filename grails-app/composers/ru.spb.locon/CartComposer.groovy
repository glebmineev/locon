package ru.spb.locon

import org.zkoss.zul.Listbox
import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import ru.spb.locon.renderers.CartRenderer
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import cart.SessionUtils
import cart.CartItem

/**
 * User: Gleb
 * Date: 22.09.12
 * Time: 0:07
 */
class CartComposer extends GrailsComposer {

  Listbox cartItems
  ListModelList<CartItem> cartModel

  Button createOrder

  def afterCompose = {Window window ->
    setModel()
    initializeListBox()
    createOrder.addEventListener(Events.ON_CLICK, createOrderLister)
  }

  private void setModel() {
    List<CartItem> cartItems = SessionUtils.getCartProducts()
    if (cartItems.size() > 0)
      cartModel = new ListModelList<CartItem>(cartItems)
  }

  private void initializeListBox() {
    cartItems.setModel(cartModel)
    cartItems.setItemRenderer(new CartRenderer(cartModel))
  }

  EventListener createOrderLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      List<CartItem> cartItems = SessionUtils.getCartProducts()
      if (cartItems.size() > 0)
        Executions.sendRedirect("/shop/checkout")
    }
  }
}


