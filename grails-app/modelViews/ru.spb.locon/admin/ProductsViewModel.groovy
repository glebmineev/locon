package ru.spb.locon.admin

import com.google.common.base.Strings
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.*
import ru.spb.locon.ProductEntity
import ru.spb.locon.admin.filters.data.FilterBean
import ru.spb.locon.admin.filters.data.FilterTypes
import ru.spb.locon.admin.filters.IFilterCallback
import ru.spb.locon.admin.filters.data.ObjectFilter
import ru.spb.locon.admin.wrapper.ProductWrapper
import ru.spb.locon.annotation.FieldInfo
import org.zkoss.zk.ui.event.EventListener
import java.lang.reflect.Field

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/6/13
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
class ProductsViewModel {

  Map<Field, Object> filterMap = new HashMap<Field, Object>()
  List<FilterBean> headers = new ArrayList<FilterBean>()
  List<FilterBean> filters = new ArrayList<FilterBean>()
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

  @Command
  public void changePrice(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {

    Page page = event.getTarget().getPage()
    Window wnd = new Window()
    wnd.setWidth("25%")
    wnd.setHeight("15%")

    Vbox vBox = new Vbox()
    vBox.setAlign("center")
    vBox.appendChild(new Label("Увеличить цену товаров."))

    Hbox hSplitter = new Hbox()
    Spinner spinner = new Spinner()
    hSplitter.appendChild(new Label("Увеличить на"))
    hSplitter.appendChild(spinner)
    hSplitter.appendChild(new Label("%"))

    Hbox hButtons = new Hbox()

    Button increment = new Button("Выбранные")
    Button incrementAllFiltered = new Button("Все отфильтрованные")
    Button cancel = new Button("Отмена")

    increment.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {
        incrementSelected(products.getSelection(), spinner.value)
        wnd.detach()
        rebuildModel()
      }
    })

    incrementAllFiltered.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {
        incrementSelected(products, spinner.value)
        wnd.detach()
        rebuildModel()
      }
    })

    cancel.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {
        wnd.detach()
      }
    })

    hButtons.appendChild(increment)
    hButtons.appendChild(incrementAllFiltered)
    hButtons.appendChild(cancel)

    vBox.appendChild(hSplitter)
    vBox.appendChild(hButtons)

    wnd.appendChild(vBox)
    wnd.setPage(page)
    wnd.doModal()

  }

  void incrementSelected(Collection<ProductWrapper> products, int spinnerValue) {
    products.each { wrapper ->
      ProductEntity product = ProductEntity.get(wrapper.productID)
      Long price = wrapper.price
      BigDecimal append = (price * spinnerValue) / 100
      Long newPrice = price + append
      product.setPrice(newPrice)
      if (product.validate())
        product.save(flush: true)
    }
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
  public void deleteProduct(@BindingParam("wrapper") ProductWrapper wrapper) {
    ProductEntity.withTransaction {
      ProductEntity toDelete = ProductEntity.get(wrapper.getProductID())
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

  List<FilterBean> getHeaders() {
    return headers
  }

  void setHeaders(List<FilterBean> headers) {
    this.headers = headers
  }

  List<FilterBean> getFilters() {
    return filters
  }

  void setFilters(List<FilterBean> filters) {
    this.filters = filters
  }

  ListModelList<ProductEntity> getProducts() {
    return products
  }

  void setProducts(ListModelList<ProductEntity> products) {
    this.products = products
  }

}
