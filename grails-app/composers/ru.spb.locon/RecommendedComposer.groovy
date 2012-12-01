package ru.spb.locon

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zkplus.spring.SpringUtil

import org.zkoss.zul.Label
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zhtml.Table
import org.zkoss.zhtml.Tr
import org.zkoss.zhtml.Td
import org.zkoss.zhtml.Br
import org.zkoss.zul.Div
import org.zkoss.zk.ui.Executions

/**
 * User: Gleb
 * Date: 20.10.12
 * Time: 0:13
 */
class RecommendedComposer extends GrailsComposer {

  Table products

  InitService initService = (InitService) SpringUtil.getApplicationContext().getBean("initService")
  ImageService imageSyncService = (ImageService) SpringUtil.getApplicationContext().getBean("imageService")
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  def afterCompose = {Window window ->

    Tr row = new Tr()

    List<ProductEntity> recommended = initService.recommended
    recommended.each {ProductEntity product ->
      Td cell = new Td()
      cell.setSclass("recommendedItem")
      //картинка товара.

      Div nameContainer = new Div()
      nameContainer.setSclass("recommendedName")
      //наименование.
      Label name = new Label(product.name)
      name.setParent(nameContainer)
      //цена.
      Label price = new Label(Float.toString(product.price))

      //Кнопка купить.
      Button buy = new Button("В корзину")
      buy.setStyle("margin: 5px;")
      buy.setAttribute("entity", product)
      buy.addEventListener(Events.ON_CLICK, buyListener)

      cell.appendChild(new Br())
      cell.appendChild(imageSyncService.getProductImage(product, "150"))
      cell.appendChild(new Br())
      cell.appendChild(nameContainer)
      cell.appendChild(new Br())
      cell.appendChild(price)
      cell.appendChild(new Br())
      cell.appendChild(buy)

      cell.addEventListener(Events.ON_CLICK, new EventListener() {

        @Override
        void onEvent(Event t) {
          Executions.sendRedirect("/shop/product?product=${product.id}")
        }

      })
      
      row.appendChild(cell)
    }

    products.appendChild(row)
    window.appendChild(products)
  }

  EventListener buyListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      ProductEntity product = (ProductEntity) ((Button) t.target).getAttribute("entity")
      cartService.addToCart(product)
    }
  }

}
