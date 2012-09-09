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
import org.zkoss.html.JavaScript
import org.zkoss.zk.ui.util.Clients
import xml.CartXML

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
        CartXML cartXML = new CartXML(cookieService.get("cart"))
        cartXML.addProduct(productId)
        cookieService.set(Executions.current.nativeResponse,"cart", cartXML.getXml(), 604800)
        Integer allCounts = cartXML.getAllCountProducts()
        Float allPrices = cartXML.getAllPricesProducts()
        //обновление дива корзины через ajax.
        Clients.evalJavaScript("\$('#countProducts').html('" + Integer.toString(allCounts) + "')")
        Clients.evalJavaScript("\$('#priceProducts').html('" + allPrices + "')")
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
