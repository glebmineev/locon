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
import cart.CartUtils

class ProductComposer extends SelectorComposer<Window> {

  @Wire("label")
  List<XulElement> outputs

  @Wire("#cartButton")
  Button cartButton

  @Wire("#backButton")
  Button backButton

  Long productId

  @Override
  public void doAfterCompose(Window window) {
    super.doAfterCompose(window)
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    Long categoryId = Long.parseLong(Executions.getCurrent().getParameter("category"))
    cartButton.addEventListener(Events.ON_CLICK, new EventListener() {

      @Override
      void onEvent(Event t) {
        //значения выбранных товаров храняться в cookie.
        CartUtils utils = new CartUtils()
        utils.addToCart(productId)
        utils.recalculateCart()
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
