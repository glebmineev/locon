package ru.spb.locon

import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Window
import org.zkoss.zul.Label

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zul.impl.XulElement
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import domain.DomainUtils
import org.zkoss.zkplus.spring.SpringUtil
import com.studentuniverse.grails.plugins.cookie.services.CookieService
import org.zkoss.zk.ui.util.Clients

class ProductComposer extends SelectorComposer<Window> {

  @Wire("label")
  List<XulElement> outputs

  @Wire("#cartButton")
  Button cartButton

  @Wire("#backButton")
  Button backButton

  Long productId

  CookieService cookieService = (CookieService) SpringUtil.getApplicationContext().getBean("cookieService")

  @Override
  public void doAfterCompose(Window window) {
    super.doAfterCompose(window)
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    Long categoryId = Long.parseLong(Executions.getCurrent().getParameter("category"))
    cartButton.addEventListener(Events.ON_CLICK, new EventListener() {

      @Override
      void onEvent(Event t) {
        //значения выбранных товаров храняться в cookie.
        //CartXML cartXML = new CartXML(cookieService.get("cart"))
        String uuid = cookieService.get("cart_uuid")
        CartEntity cart = null
        if (uuid != null && !uuid.isEmpty()) {
          refreshCart(uuid)
          cart = CartEntity.findByUuid(uuid)
        } else {
          cart = saveNewCart()
        }

        Collection<CartProductEntity> result = cart.listCartProduct

        Clients.evalJavaScript("\$('#countProducts').html('" + result.size() + "')")
        float allPrices = 0.0
        result.product.each {ProductEntity product ->
          float price = product.price == null ? 0.0 : product.price
          allPrices = allPrices + price
        }
        Clients.evalJavaScript("\$('#priceProducts').html('" + allPrices + "')")

      }

      private CartEntity saveNewCart() {
        CartEntity cart = null
        CartProductEntity.withTransaction {
          String uuid = UUID.randomUUID().toString().replaceAll("-", "_")
          cart = new CartEntity(dateCreate: new Date(), uuid: uuid)
          cart.save()
          CartProductEntity link = new CartProductEntity(cart: cart, product: ProductEntity.get(productId))
          if (link.validate())
            link.save(flush: true)
          else
            throw new Exception("Ошибка сохранения связки корзины с продуктом.")
          //задам id корзины.
          cookieService.set(Executions.current.nativeResponse, "cart_uuid", uuid, 604800)
        }
        return cart
      }

      private void refreshCart(String uuid) {
        CartProductEntity.withTransaction {
          CartEntity cart = CartEntity.findByUuid(uuid)
          if (cart == null)
            cart = new CartEntity(uuid: uuid, dateCreate: new Date())
          cart.save()
          CartProductEntity link = new CartProductEntity(cart: cart, product: ProductEntity.get(productId))
          if (link.validate())
            link.save(flush: true)
          else
            throw new Exception("Ошибка сохранения связки корзины с продуктом.")
        }
      }
    })

    backButton.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) {
        Executions.sendRedirect("/shop/catalog?category=${categoryId}")
      }
    })

    initializeFields()
  }

  /*
   * метод проставляет занчения товара.
   */

  private void initializeFields() {
    ProductEntity product = ProductEntity.get(productId)
    if (product != null) {
      outputs.each {XulElement element ->
        String fieldName = element.id
        if (element instanceof Label &&
            !fieldName.isEmpty()) {
          Label label = (Label) element
          Object value = product."${fieldName}"
          label.setValue(DomainUtils.parseTo(value))
        }
      }
    }
  }

}
