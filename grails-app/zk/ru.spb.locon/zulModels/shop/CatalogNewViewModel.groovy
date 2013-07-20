package ru.spb.locon.zulModels.shop

import com.google.common.base.Function
import com.google.common.collect.Collections2
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.image.AImage
import org.zkoss.zhtml.Li
import org.zkoss.zhtml.Ul
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zul.Div
import org.zkoss.zul.Image
import org.zkoss.zul.Label
import ru.spb.locon.CartService
import ru.spb.locon.CategoryEntity
import org.zkoss.zk.ui.event.*
import ru.spb.locon.ImageService
import ru.spb.locon.ProductEntity
import ru.spb.locon.common.PathHandler;
import ru.spb.locon.wrappers.CategoryWrapper
import ru.spb.locon.wrappers.HrefWrapper
import ru.spb.locon.wrappers.ProductWrapper

class CatalogNewViewModel {

  static Logger log = LoggerFactory.getLogger(CatalogNewViewModel.class)

  List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>()
  //Навигация.
  List<HrefWrapper> links = new LinkedList<HrefWrapper>()
  //Все товары
  List<ProductEntity> allProducts = new ArrayList<ProductEntity>()
  List<ProductWrapper> products = new ArrayList<ProductWrapper>()

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService
  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService

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
      products.addAll(transform(allProducts.subList(0, currentIndex)))
    }

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
   * Перестроение навигационного меню.
   * @param categoryID - айде начальной категрии.
   */
  void rebuildPath(Long categoryID) {
    List<CategoryEntity> categories = PathHandler.getCategoryPath(CategoryEntity.get(categoryID))
    links.clear()
    links.add(new HrefWrapper("Главная", "/shop"))
    categories.each { it ->
      links.add(new HrefWrapper(it.name, "/shop/catalog/category?category=${it.id}"))
    }
  }

  /**
   * Добавление товаров на html страничку
   * @param event
   */
  @Command
  public void appendElse(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Page page = event.getPage()
    Ul parent = page.getFellow("productParent") as Ul
    addRows(parent)
  }

  public void addRows(Ul parent) {
    int nextIndex = currentIndex + 20
    int allProductsSize = allProducts.size()
    List<ProductWrapper> subList
    if (nextIndex < allProductsSize) {
      subList = transform(allProducts.subList(currentIndex, nextIndex))
      currentIndex = currentIndex + 20
    }
    else if (nextIndex > allProductsSize){
      subList = transform(allProducts.subList(currentIndex, allProductsSize))
      currentIndex = allProductsSize
    }

    subList.each {it ->
      Li li = new Li()
      Div div = new Div()
      Div imageDiv = new Div()
      imageDiv.setAlign("center")

      Image img = new Image()
      img.setSclass("productImage")

      AImage aImg = imageService.getImageFile(ProductEntity.get(it.id), "150")
      img.setContent(aImg)

      img.addEventListener(Events.ON_CLICK, new EventListener(){
        @Override
        void onEvent(Event t) {
          Executions.sendRedirect("/shop/product?product=${it.id}")
        }
      })

      Label label = new Label(it.getName())

      imageDiv.appendChild(img)
      div.appendChild(label)

      div.appendChild(imageDiv)
      li.appendChild(div)


      parent.appendChild(li)

    }

  }

  /**
   * Трансформирует список товаров в список оберток товаров.
   * @return
   */
  List<ProductWrapper> transform(List<ProductEntity> target){
    return Collections2.transform(target, new Function<ProductEntity, ProductWrapper>() {
      @Override
      ProductWrapper apply(ProductEntity f) {
        ProductWrapper wrapper = new ProductWrapper(f)
        cartService.initAsCartItem(wrapper)
        return wrapper;
      }
    }) as List<ProductWrapper>
  }

  @Command
  public void toCart(@BindingParam("wrapper") ProductWrapper wrapper){
    cartService.addToCart(ProductEntity.get(wrapper.getProductID()))
    wrapper.setInCart(true)
    refreshRowTemplate(wrapper)
  }

  @Command
  public void refreshRowTemplate(ProductWrapper wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "inCart");
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
