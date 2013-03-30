package ru.spb.locon.catalog

import org.zkoss.zk.ui.Execution
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
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
    ProductEntity product = ProductEntity.get(t.id)
    listitem.setValue(product)

    //Ячейка с каритнкой
    Listcell imageCell = new Listcell()
    Image image = imageSyncService.getProductImage(product, "150")
    Div imageDiv = new Div()
    imageDiv.setSclass("imageBox")
    imageDiv.appendChild(image)
    imageCell.appendChild(imageDiv)
    imageCell.setParent(listitem)

    //Ячейка с товаром
    Listcell descCell = new ProductCell(product)
    descCell.setParent(listitem)

    descCell.addEventListener(Events.ON_CLICK, new org.zkoss.zk.ui.event.EventListener(){
      @Override
      void onEvent(Event event) throws Exception {
        Executions.sendRedirect("/shop/product?product=${product.id}")
      }
    })

  }

}

