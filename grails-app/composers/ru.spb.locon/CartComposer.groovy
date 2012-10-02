package ru.spb.locon

import org.zkoss.zul.Listbox
import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import ru.spb.locon.renderers.CartRenderer
import org.zkoss.zul.ListModelList
import cart.CartUtilsOld
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events

/**
 * User: Gleb
 * Date: 22.09.12
 * Time: 0:07
 */
class CartComposer extends GrailsComposer {

  Listbox products
  ListModelList<CartProductEntity> productsModel

  Button createOrder

  CartUtilsOld utils = new CartUtilsOld()

  def afterCompose = {Window window ->
    setModel()
    initializeListBox()
    createOrder.addEventListener(Events.ON_CLICK, createOrderLister)
  }

  private void setModel() {
    CartEntity cart = utils.getCart()
    if (cart != null && cart.listCartProduct != null)
      productsModel = new ListModelList<CartProductEntity>(cart.listCartProduct as List<CartProductEntity>)
  }


  private void initializeListBox() {
    products.setModel(productsModel)
    products.setItemRenderer(new CartRenderer(productsModel))
  }

  EventListener createOrderLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      CartEntity cart = utils.getCart()
      if (cart != null && cart.listCartProduct != null)
        Executions.sendRedirect("/shop/checkout")
    }
  }
}


