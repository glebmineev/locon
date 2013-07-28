package ru.spb.locon.zulModels.shop

import com.google.common.base.Strings
import com.google.common.collect.Lists
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Textbox
import ru.spb.locon.CategoryEntity
import org.zkoss.zk.ui.event.*
import ru.spb.locon.FilterEntity
import ru.spb.locon.InitService
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.common.PathHandler;
import ru.spb.locon.wrappers.CategoryWrapper
import ru.spb.locon.wrappers.HrefWrapper

class CatalogNewViewModel {

  static Logger log = LoggerFactory.getLogger(CatalogNewViewModel.class)

  List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>()
  //Навигация.
  List<HrefWrapper> links = new LinkedList<HrefWrapper>()
  //Все товары
  List<ProductEntity> allProducts = new ArrayList<ProductEntity>()

  ListModelList<ManufacturerEntity> manufsFilterModel
  ListModelList<FilterEntity> usageFilterModel

  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService

  Long categoryID
  int currentIndex;
  //Показать товары и фильтры.
  boolean showCatalog;

  @Init
  public void init() {
    categoryID = Executions.getCurrent().getParameter("category") as Long
    initModels()
  }

  @NotifyChange(["showCatalog"])
  public void initModels(){
    categories.clear()
    CategoryEntity category = CategoryEntity.get(categoryID)
    showCatalog = category.getParentCategory() != null
    category.listCategory.each { it ->
      fillModel(it)
    }
    rebuildNaviPath(categoryID)

    if (showCatalog)
      allProducts = collectAllProducts(category, Lists.newArrayList())

    int allProductsSize = allProducts.size()
    if (allProductsSize > 0)
      initFilters()
  }

  /**
   * Инициализируем фильтры.
   */
  void initFilters() {
/*    List<FilterEntity> filters = products.filter.unique() as List<FilterEntity>
    manufsFilterModel = new BindingListModelList<ManufacturerEntity>(
        products.manufacturer.unique() as List<ManufacturerEntity>,
        true
    )

    manufsFilterModel.setMultiple(true)
    usageFilterModel = new BindingListModelList<FilterEntity>(filters, true)
    usageFilterModel.setMultiple(true)*/
  }

  /**
   * Собирает все продукты из категорий.
   * @param category - категория с товарами.
   * @param products - итоговый список товаров.
   * @return - все продукты категорий.
   */
  List<ProductEntity> collectAllProducts(CategoryEntity category, List<ProductEntity> products) {
    List<CategoryEntity> categories = category.listCategory as List<CategoryEntity>
    if (categories != null && categories.size() > 0)
      categories.each { CategoryEntity it ->
        if (it.listCategory != null && it.listCategory.size() > 0)
          collectAllProducts(it, products)
        else
          products.addAll(it.products as List<ProductEntity>)
      }
    else {
      products.addAll(category.products as List<ProductEntity>)
    }

    return products
  }

  /**
   * Формирует древовидную структуру категорий для навигации.
   * @param entity - категорий.
   */
  public void fillModel(CategoryEntity entity) {
    CategoryWrapper wrapper = new CategoryWrapper(entity)
    fillChildren(wrapper, entity.listCategory)
    categories.add(wrapper)
  }

  public void fillChildren(CategoryWrapper wrapper, Set<CategoryEntity> list) {
    list.each { it ->
      CategoryWrapper child = new CategoryWrapper(it)
      wrapper.addChild(child)
    }
  }

  /**
   * Обновление моделей продуктов и категорий.
   * @param categoryID - id категории.
   * @param list
   */
  @Command
  @NotifyChange(["categories"])
  public void goToCategory(@BindingParam("categoryID") Long categoryID){
    Executions.sendRedirect("/shop/catalog?category=${categoryID}")
  }

  /**
   * Применение фильтров.
   */
  @Command
  @NotifyChange(["products"])
  public void filtred() {

/*    List<String> manufsSelection = manufsFilterModel.getSelection().collect { it.name } as List<String>
    List<String> usageSelection = usageFilterModel.getSelection().collect { it.name } as List<String>

    List<ProductEntity> result = allProducts
    if (manufsSelection.size() > 0)
      result = allProducts.findAll { it -> manufsSelection.contains(it.manufacturer.name) }

    if (usageSelection.size() > 0)
      result = result.findAll {
        it ->
          usageSelection.contains(it.filter.name)
      }

    products.clear()
    currentIndex = 0

    if (result.size() > 0) {
      currentIndex += result.size() > 19 ? 20 : result.size()
      products.addAll(transform(result.subList(0, currentIndex)))
    }*/

  }

  /**
   * Фильтрация по цене.
   */
  @Command
  @NotifyChange(["products"])
  public void priceFilter(@BindingParam("inputEvent") InputEvent event) {

/*    Long beforeValue = getBeforeValue(event)
    Long afterValue = getAfterValue(event)

    ArrayList<ProductWrapper> result = products.findAll { it ->
      long price = it.price
      price >= (beforeValue) && price <= (afterValue)
    }
    if (result != null && result.size() > 0) {
      products.clear()
      products.addAll(result)
    }*/

  }

  Long getBeforeValue(InputEvent event) {
    Component target = event.getTarget()
    String id = target.getId()
    if ("beforeFilterPrice".equals(id))
      return event.getValue() as Long
    else
    {
      Textbox before = event.getPage().getFellow("beforeFilterPrice") as Textbox
      return !Strings.isNullOrEmpty(before.getValue()) ? before.getValue() as Long : 0L
    }
  }

  Long getAfterValue(InputEvent event) {
    Component target = event.getTarget()
    String id = target.getId()
    if ("afterFilterPrice".equals(id))
      return event.getValue() as Long
    else
    {
      Textbox after = event.getPage().getFellow("afterFilterPrice") as Textbox
      return !Strings.isNullOrEmpty(after.getValue()) ? after.getValue() as Long : 0L
    }
  }

  /**
   * Формирование навигации.
   * @param categoryID - текущщая категория.
   */
  void rebuildNaviPath(Long categoryID) {
    List<CategoryEntity> categories = PathHandler.getCategoryPath(CategoryEntity.get(categoryID))
    links.clear()
    categories.each { it ->
      links.add(new HrefWrapper(it.name, "/shop/catalog/category?category=${it.id}"))
    }
  }

}
