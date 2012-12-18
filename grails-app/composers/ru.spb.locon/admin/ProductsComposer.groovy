package ru.spb.locon.admin

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.*
import org.zkoss.zul.Listbox
import ru.spb.locon.ProductEntity
import java.lang.reflect.Field
import ru.spb.locon.annotation.FieldInfo
import org.zkoss.zkplus.databind.BindingListModelList
import ru.spb.locon.admin.filters.TextBoxFilter
import ru.spb.locon.admin.filters.IFilterCallback
import ru.spb.locon.admin.filters.ObjectFilter

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 13:34
 */
class ProductsComposer extends GrailsComposer {

  Listbox products
  BindingListModelList<ProductEntity> productsModel
  Map<Field, Object> filterMap = new HashMap<Field, Object>()

  def afterCompose = {Window window ->

    Auxhead auxhead = new Auxhead()
    auxhead.setSclass("category-center")
    auxhead.appendChild(new Auxheader())

    ProductEntity.declaredFields.each {Field field ->

      Auxheader auxheader = new Auxheader()
      auxheader.setSclass("category-center")

      FieldInfo annotation = field.getAnnotation(FieldInfo)
      Hbox hbox = new Hbox()

      if (annotation != null && annotation.isFilter()) {
        // берем картинку фильтра
        Image funnel = new Image("/images/funnel.png")
        TextBoxFilter textBoxFilter = new TextBoxFilter(
            new IFilterCallback() {

              @Override
              void changed(ObjectFilter objectFilter) {
                filterMap.put(objectFilter.field, objectFilter.value)
                rebuildModel()
              }

            }, field)

        hbox.appendChild(funnel)
        hbox.appendChild(textBoxFilter)
        auxheader.appendChild(hbox)
        auxhead.appendChild(auxheader)
      }

    }

    products.appendChild(auxhead)
    products.setItemRenderer(new ProductsRenderer())
    rebuildModel()

    products.addEventListener(Events.ON_CLICK, productsListener)

  }

  EventListener productsListener = new EventListener() {

    @Override
    void onEvent(Event t) {
      Listitem item = products.getSelectedItem()
      Executions.sendRedirect("/admin/productItem?product=${item.value.id}")
    }

  }

  void rebuildModel() {
    if (productsModel != null) {
      productsModel.clear()
      productsModel.addAll(getModelByFilters())
    } else {
      productsModel = new BindingListModelList<ProductEntity>(ProductEntity.list(), true)
    }
    products.setModel(productsModel)
  }

  List<ProductEntity> getModelByFilters() {
    return ProductEntity.createCriteria().list {
      filterMap.each {Field field, Object value ->
        if (field.type.name.contains("String"))
          ilike(field.name, "%${(String) value}%")
        else {
          sqlRestriction("cast(product_${field.name} as varchar) like '%${(String) value}%'")
        }
      }
    }
  }

}
