package ru.spb.locon.zulModels.shop

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zhtml.Li
import org.zkoss.zhtml.Ul
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zul.Div
import org.zkoss.zul.Label
import ru.spb.locon.CategoryEntity
import org.zkoss.zk.ui.event.*
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
  List<ProductEntity> products = new ArrayList<ProductEntity>()

  Long categoryID

  int currentIndex;

  @Init
  public void init() {

    categoryID = Executions.getCurrent().getParameter("category") as Long

    categories.clear()
    CategoryEntity category = CategoryEntity.get(categoryID)
    category.listCategory.each { it ->
      fillModel(it)
    }
    rebuildPath(categoryID)

    collectAllProducts(category, allProducts)
    int allProductsSize = allProducts.size()
    if (allProductsSize > 0) {
      currentIndex += allProductsSize > 19 ? 20 : allProductsSize
      products.addAll(allProducts.subList(0, currentIndex))
    }

  }

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

  void rebuildPath(Long categoryID) {
    List<CategoryEntity> categories = PathHandler.getCategoryPath(CategoryEntity.get(categoryID))
    links.clear()
    links.add(new HrefWrapper("Главная", "/shop"))
    categories.each { it ->
      links.add(new HrefWrapper(it.name, "/shop/catalog/category?category=${it.id}"))
    }
  }

  @Command
  public void appendElse(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Page page = event.getPage()
    Ul parent = page.getFellow("productParent") as Ul
    addRows(parent)
  }

  public void addRows(Ul parent) {
    int nextIndex = currentIndex + 20
    int allProductsSize = allProducts.size()
    List<ProductEntity> subList
    if (nextIndex < allProductsSize) {
      subList = allProducts.subList(currentIndex, nextIndex)
      currentIndex = currentIndex + 20
    }
    else if (nextIndex > allProductsSize){
      subList = allProducts.subList(currentIndex, allProductsSize)
      currentIndex = allProductsSize
    }

    subList.each {it ->
      Li li = new Li()
      Div div = new Div()
      Div imageDiv = new Div()


      Label label = new Label(it.getName())

      div.appendChild(label)

      li.appendChild(div)
      div.appendChild(imageDiv)

      parent.appendChild(li)

    }

  }

  @Command
  public void applyRowTemplate(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Page page = event.getPage()
    Div productsDiv = page.getFellow("products") as Div
    productsDiv.setSclass("products-row-template")
  }

  @Command
  public void applyCellTemplate(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Page page = event.getPage()
    Div productsDiv = page.getFellow("products") as Div
    productsDiv.setSclass("products-cell-template")
  }

}
