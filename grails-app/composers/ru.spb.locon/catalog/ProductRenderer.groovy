package ru.spb.locon.catalog

import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell

import org.zkoss.zul.Image
import org.zkoss.zul.Div
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Listitem

import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Label
import org.zkoss.zul.Vbox
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.CartService
import ru.spb.locon.importer.ConverterRU_EN
import ru.spb.locon.importer.ImageHandler
import org.zkoss.zk.ui.Executions

class ProductRenderer implements ListitemRenderer<ProductEntity> {

  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {
    listitem.setValue(t)

    //Ячейка с каритнкой
    Listcell imageCell = new Listcell()
    imageCell.appendChild(getImage(t))
    imageCell.setParent(listitem)

    //Ячейка с товаром
    Listcell descCell = new Listcell()
    descCell.appendChild(getProductCell(t))
    descCell.setParent(listitem)
  }

  private Image getImage(ProductEntity entity) {
    Image image = new Image()
    image.setWidth("100px")
    image.setHeight("100px")
    String imagePath = ConverterRU_EN.translit(entity.imagePath)
    String applicationPath = Executions.current.nativeRequest.getSession().getServletContext().getRealPath("/")
    String path = "${applicationPath}\\images\\catalog\\${imagePath}"
    ImageHandler dirUtils = new ImageHandler()
    List<String> images = dirUtils.findImages(path)
    if ( images.size() > 0)
      image.setSrc("/images/catalog/${imagePath}/1-100.jpg")
    else
      image.setSrc("/images/empty.png")
    return image
  }

  EventListener addToCartListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component button = t.target
      Listitem parent = (Listitem) button.parent.parent.parent.parent
      ProductEntity value = (ProductEntity) parent.getValue()
      cartService.addToCart(value)
    }
  }

  private Div getProductCell(ProductEntity entity){
    
    Div productCell = new Div()

    Vbox vBox = new Vbox()
    vBox.setAlign("left")

    Label manufacturer = new Label(entity.manufacturer.name)
    manufacturer.setStyle("font-size: 14px;margin-top: 5px;margin-bottom: 5px;")

    Label productName = new Label(entity.name)
    productName.setStyle("font-size: 18px;margin-top: 5px;margin-bottom: 5px;")

    Label price = new Label()
    price.setStyle("font-size: 14px;margin-bottom: 5px;")
    price.setValue("Цена: ${Float.toString(entity.price)}")

    Button addToCart = new Button("Добавить в корзину")
    addToCart.addEventListener(Events.ON_CLICK, addToCartListener)

    vBox.appendChild(manufacturer)
    vBox.appendChild(productName)
    vBox.appendChild(price)
    vBox.appendChild(addToCart)

    productCell.appendChild(vBox)

    return productCell
  }

}

