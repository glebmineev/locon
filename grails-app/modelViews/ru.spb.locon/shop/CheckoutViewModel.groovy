package ru.spb.locon.shop

import com.google.common.base.Strings
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Textbox
import ru.spb.locon.CartService
import ru.spb.locon.LoginService
import ru.spb.locon.OrderEntity
import ru.spb.locon.OrderProductEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.UserEntity
import ru.spb.locon.ZulService

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

  ZulService zulService = (ZulService) SpringUtil.getApplicationContext().getBean("zulService")
  LoginService loginService = (LoginService) SpringUtil.getApplicationContext().getBean("loginService")
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

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
  public void checkout(){
    OrderEntity order = null
    //сохраняем заказ
    OrderEntity.withTransaction {
      order = fillOrder()
      if (order.validate()) {
        order.save(flush: true)
        Executions.sendRedirect("/shop/success")
      }
      else
        zulService.showErrors(order.errors.allErrors)

    }

    if (order.errors.allErrors.size() == 0) {
      saveCartData(order)
    }

    cartService.cleanCart()
  }

  /**
   * Формирует заказ.
   * @return - заказ.
   */
  private OrderEntity fillOrder() {
    OrderEntity order = new OrderEntity()
    order.setNumber(UUID.randomUUID().toString().replaceAll("-", "_"))

    if (!Strings.isNullOrEmpty(fio))
      order.setFio(fio)
    if (!Strings.isNullOrEmpty(phone))
      order.setPhone(phone)
    if (!Strings.isNullOrEmpty(email))
      order.setEmail(email)
    if (!Strings.isNullOrEmpty(address))
      order.setAddress(address)
    if (!Strings.isNullOrEmpty(notes))
      order.setNotes(notes)

    if (courier) {
      order.setCourier(courier)
      order.setEmoney(!courier)
    }

    if (emoney) {
      order.setEmoney(emoney)
      order.setCourier(!emoney)
    }

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
      cartItems.each {ProductEntity productEntity ->
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
