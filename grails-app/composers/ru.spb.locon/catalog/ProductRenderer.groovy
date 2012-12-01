package ru.spb.locon.catalog

import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell

import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.ImageService
import org.zkoss.zul.Image
import org.zkoss.zul.Div

class ProductRenderer implements ListitemRenderer<ProductEntity> {

  ImageService imageSyncService = (ImageService) SpringUtil.getApplicationContext().getBean("imageService")

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {
    listitem.setValue(t)

    //Ячейка с каритнкой
    Listcell imageCell = new Listcell()
    Image image = imageSyncService.getProductImage(t, "150")
    Div imageDiv = new Div()
    imageDiv.setSclass("imageBox")
    imageDiv.appendChild(image)
    imageCell.appendChild(imageDiv)
    imageCell.setParent(listitem)

    //Ячейка с товаром
    Listcell descCell = new ProductCell(t)
    descCell.setParent(listitem)
  }

}

