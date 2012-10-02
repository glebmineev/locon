package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell
import org.zkoss.zul.Textbox

import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem

import org.zkoss.zk.ui.event.InputEvent
import org.zkoss.zul.ListModelList
import ru.spb.locon.CartEntity

import ru.spb.locon.CartProductEntity
import cart.CartUtilsOld

/**
 * User: Gleb
 * Date: 23.09.12
 * Time: 14:54
 */
class CartRenderer implements ListitemRenderer {

  ListModelList<CartProductEntity> productsModel
  CartUtilsOld utils = new CartUtilsOld()

  CartRenderer(ListModelList<CartProductEntity> productsModel) {
    this.productsModel = productsModel
  }

  @Override
  void render(org.zkoss.zul.Listitem listitem, Object object, int i) {
    CartProductEntity entity = (CartProductEntity) object
    listitem.setValue(entity)

    //Товар
    Listcell productCell = new Listcell(entity.product.name)
    productCell.setParent(listitem)

    //Цена
    Float price = entity.product.price != null ? entity.product.price : 0.0
    Listcell priceCell = new Listcell(price.toString())
    priceCell.setParent(listitem)

    //Количество
    Listcell productsCount = new Listcell()
    Textbox textbox = new Textbox()
    textbox.addEventListener(Events.ON_CHANGE, updateListener)

    textbox.setValue(Integer.toString(entity.productsCount))
    productsCount.appendChild(textbox)
    productsCount.setParent(listitem)

    //Стоимость
    Integer counts = entity.productsCount != null ? entity.productsCount : 0
    Listcell allPriceCell = new Listcell(Double.toString(price * counts))
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
      CartProductEntity value = (CartProductEntity) parent.getValue()
      value.delete()
      removeFromModel(value)
      utils.recalculateCart()

    }
  }

  EventListener updateListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      InputEvent event = (InputEvent) t
      CartProductEntity.withTransaction {
        Component button = event.target
        Listitem parent = (Listitem) button.parent.parent
        CartProductEntity value = (CartProductEntity) parent.getValue()
        String newValue = event.getValue()

        CartProductEntity.withNewTransaction {
          value.setProductsCount(Integer.parseInt(newValue))
          value.merge(flush: true)
        }
        rebuildModel()
        utils.recalculateCart()
      }
    }
  }

  private void rebuildModel(){
    CartEntity cart = utils.getCart()
    productsModel.clear()
    productsModel.addAll(cart.listCartProduct)
  }

  private void removeFromModel(CartProductEntity value) {
    productsModel.remove(value)
  }

}
