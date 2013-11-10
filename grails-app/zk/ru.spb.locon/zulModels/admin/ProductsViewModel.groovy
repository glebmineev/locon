package ru.spb.locon.zulModels.admin

import com.google.common.base.Strings
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.*
import ru.spb.locon.ProductEntity
import ru.spb.locon.zulModels.admin.filters.data.FilterBean
import ru.spb.locon.zulModels.admin.filters.data.FilterTypes
import ru.spb.locon.zulModels.admin.filters.IFilterCallback
import ru.spb.locon.zulModels.admin.filters.data.ObjectFilter
import ru.spb.locon.annotation.FieldInfo
import ru.spb.locon.wrappers.ProductWrapper

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
  ListModelList<ProductWrapper> products

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
  public void changeEditableStatus(@BindingParam("wrapper") ProductWrapper wrapper) {
    wrapper.setEditingStatus(!wrapper.getEditingStatus())
    refreshRowTemplate(wrapper);
  }

  @Command
  public void refreshRowTemplate(ProductWrapper wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "editingStatus");
  }

  @Command
  public void saveProduct(@BindingParam("wrapper") ProductWrapper wrapper) {

    ProductEntity.withTransaction {
      ProductEntity toSave = ProductEntity.get(wrapper.getId())
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
  public void deleteProduct(@BindingParam("wrapper") ProductWrapper wrapper) {
    ProductEntity.withTransaction {
      ProductEntity toDelete = ProductEntity.get(wrapper.getId())
      toDelete.delete(flush: true)
    }
    rebuildModel()
  }

  @Command
  public void cancelEditing(@BindingParam("wrapper") ProductWrapper wrapper) {
    wrapper.restore()
    changeEditableStatus(wrapper)
  }

  void rebuildModel() {
    if (products != null) {
      products.clear()
      products.addAll(fillWrappers(getModelByFilters()))
    } else {
      products = new BindingListModelList<ProductWrapper>(fillWrappers(ProductEntity.list(sort: "name")), true)
    }
  }

  /**
   * Метод создает обертки на основе сущностей.
   * @param list - список оригиналов.
   * @return список оберток.
   */
  List<ProductWrapper> fillWrappers(def list) {
    List<ProductWrapper> target = new ArrayList<ProductWrapper>()
    list.each { ProductEntity it ->
      ProductWrapper wrapper = new ProductWrapper(it)
      wrapper.setEditingStatus(false)
      wrapper.setMemento(wrapper.clone() as ProductWrapper)
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
