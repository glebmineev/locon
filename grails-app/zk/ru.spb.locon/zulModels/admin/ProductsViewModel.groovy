package ru.spb.locon.zulModels.admin

import com.google.common.base.Strings
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.*
import ru.spb.locon.ProductEntity
import ru.spb.locon.zulModels.admin.filters.data.FilterBean
import ru.spb.locon.zulModels.admin.filters.data.FilterTypes
import ru.spb.locon.zulModels.admin.filters.IFilterCallback
import ru.spb.locon.zulModels.admin.filters.data.ObjectFilter
import ru.spb.locon.annotation.FieldInfo
import org.zkoss.zk.ui.event.EventListener
import ru.spb.locon.wrappers.ProductModel

import java.lang.reflect.Field

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/6/13
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
class ProductsViewModel {

  //Фильтры (поле, значение)
  Map<Field, Object> filterMap = new HashMap<Field, Object>()
  List<FilterBean> filters = new ArrayList<FilterBean>()
  //Модель гриды.
  ListModelList<ProductModel> products

  @Init
  public void init() {

    ProductEntity.declaredFields.each { Field field ->
      FieldInfo annotation = field.getAnnotation(FieldInfo)
      if (annotation != null && annotation.isFilter()) {

        FilterBean bean = new FilterBean(new IFilterCallback() {
          @Override
          void changed(ObjectFilter objectFilter) {
            filterMap.put(objectFilter.field, objectFilter)
            rebuildModel()
          }
        }, field, annotation.type())

        filters.add(bean)

      }

    }

    rebuildModel()
    products.setMultiple(true)
  }

  /**
   * Формирует окошко с увеличением цены товаров.
   * @param event
   */
  @Command
  public void changePrice() {

    Map<Object, Object> params = new HashMap<Object, Object>()
    params.put("selectedProducts", products.getSelection())
    Window wnd = Executions.createComponents("/zul/admin/windows/incrementProductsWnd.zul", null, params) as Window
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)

  }

  @Command
  public void changeEditableStatus(@BindingParam("wrapper") ProductModel wrapper) {
    wrapper.setEditingStatus(!wrapper.getEditingStatus())
    refreshRowTemplate(wrapper);
  }

  @Command
  public void refreshRowTemplate(ProductModel wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "editingStatus");
  }

  @Command
  public void saveProduct(@BindingParam("wrapper") ProductModel wrapper) {

    ProductEntity.withTransaction {
      ProductEntity toSave = ProductEntity.get(wrapper.getProductID())
      toSave.setName(wrapper.getName())
      toSave.setArticle(wrapper.getArticle())
      toSave.setManufacturer(wrapper.getManufacturers().getSelection().first())
      toSave.setPrice(wrapper.getPrice())
      toSave.setCountToStock(wrapper.getCountToStock())
      if (toSave.validate())
        toSave.save(flush: true)
    }

    changeEditableStatus(wrapper)
  }

  @Command
  public void deleteProduct(@BindingParam("wrapper") ProductModel wrapper) {
    ProductEntity.withTransaction {
      ProductEntity toDelete = ProductEntity.get(wrapper.getProductID())
      toDelete.delete(flush: true)
    }
    rebuildModel()
  }

  @Command
  public void cancelEditing(@BindingParam("wrapper") ProductModel wrapper) {
    wrapper.restore()
    changeEditableStatus(wrapper)
  }

  void rebuildModel() {
    if (products != null) {
      products.clear()
      products.addAll(fillWrappers(getModelByFilters()))
    } else {
      products = new BindingListModelList<ProductModel>(fillWrappers(ProductEntity.list(sort: "name")), true)
    }
  }

  /**
   * Метод создает обертки на основе сущностей.
   * @param list - список оригиналов.
   * @return список оберток.
   */
  List<ProductModel> fillWrappers(def list) {
    List<ProductModel> target = new ArrayList<ProductModel>()
    list.each { ProductEntity it ->
      ProductModel wrapper = new ProductModel(it)
      wrapper.setEditingStatus(false)
      wrapper.setMemento(wrapper.clone() as ProductModel)
      target.add(wrapper)
    }
    return target
  }

  List<ProductEntity> getModelByFilters() {
    return ProductEntity.createCriteria().list {
      filterMap.each { Field field, ObjectFilter filter ->

        FieldInfo annotation = field.getAnnotation(FieldInfo)

        if (annotation.type() == FilterTypes.TEXT_FIELD &&
            Strings.emptyToNull(filter.startValue as String))
          ilike(field.name, "%${(String) filter.startValue}%")

        if (annotation.type() == FilterTypes.COMBO_FIELD &&
            filter.startValue != null)
          sqlRestriction("product_${field.name}_id = '${filter.startValue.id}'")

        if (annotation.type() == FilterTypes.NUMBER_FIELD &&
            Strings.emptyToNull(filter.startValue as String))
          sqlRestriction("cast(product_${field.name} as varchar) like '%${(String) filter.startValue}%'")

        if (annotation.type() == FilterTypes.MEASURE_FIELD) {
          if (Strings.emptyToNull(filter.startValue as String))
            gt("${field.name}", filter.startValue as Long)
          if (Strings.emptyToNull(filter.endValue as String))
            le("${field.name}", filter.endValue as Long)

        }

      }
    }
  }

}
