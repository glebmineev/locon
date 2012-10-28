package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Listbox
import org.zkoss.zk.ui.event.Events
import ru.spb.locon.renderers.OrderRenderer
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Listitem
import org.zkoss.zk.ui.Executions

import org.zkoss.zkplus.spring.SpringUtil

/**
 * User: Gleb
 * Date: 30.09.12
 * Time: 16:35
 */
class OrderComposer extends GrailsComposer {

  Listbox orders
  ListModelList<OrderEntity> ordersModel

  LoginService loginService = (LoginService) SpringUtil.getApplicationContext().getBean("loginService")

  def afterCompose = {Window window ->
    setModel()
    initializeListBox()
  }

  private void setModel() {
    List<OrderEntity> ordersList = getOrders()
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
        String path = "user"
        if (loginService.userGroups.contains("MANAGER"))
          path = "admin"
        Executions.sendRedirect("/${path}/orderItem?order=${order.id}")
      }
    }
  }

  private List<OrderEntity> getOrders(){
    List<OrderEntity> ordersList = null
    if (loginService.isLogged() && loginService.userGroups.contains("USER")){
      UserEntity user = loginService.getCurrentUser()
      ordersList =  OrderEntity.findAllWhere(user: user) as List<OrderEntity>
    }
    else
    {
      ordersList = OrderEntity.list()
    }
    return ordersList
  }

}
