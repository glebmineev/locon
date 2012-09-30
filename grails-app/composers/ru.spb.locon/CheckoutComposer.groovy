package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zul.Textbox

import org.zkoss.zul.Radio
import cart.CartUtils
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events

import locon.ZulService
import org.zkoss.zkplus.spring.SpringUtil

/**
 * User: Gleb
 * Date: 27.09.12
 * Time: 22:07
 */
class CheckoutComposer extends GrailsComposer {

  Textbox fio
  Textbox phone
  Textbox email
  Textbox address
  Textbox notes
  Radio courier
  Radio emoney

  Button checkout

  CartUtils utils = new CartUtils()
  ZulService zulService = (ZulService) SpringUtil.getApplicationContext().getBean("zulService")

  def afterCompose = {Window window ->

    checkout.addEventListener(Events.ON_CLICK, createOrderLister)

  }

  EventListener createOrderLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      OrderEntity order = null
      //сохраняем заказ
      OrderEntity.withTransaction {
        order = saveOrder()
        if (order.validate()) {
          order.save(flush: true)
          Executions.sendRedirect("/shop/success")
        }
        else {
          zulService.showErrors(order.errors.allErrors)
        }
      }

      if(order.errors.allErrors.size() == 0){
        saveCartData(order)
      }
    }
  }

  private OrderEntity saveOrder() {
    OrderEntity order = new OrderEntity()
    order.setNumber(UUID.randomUUID().toString().replaceAll("-", "_"))

    if (validTextBox(fio))
      order.setFio(fio.value)
    if (validTextBox(phone))
      order.setPhone(phone.value)
    if (validTextBox(email))
      order.setEmail(email.value)
    if (validTextBox(address))
      order.setAddress(address.getValue())
    if (validTextBox(notes))
      order.setNotes(notes.getValue())

    if (courier.checked){
      order.setCourier(true)
      order.setEmoney(false)
    }

    if (emoney.checked) {
      order.setEmoney(true)
      order.setCourier(false)
    }


    return order
  }

  private validTextBox(Textbox tBox) {
    boolean result = false
    if (tBox.getValue() != null &&
        !tBox.getValue().isEmpty()) {
      result = true
    }
    return result
  }


  private void saveCartData(OrderEntity order){
    utils.getCart().listCartProduct.each {CartProductEntity cartProduct ->
      OrderProductEntity.withTransaction{
        OrderProductEntity orderProduct = new OrderProductEntity()
        orderProduct.setCountProduct(cartProduct.productsCount)
        orderProduct.setProduct(cartProduct.product)
        orderProduct.setOrder(order)
        orderProduct.save(flush: true)
      }
    }

    utils.deleteCart()
    utils.clearCookie()
    utils.recalculateCart()
  }
}
