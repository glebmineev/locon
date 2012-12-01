package ru.spb.locon.admin

import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import java.lang.reflect.Field
import ru.spb.locon.annotation.FieldInfo
import org.zkoss.zul.Image
import org.zkoss.zul.Listcell
import org.zkoss.zul.Div
import ru.spb.locon.ImageService
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 22:02
 */
class ProductsRenderer implements ListitemRenderer<ProductEntity> {

  ImageService imageSyncService = ApplicationHolder.getApplication().getMainContext().getBean("imageService")

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {

    Listcell imageCell = new Listcell()
    Image image = imageSyncService.getProductImage(t, "150")
    Div imageDiv = new Div()
    imageDiv.setSclass("imageBox")
    image.setParent(imageDiv)
    imageCell.appendChild(imageDiv)
    imageCell.setParent(listitem)

    ProductEntity.declaredFields.each {Field field ->
      FieldInfo annotation = field.getAnnotation(FieldInfo)

      if (annotation != null) {
        String value = t."${field.name}"
        Listcell cell = new Listcell()
        if (value != null)
          cell.setLabel(value.toString())
        cell.setParent(listitem)
      }

    }
    Listcell actionСell = new Listcell("Действия")
    actionСell.setParent(listitem)
  }

}
