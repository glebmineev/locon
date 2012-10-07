package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.*
import ru.spb.locon.renderers.ProductRenderer
import org.zkoss.zk.ui.event.*
import org.zkoss.zk.ui.Executions
import ru.spb.locon.renderers.OrderItemRenderer

/**
 * User: Gleb
 * Date: 07.10.12
 * Time: 14:11
 */
class AdminOrderItemComposer extends GrailsComposer {

  Label fio
  Label phone
  Label email
  Label address
  Label notes
  Radio courier
  Radio emoney

  Label number

  Listbox products
  ListModelList<OrderProductEntity> productsModel

  Button orderComplete
  Button backButton
  Button orderCancel

  Long orderId

  def afterCompose = {Window window ->
    orderId = Long.parseLong(execution.getParameter("order"))
    initializeFields()
    initializeListBox()
    initializeButton()
  }

  private void initializeFields(){
    OrderEntity order = OrderEntity.get(orderId)
    if (order.user == null){
      number.setValue(order.number)
      fio.setValue(order.fio)
      phone.setValue(order.phone)
      email.setValue(order.email)
      address.setValue(order.address)
      notes.setValue(order.notes)
      if (order.courier){
        courier.setSelected(true)
        emoney.setSelected(false)
      }
      else {
        courier.setSelected(false)
        emoney.setSelected(true)
      }
    }
  }

  private void initializeListBox(){
    OrderEntity order = OrderEntity.get(orderId)
    Collection<OrderProductEntity> productsList = order.orderProductList
    productsModel = new ListModelList<OrderProductEntity>(productsList)
    products.setModel(productsModel)
    products.setItemRenderer(new OrderItemRenderer())
  }

  private void initializeButton() {
    orderComplete.addEventListener(Events.ON_CLICK, orderCompleteLister)
    backButton.addEventListener(Events.ON_CLICK, backButtonLister)
    orderCancel.addEventListener(Events.ON_CLICK, orderCancelLister)
  }

  EventListener orderCompleteLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      OrderEntity order = OrderEntity.get(orderId)
      OrderEntity.withTransaction {
        order.setIsComplete(true)
        order.setIsProcessed(false)
        order.merge(flush: true)
      }
      Executions.sendRedirect("/admin/orders")
    }
  }

  EventListener backButtonLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      Executions.sendRedirect("/admin/orders")
    }
  }

  EventListener orderCancelLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      OrderEntity order = OrderEntity.get(orderId)
      OrderEntity.withTransaction {
        order.setIsProcessed(false)
        order.setIsComplete(false)
        order.setIsCancel(true)
        order.merge(flush: true)
      }
      Executions.sendRedirect("/admin/orders")
    }
  }

}
