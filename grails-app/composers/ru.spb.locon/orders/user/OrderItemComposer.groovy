package ru.spb.locon.orders.user

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.*
import org.zkoss.zk.ui.event.*
import org.zkoss.zk.ui.Executions
import ru.spb.locon.renderers.OrderItemRenderer
import org.zkoss.zhtml.Table
import org.zkoss.zhtml.Tr
import org.zkoss.zhtml.Td

import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.OrderProductEntity
import ru.spb.locon.LoginService
import ru.spb.locon.OrderEntity

/**
 * User: Gleb
 * Date: 07.10.12
 * Time: 14:11
 */
class OrderItemComposer extends GrailsComposer {

  Label fio
  Label phone
  Label email
  Label address
  Label notes
  Radio courier
  Radio emoney
  Label number

  Label orderStatus

  Listbox products
  ListModelList<OrderProductEntity> productsModel

  Long orderId

  def afterCompose = {Window window ->
    orderId = Long.parseLong(execution.getParameter("order"))
    initializeFields()
    initializeListBox()

  }

  private void initializeFields() {
    OrderEntity order = OrderEntity.get(orderId)

    if (order.isProcessed)
      orderStatus.setValue("Ваш заказ находиться в обработке")
    if (order.isComplete)
      orderStatus.setValue("Ваш заказ выполнен")
    if (order.isCancel)
      orderStatus.setValue("Ваш заказ отменен")

  }

  private void initializeListBox() {
    OrderEntity order = OrderEntity.get(orderId)
    Collection<OrderProductEntity> productsList = order.orderProductList
    productsModel = new ListModelList<OrderProductEntity>(productsList)
    products.setModel(productsModel)
    products.setItemRenderer(new OrderItemRenderer())
  }

  public void onClick_products(Event event) {
    int index = products.getSelectedIndex()
    Listitem listitem = products.getItemAtIndex(index)
    if (listitem != null)
      Executions.sendRedirect("/shop/product?product=${listitem.value.id}")
  }

}