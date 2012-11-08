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
import org.zkoss.zhtml.Br

/**
 * User: Gleb
 * Date: 20.10.12
 * Time: 0:13
 */
class RecommendedComposer extends GrailsComposer {

  Table products

  InitService initService = (InitService) SpringUtil.getApplicationContext().getBean("initService")
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  def afterCompose = {Window window ->

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
      //цена.
      Label price = new Label(Float.toString(product.price))

      //Кнопка купить.
      Button buy = new Button("В корзину")
      buy.setStyle("margin: 5px;")
      buy.setAttribute("entity", product)
      buy.addEventListener(Events.ON_CLICK, buyListener)

      cell.appendChild(image)
      cell.appendChild(new Br())
      //cell.appendChild(name)
      cell.appendChild(new Br())
      cell.appendChild(price)
      cell.appendChild(new Br())
      cell.appendChild(buy)

      //cell.appendChild(vbox)

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
