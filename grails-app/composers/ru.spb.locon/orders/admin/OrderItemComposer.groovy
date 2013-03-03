package ru.spb.locon.orders.admin

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.LoginService
import ru.spb.locon.OrderEntity
import ru.spb.locon.OrderProductEntity
import ru.spb.locon.renderers.OrderItemRenderer
import org.zkoss.zul.*

/**
 * User: Gleb
 * Date: 07.10.12
 * Time: 14:11
 */
class OrderItemComposer extends GrailsComposer {

  Label header

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
  Button orderCancel
  Button backButton

  Long orderId

  def afterCompose = {Window window ->

    orderId = Long.parseLong(execution.getParameter("order"))
    initializeFields()
    initializeListBox()
  }

  private void initializeFields() {
    OrderEntity order = OrderEntity.get(orderId)

    header.setValue("Заказ ${order.number}")

    if (order.user == null) {
      fio.setValue(order.fio)
      phone.setValue(order.phone)
      email.setValue(order.email)
      address.setValue(order.address)
    }
    else {
      fio.setValue(order.user.fio)
      phone.setValue(order.user.phone)
      email.setValue(order.user.email)
      address.setValue(order.user.address)
    }

    number.setValue(order.number)
    notes.setValue(order.notes)

    if (order.courier) {
      courier.setSelected(true)
      emoney.setSelected(false)
    }
    else {
      courier.setSelected(false)
      emoney.setSelected(true)
    }
  }

  private void initializeListBox() {
    OrderEntity order = OrderEntity.get(orderId)
    Collection<OrderProductEntity> productsList = order.orderProductList
    productsModel = new ListModelList<OrderProductEntity>(productsList)
    products.setModel(productsModel)
    products.setItemRenderer(new OrderItemRenderer())
  }

  public void onClick_orderComplete(Event event) {
    OrderEntity order = OrderEntity.get(orderId)
    OrderEntity.withTransaction {
      order.setIsComplete(true)
      order.setIsProcessed(false)
      order.merge(flush: true)
    }
    Executions.sendRedirect("/admin/orders")
  }

  public void onClick_backButton(Event event) {
    Executions.sendRedirect("/admin/orders")
  }

  public void onClick_orderCancel(Event event) {
    OrderEntity order = OrderEntity.get(orderId)
    OrderEntity.withTransaction {
      order.setIsProcessed(false)
      order.setIsComplete(false)
      order.setIsCancel(true)
      order.merge(flush: true)
    }
    Executions.sendRedirect("/admin/orders")
  }

  public void onClick_products(Event event) {
    int index = products.getSelectedIndex()
    Listitem listitem = products.getItemAtIndex(index)
    if (listitem != null)
      Executions.sendRedirect("/shop/product?product=${listitem.value.id}")
  }

}