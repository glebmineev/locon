package ru.spb.locon.admin

import org.zkoss.image.AImage
import ru.spb.locon.ProductEntity
import org.zkoss.zul.ListitemRenderer
import java.lang.reflect.Field
import ru.spb.locon.annotation.FieldInfo
import org.zkoss.zul.*
import ru.spb.locon.ImageService
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 22:02
 */
class ProductsRenderer implements ListitemRenderer<ProductEntity> {

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService")

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductEntity t, int i) {

    listitem.setValue(t)

    Listcell imageCell = new Listcell()
    Image image = new Image()
    AImage aImage = imageService.getImageFile(t, "150")
    image.setContent(aImage)

    Div imageDiv = new Div()
    imageDiv.setWidth("160px")
    imageDiv.setAlign("center")
    imageDiv.setSclass("imageBox")
    image.setParent(imageDiv)
    imageCell.appendChild(imageDiv)
    imageCell.setParent(listitem)

    ProductEntity.declaredFields.each {Field field ->
      FieldInfo annotation = field.getAnnotation(FieldInfo)
      if (annotation != null && annotation.isFilter()) {
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
