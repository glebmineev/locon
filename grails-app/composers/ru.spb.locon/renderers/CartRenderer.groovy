package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell

import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem

import ru.spb.locon.cart.CartItem
import ru.spb.locon.ProductEntity
import org.zkoss.zkplus.databind.BindingListModelList
import ru.spb.locon.CartService

import org.zkoss.zul.Label
import org.zkoss.zul.Hbox
import org.zkoss.zul.Image
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Window
import org.codehaus.groovy.grails.commons.ApplicationHolder
import java.math.RoundingMode

/**
 * User: Gleb
 * Date: 23.09.12
 * Time: 14:54
 */
class CartRenderer implements ListitemRenderer<CartItem> {

  BindingListModelList<CartItem> cartModel

  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService")

  CartRenderer(BindingListModelList<CartItem> cartModel) {
    this.cartModel = cartModel
  }

  @Override
  void render(Listitem listitem, CartItem object, int i) {
    CartItem cartItem = (CartItem) object
    listitem.setValue(cartItem)

    ProductEntity product = cartItem.getProduct()
    Long count = cartItem.getCount()
    //Товар
    Listcell productCell = new Listcell(product.name)
    productCell.setParent(listitem)

    //Цена
    Float price = product.price != null ? product.price : 0.0
    Listcell priceCell = new Listcell(price.toString())
    priceCell.setParent(listitem)

    //Количество
    Listcell productsCount = new Listcell()
    Hbox hbox = new Hbox()
    Label countLabel = new Label()
    countLabel.setId("good_${cartItem.product}")
    countLabel.setValue(Long.toString(count))
    Image plus = new Image("/images/plus.png")
    plus.addEventListener(Events.ON_CLICK, plusListener)
    Image minus = new Image("/images/minus.png")
    minus.addEventListener(Events.ON_CLICK, minusListener)
    hbox.appendChild(countLabel)
    hbox.appendChild(plus)
    hbox.appendChild(minus)
    productsCount.appendChild(hbox)
    productsCount.setParent(listitem)

    //Стоимость
    Float roundPrice = (product.price * Float.parseFloat(count.toString()))
    BigDecimal rounded = new BigDecimal(roundPrice).setScale(1, RoundingMode.HALF_DOWN).floatValue()

    Listcell allPriceCell = new Listcell()
    Label allPriceLabel = new Label(roundPrice.toString())
    allPriceLabel.setId("price_${cartItem.product}")
    allPriceLabel.setParent(allPriceCell)
    allPriceCell.setParent(listitem)

    //Дейсвия
    Button deleteFromCart = new Button("Удалить")
    deleteFromCart.addEventListener(Events.ON_CLICK, deleteButtonListener)

    Listcell actionsCell = new Listcell()
    actionsCell.appendChild(deleteFromCart)
    actionsCell.setParent(listitem)
  }

  EventListener deleteButtonListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component button = t.target
      Listitem parent = (Listitem) button.parent.parent
      CartItem value = (CartItem) parent.getValue()
      cartModel.remove(value)
      cartService.removeFromCart(value)
    }
  }

  EventListener plusListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Image image = (Image) t.target
      Listitem parent = (Listitem) image.parent.parent.parent
      CartItem value = (CartItem) parent.getValue()
      cartService.addToCart(value.product)

      //изменяем начение количества.
      Window window = (Window) image.getPage().getFirstRoot()
      Label countLabel = (Label) window.getFellow("good_${value.product}")
      countLabel.setValue(Long.toString(value.count + 1))

      //изменяем item модели.
      CartItem changingItem = (CartItem) cartModel.get((cartModel.indexOf(value)))
      changingItem.setCount(value.count + 1)

      float price = value.product.price
      Float resultPrice = (price * value.count)
      BigDecimal rounded = new BigDecimal(resultPrice).setScale(1, RoundingMode.HALF_DOWN).floatValue()

      //изменяем начение цены.
      Label allPriceLabel = (Label) window.getFellow("price_${value.product}")
      allPriceLabel.setValue(rounded.toString())
    }
  }

  EventListener minusListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component image = t.target
      Listitem parent = (Listitem) image.parent.parent.parent
      CartItem value = (CartItem) parent.getValue()
      long count = value.count
      if (value.count != 0) {
        cartService.decrementProduct(value)
        Window window = (Window) image.getPage().getFirstRoot()
        Label countLabel = (Label) window.getFellow("good_${value.product}")
        countLabel.setValue(Long.toString(count - 1))

        CartItem changingItem = (CartItem) cartModel.get((cartModel.indexOf(value)))
        changingItem.setCount(value.count - 1)

        float price = value.product.price
        Float resultPrice = (price * value.count)
        BigDecimal rounded = new BigDecimal(resultPrice).setScale(1, RoundingMode.HALF_DOWN).floatValue()

        //изменяем начение цены.
        Label allPriceLabel = (Label) window.getFellow("price_${value.product}")
        allPriceLabel.setValue(rounded.toString())

      }
    }
  }

  private void updateCartModel(){
    Clients.showBusy("Подождите")
    cartModel.clear()
    cartModel.addAll(cartService.getCartProducts())
  }

}


