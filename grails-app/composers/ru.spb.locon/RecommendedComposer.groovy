package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zkplus.spring.SpringUtil

import org.zkoss.zul.Image
import org.zkoss.zul.Label
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zhtml.Table
import org.zkoss.zhtml.Tr
import org.zkoss.zhtml.Td
import org.zkoss.zul.Vbox

/**
 * User: Gleb
 * Date: 20.10.12
 * Time: 0:13
 */
class RecommendedComposer extends GrailsComposer {
  
  InitService initService = (InitService) SpringUtil.getApplicationContext().getBean("initService")
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  def afterCompose = {Window window ->

    Vbox hbox = new Vbox()
    hbox.setWidth("100%")
    hbox.setAlign("center")
    hbox.setStyle("background: #f6f6f6;border-bottom: 1px solid #346F97;")
    Label headerLabel = new Label("Популярные")
    headerLabel.setStyle("font-size: 20px;")
    hbox.appendChild(headerLabel)
    window.appendChild(hbox)

    Table products = new Table()
    products.setStyle("width:100%;")
    Tr row = new Tr()

    List<ProductEntity> recommended = initService.recommended
    recommended.each {ProductEntity product ->
      Td cell = new Td()
      cell.setSclass("recommendedItem")
      Vbox vbox = new Vbox()
      vbox.setAlign("center")
      //картинка товара.
      Image image = new Image()
      image.setStyle("border: 1px solid #f6f6f6;")
      image.setSrc("/images/empty.png")

      //наименование.
      Label name = new Label(product.name)
      name.setWidth("217px")
      //цена.
      Label price = new Label(Float.toString(product.price))

      //Кнопка купить.
      Button buy = new Button("В корзину")
      buy.setStyle("margin: 5px;")
      buy.setAttribute("entity", product)
      buy.addEventListener(Events.ON_CLICK, buyListener)

      vbox.appendChild(image)
      vbox.appendChild(name)
      vbox.appendChild(price)
      vbox.appendChild(buy)

      cell.appendChild(vbox)

      row.appendChild(cell)
    }

    products.appendChild(row)
    window.appendChild(products)
  }

  EventListener buyListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      ProductEntity product = ((Button) t.target).getAttribute("entity")
      cartService.addToCart(product)
    }
  }

}
