package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import ru.spb.locon.OrderProductEntity
import ru.spb.locon.ProductEntity
import org.zkoss.zul.Listcell

import org.zkoss.zul.Label

import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.ImageService

/**
 * User: Gleb
 * Date: 07.10.12
 * Time: 15:43
 */
class OrderItemRenderer implements ListitemRenderer<OrderProductEntity> {

  ImageService imageSyncService = (ImageService) SpringUtil.getApplicationContext().getBean("imageService")

  @Override
  void render(org.zkoss.zul.Listitem listitem, OrderProductEntity t, int i) {
    listitem.setValue(t)

    ProductEntity product = t.getProduct()
    Long count = t.getCountProduct()

    //Рисунок
    Listcell imageCell = new Listcell()
    imageCell.appendChild(imageSyncService.getProductImage(product, "100"))
    imageCell.setParent(listitem)

    //Товар
    Listcell productCell = new Listcell(product.name)
    productCell.setParent(listitem)

    //Цена
    Float price = product.price != null ? product.price : 0.0
    Listcell priceCell = new Listcell(price.toString())
    priceCell.setParent(listitem)

    //Количество
    Listcell productsCount = new Listcell()
    productsCount.appendChild(new Label(count.toString()))
    productsCount.setParent(listitem)

    //Стоимость
    Float roundPrice = (product.price * Float.parseFloat(count.toString()))
    Listcell allPriceCell = new Listcell(roundPrice.toString())
    allPriceCell.setParent(listitem)

  }

}
