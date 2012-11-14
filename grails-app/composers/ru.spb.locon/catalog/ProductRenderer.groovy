package ru.spb.locon.catalog

import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import org.zkoss.zul.Listcell

import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.ImageSyncService

class ProductRenderer implements ListitemRenderer<ProductEntity> {

  ImageSyncService imageSyncService = (ImageSyncService) SpringUtil.getApplicationContext().getBean("imageSyncService")

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {
    listitem.setValue(t)

    //Ячейка с каритнкой
    Listcell imageCell = new Listcell()
    imageCell.appendChild(imageSyncService.getProductImage(t, "100"))
    imageCell.setParent(listitem)

    //Ячейка с товаром
    Listcell descCell = new ProductCell(t)
    descCell.setParent(listitem)
  }

}

