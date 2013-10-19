package ru.spb.locon.zulModels.shop

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import ru.spb.locon.*

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Модель создания заказа.
 */
class CheckoutViewModel {

  String fio
  String phone
  String email
  String address
  String notes
  boolean courier = true
  boolean emoney = false

  LoginService loginService = ApplicationHolder.getApplication().getMainContext().getBean("loginService") as LoginService
  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService

  @Init
  public void init() {
    //Если юзер авторизован проставляем его данные в поля.
    UserEntity user = loginService.currentUser
    if (user != null) {
      fio = user.fio
      phone = user.phone
      email = user.email
      address = user.address
    }
  }

  @Command
  public void checkout() {
    OrderEntity order = fillOrder()
    //сохраняем заказ
    OrderEntity.withTransaction {
      if (order.validate())
        order.save(flush: true)
    }

    saveCartData(order)
    cartService.cleanCart()
    Executions.sendRedirect("/shop/success")

  }

  /**
   * Формирует заказ.
   * @return - заказ.
   */
  private OrderEntity fillOrder() {
    OrderEntity order = new OrderEntity()
    OrderIdentEntity ident = new OrderIdentEntity(ident: OrderIdentEntity.last().ident + 1).save(flush: true)
    order.setNumber(ident.ident as String)

    order.setFio(fio)
    order.setPhone(phone)
    order.setEmail(email)
    order.setAddress(address)
    order.setNotes(notes)

    order.setCourier(courier)
    order.setEmoney(emoney)

    order.setIsProcessed(true)

    UserEntity user = loginService.currentUser
    if (user != null)
      order.setUser(user)

    return order
  }

  /**
   * Сохранение данных околичестве товара.
   * @param order - заказ.
   */
  void saveCartData(OrderEntity order) {
    //получаем товары в корзине.
    List<ProductEntity> cartItems = cartService.getCartProducts()
    OrderProductEntity.withTransaction {
      //сохраняем данные о количестве товара в сущности OrderProductEntity.
      cartItems.each { ProductEntity productEntity ->
        OrderProductEntity orderProduct = new OrderProductEntity(
            product: productEntity,
            countProduct: cartService.getProductCount(productEntity.id),
            order: order
        )
        orderProduct.save(flush: true)
      }
    }
  }

}
