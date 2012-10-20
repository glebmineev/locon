package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zul.Textbox

import org.zkoss.zul.Radio

import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events

import locon.ZulService
import org.zkoss.zkplus.spring.SpringUtil
import cart.CartItem
import locon.LoginService
import locon.CartService

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

  ZulService zulService = (ZulService) SpringUtil.getApplicationContext().getBean("zulService")
  LoginService loginService = (LoginService) SpringUtil.getApplicationContext().getBean("loginService")
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  def afterCompose = {Window window ->
    initializeFields()
    checkout.addEventListener(Events.ON_CLICK, createOrderLister)
  }

  private void initializeFields() {
    UserEntity user = loginService.currentUser
    if (user != null) {
      fio.setValue(user.fio)
      phone.setValue(user.phone)
      email.setValue(user.email)
      address.setValue(user.address)
    }
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

      if (order.errors.allErrors.size() == 0) {
        saveCartData(order)
      }

      cartService.cleanCart()

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

    if (courier.checked) {
      order.setCourier(true)
      order.setEmoney(false)
    }

    if (emoney.checked) {
      order.setEmoney(true)
      order.setCourier(false)
    }

    order.setIsProcessed(true)

    UserEntity user = loginService.currentUser
    if (user != null)
      order.setUser(user)
    
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


  private void saveCartData(OrderEntity order) {
    List<CartItem> cartItems = cartService.getCartProducts()
    OrderProductEntity.withTransaction {
      cartItems.each {CartItem cartItem ->
        OrderProductEntity orderProduct = new OrderProductEntity(
            product: cartItem.getProduct(),
            countProduct: cartItem.getCount(),
            order: order
        )
        orderProduct.save(flush: true)
      }
    }
  }

}
