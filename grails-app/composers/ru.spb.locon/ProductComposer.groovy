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
import ru.spb.locon.domain.DomainUtils

import org.zkoss.zkplus.spring.SpringUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import ru.spb.locon.importer.DirUtils
import org.zkoss.zhtml.Img
import org.zkoss.zul.Image

class ProductComposer extends SelectorComposer<Window> {

  @Wire("label")
  List<XulElement> outputs

  @Wire("#productImg")
  Image productImage

  @Wire("#cartButton")
  Button cartButton

  @Wire("#backButton")
  Button backButton

  Long productId

  String catalogPath = ConfigurationHolder.config.locon.store.catalog
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  @Override
  public void doAfterCompose(Window window) {
    super.doAfterCompose(window)
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    ProductEntity product = ProductEntity.get(productId)

    String path = "${catalogPath}\\${product.imagePath}\\"
    DirUtils dirUtils = new DirUtils()
    dirUtils.findImages(path)
    productImage.setSrc("\\${path}\\1.jpg")

    cartButton.addEventListener(Events.ON_CLICK, new EventListener() {

      @Override
      void onEvent(Event t) {
        //значения выбранных товаров храняться в cookie.
        cartService.addToCart(product)

      }
    })

    backButton.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) {
        String category = Executions.getCurrent().getParameter("category")
        if (category != null)
          Executions.sendRedirect("/shop/catalog?category=${Long.parseLong(category)}")
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
