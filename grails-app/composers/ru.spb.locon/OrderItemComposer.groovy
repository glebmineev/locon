package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.*
import org.zkoss.zk.ui.event.*
import org.zkoss.zk.ui.Executions
import ru.spb.locon.renderers.OrderItemRenderer
import org.zkoss.zhtml.Table
import org.zkoss.zhtml.Tr
import org.zkoss.zhtml.Td
import locon.LoginService
import org.zkoss.zkplus.spring.SpringUtil

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

  Listbox products
  ListModelList<OrderProductEntity> productsModel

  Long orderId

  LoginService loginService = (LoginService) SpringUtil.getApplicationContext().getBean("loginService")

  def afterCompose = {Window window ->
    orderId = Long.parseLong(execution.getParameter("order"))
    initializeFields()
    initializeListBox()

    if (loginService.isLogged() && loginService.getUserGroups().contains("MANAGER"))
      window.appendChild(createTableWithButtons())
  }

  private void initializeFields() {
    OrderEntity order = OrderEntity.get(orderId)
    if (loginService.userGroups.contains("MANAGER")) {
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

  }

  private void initializeListBox() {
    OrderEntity order = OrderEntity.get(orderId)
    Collection<OrderProductEntity> productsList = order.orderProductList
    productsModel = new ListModelList<OrderProductEntity>(productsList)
    products.setModel(productsModel)
    products.setItemRenderer(new OrderItemRenderer())
  }

  private Table createTableWithButtons() {

    Table table = new Table()
    table.setStyle("width:100%;align:center;")
    Tr row = new Tr()
    Td completeCell = new Td()
    Td backCell = new Td()
    Td cancelCell = new Td()

    Button orderComplete = new Button("Заказ обработан")
    orderComplete.addEventListener(Events.ON_CLICK, orderCompleteLister)

    Button backButton = new Button("Отменить заказ")
    backButton.addEventListener(Events.ON_CLICK, backButtonLister)

    Button cancelButton = new Button("К списку заказов")
    cancelButton.addEventListener(Events.ON_CLICK, orderCancelLister)

    completeCell.appendChild(orderComplete)
    row.appendChild(completeCell)

    backCell.appendChild(backButton)
    row.appendChild(backCell)

    cancelCell.appendChild(cancelButton)
    row.appendChild(cancelCell)

    table.appendChild(row)
    return table

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
