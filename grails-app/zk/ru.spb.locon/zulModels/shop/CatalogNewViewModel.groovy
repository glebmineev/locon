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
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.Include
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Textbox
import ru.spb.locon.CategoryEntity
import org.zkoss.zk.ui.event.*
import ru.spb.locon.FilterEntity
import ru.spb.locon.InitService
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.common.CategoryPathHandler;
import ru.spb.locon.wrappers.CategoryWrapper
import ru.spb.locon.wrappers.HrefWrapper

class CatalogNewViewModel {

  static Logger log = LoggerFactory.getLogger(CatalogNewViewModel.class)

  List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>()
  //Навигация.
  List<HrefWrapper> links = new LinkedList<HrefWrapper>()
  //Все товары
  List<ProductEntity> allProducts = new ArrayList<ProductEntity>()

  //Отфильтрованные товары.
  List<ProductEntity> filtredProducts = new ArrayList<ProductEntity>()

  ListModelList<ManufacturerEntity> manufsFilterModel
  ListModelList<FilterEntity> usageFilterModel

  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService

  Long categoryID
  //Показать товары и фильтры.
  boolean showCatalog;

  boolean isShowcase = false

  @Init
  public void init() {
    categoryID = Executions.getCurrent().getParameter("category") as Long
    Long productId = Executions.getCurrent().getParameter("product") as Long
    if (categoryID != null)
      initModels()
    if (productId != null)
      buildProductNavPath(productId)
  }

  @NotifyChange(["showCatalog"])
  public void initModels(){
    isShowcase = true
    categories.clear()
    CategoryEntity category = CategoryEntity.get(categoryID)
    showCatalog = category.getParentCategory() != null
    category.listCategory.each { it ->
      fillModel(it)
    }
    rebuildCategoryNavPath(categoryID)

    if (showCatalog)
      allProducts = collectAllProducts(category, Lists.newArrayList())
    else
      allProducts = initService.getHits(category.name)

    int allProductsSize = allProducts.size()
    if (allProductsSize > 0)
      initFilters()
  }

  /**
   * Инициализируем фильтры.
   */
  void initFilters() {
    List<FilterEntity> filters = allProducts.filter.unique() as List<FilterEntity>
    manufsFilterModel = new BindingListModelList<ManufacturerEntity>(allProducts.manufacturer.unique() as List<ManufacturerEntity>, true)

    manufsFilterModel.setMultiple(true)
    usageFilterModel = new BindingListModelList<FilterEntity>(filters, true)
    usageFilterModel.setMultiple(true)
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
  public void filtred() {
    Event event = ExecutionsCtrl.getCurrentCtrl().getExecutionInfo().getEvent()
    filtredProducts.clear()
    applyManufacturers()
    applyUsageFilter()
    if (event instanceof InputEvent)
      applyPriceFilter(event)

    Page page = ExecutionsCtrl.getCurrentCtrl().getCurrentPage()
    Include showcase = page.getFellow("showcase") as Include
    showcase.setDynamicProperty("allProducts", filtredProducts)
    showcase.invalidate()
  }

  /**
   * Фильтрация по производителю
   */
  void applyManufacturers() {
    List<String> manufsSelection = manufsFilterModel.getSelection().collect { it.name } as List<String>
    if (manufsSelection.size() > 0)
      filtredProducts = allProducts.findAll { it -> manufsSelection.contains(it.manufacturer.name) }
    else
      filtredProducts.addAll(allProducts)
  }

  /**
   * Фильтрация по применению.
   */
  void applyUsageFilter() {
    List<String> usageSelection = usageFilterModel.getSelection().collect { it.name } as List<String>
    if (usageSelection.size() > 0)
      filtredProducts = filtredProducts.findAll { it -> usageSelection.contains(it.filter.name) }
  }

  /**
   * Фильтрация по цене.
   */
  public void applyPriceFilter(InputEvent event) {

    Long beforeValue = getBeforeValue(event)
    Long afterValue = getAfterValue(event)

    if (beforeValue > Long.MIN_VALUE && afterValue < Long.MAX_VALUE)
      filtredProducts = filtredProducts.findAll { it ->
        long price = it.price
        price >= (beforeValue) && price <= (afterValue)
      }
  }

  Long getBeforeValue(InputEvent event) {
    Component target = event.getTarget()
    String id = target.getId()
    if ("beforeFilterPrice".equals(id))
      return !Strings.isNullOrEmpty(event.getValue()) ? event.getValue() as Long : Long.MIN_VALUE
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
      return !Strings.isNullOrEmpty(event.getValue()) ? event.getValue() as Long : Long.MAX_VALUE
    else
    {
      Textbox after = event.getPage().getFellow("afterFilterPrice") as Textbox
      return !Strings.isNullOrEmpty(after.getValue()) ? after.getValue() as Long : Long.MAX_VALUE
    }
  }

  /**
   * Формирование навигации.
   * @param categoryID - текущщая категория.
   */
  void rebuildCategoryNavPath(Long categoryID) {
    List<CategoryEntity> categories = CategoryPathHandler.getCategoryPath(CategoryEntity.get(categoryID))
    links.clear()
    categories.each { it ->
      links.add(new HrefWrapper(it.name, "/shop/catalog/category?category=${it.id}"))
    }
  }

  public void buildProductNavPath(Long productID) {
    ProductEntity product = ProductEntity.get(productID)
    rebuildCategoryNavPath(product.getCategory().id)
    links.add(new HrefWrapper(product.name, "/shop/catalog?product=${product.id}"))
  }

  @Command
  public void back(){
    Executions.sendRedirect("/shop/catalog/category?category=${categoryID}")
  }

}
