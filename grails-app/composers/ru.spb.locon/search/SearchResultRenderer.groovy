package ru.spb.locon.search

import org.zkoss.zul.ListitemRenderer
import ru.spb.locon.ProductEntity
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.ImageSyncService
import org.zkoss.zul.Listcell
import org.zkoss.zul.Label

/**
 * User: Gleb
 * Date: 14.11.12
 * Time: 13:32
 */
class SearchResultRenderer implements ListitemRenderer<ProductEntity>{

  String keyword

  ImageSyncService imageSyncService = ApplicationHolder.getApplication().getMainContext().getBean("imageSyncService")

  SearchResultRenderer(String keyword) {
    this.keyword = keyword
  }

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {

    listitem.setValue(t)

    //Картинка товара
    Listcell imageCell = new Listcell()
    imageCell.appendChild(imageSyncService.getProductImage(t, "100"))
    imageCell.setParent(listitem)

    //Наименование товара
    Listcell nameCell = new Listcell()
    nameCell.appendChild(new Label(t.name))
    nameCell.setParent(listitem)

    //Описание товара
    Listcell descCell = new Listcell()
    descCell.appendChild(new Label(shorting(t.description)))
    descCell.setParent(listitem)

    //Применение товара
    Listcell usageCell = new Listcell()
    usageCell.appendChild(new Label(shorting(t.usage)))
    usageCell.setParent(listitem)

    //Цена товара
    Listcell priceCell = new Listcell()
    priceCell.appendChild(new Label(t.price.toString()))
    priceCell.setParent(listitem)

  }


  String shorting(String source){
    String result
    /*int index = source.indexOf(keyword)
    int start = 0
    int end = (index + 20)
    if ((index - 20) > 0)
      start = (index - 20)
    else
      start = index

    result = source.substring(start, end)*/

    if (source != null && source.length() > 100)
      result = "${source.substring(0, 100)} ..."
    else
      result = source
    return result
  }
  
}
