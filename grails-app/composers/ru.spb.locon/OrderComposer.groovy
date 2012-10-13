package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Listbox
import org.zkoss.zk.ui.event.Events
import ru.spb.locon.renderers.CartRenderer
import ru.spb.locon.renderers.OrderRenderer
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Listitem
import org.zkoss.zk.ui.Executions

/**
 * User: Gleb
 * Date: 30.09.12
 * Time: 16:35
 */
class OrderComposer extends GrailsComposer {

  Listbox orders
  ListModelList<OrderEntity> ordersModel

  def afterCompose = {Window window ->
    setModel()
    initializeListBox()
  }

  private void setModel() {
    List<OrderEntity> ordersList = OrderEntity.list()
    if (ordersList != null)
      ordersModel = new ListModelList<OrderEntity>(ordersList)
  }

  private void initializeListBox() {
    orders.addEventListener(Events.ON_CLICK, ordersLister)
    orders.setModel(ordersModel)
    orders.setItemRenderer(new OrderRenderer())
  }

  EventListener ordersLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      int index = orders.getSelectedIndex()
      final Listitem listitem = orders.getItemAtIndex(index)
      if (listitem != null) {
        OrderEntity order = (OrderEntity) listitem.getValue()
        Executions.sendRedirect("/admin/orderItem?order=${order.id}")
      }
    }
  }

}
