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
import org.zkoss.bind.annotation.ExecutionArgParam
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.image.AImage
import org.zkoss.zhtml.Li
import org.zkoss.zhtml.Ul
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Div
import org.zkoss.zul.Image
import org.zkoss.zul.Include
import org.zkoss.zul.Label
import org.zkoss.zul.ListModelList
import ru.spb.locon.CartService
import ru.spb.locon.FilterEntity
import ru.spb.locon.ImageService
import ru.spb.locon.InitService
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.wrappers.CategoryWrapper
import ru.spb.locon.wrappers.HrefWrapper
import ru.spb.locon.wrappers.ProductWrapper

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 7/28/13
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
class ShowcaseViewModel {

  static Logger log = LoggerFactory.getLogger(ShowcaseViewModel.class)

  List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>()
  //Навигация.
  List<HrefWrapper> links = new LinkedList<HrefWrapper>()
  //Все товары
  List<ProductEntity> allProducts = new ArrayList<ProductEntity>()
  //Показываемые пользователю товары
  List<ProductWrapper> products = new ArrayList<ProductWrapper>()
  //Модель данных для фильтра по производителю.
  ListModelList<ManufacturerEntity> manufsFilterModel
  //Модель данных для фильра по применению.
  ListModelList<FilterEntity> usageFilterModel

  /**
   * Необходимые сервисы.
   */
  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService
  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService
  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService

  //Id выбранной пользователем категории.
  Long categoryID

  //Индекс в списке всех товаров(allProducts)
  int currentIndex

  /**
   * Параметры для настройки вложения.
   */
  //Показывать или нет панель изменения вида отображения витрины.
  boolean isChangeShow = false
  //Показать или нет кнопку добавить.
  boolean showAppendBtn = false
  //Уникальный идентифткатор вложения.
  String uuidInclude

  @Init
  public void init(@ExecutionArgParam("allProducts") List<ProductEntity> data,
                   @ExecutionArgParam("isChangeShow") String isChangeShow,
                   @ExecutionArgParam("showAppendBtn") String showAppendBtn) {
    uuidInclude = UUID.randomUUID()
    this.isChangeShow = Boolean.parseBoolean(isChangeShow)
    this.showAppendBtn = Boolean.parseBoolean(showAppendBtn)

    rebuildModel(data)
  }

  /**
   * Обновление модели данных вложения.
   * @param data
   */
  @Command
  @NotifyChange(["products"])
  void rebuildModel(List<ProductEntity> data){
    currentIndex = 0;
    allProducts.clear()
    products.clear()
    allProducts.addAll(data)
    int allProductsSize = allProducts.size()
    if (allProductsSize > 0) {
      currentIndex += allProductsSize > 19 ? 20 : allProductsSize
      products.addAll(transform(allProducts.subList(0, currentIndex)))
    }
  }

  List<ProductWrapper> transform(List<ProductEntity> target) {
    return Collections2.transform(target, new Function<ProductEntity, ProductWrapper>() {
      @Override
      ProductWrapper apply(ProductEntity f) {
        ProductWrapper wrapper = new ProductWrapper(f)
        cartService.initAsCartItem(wrapper)
        return wrapper;
      }
    }) as List<ProductWrapper>
  }

  /**
   * Добавление в корзину.
   * @param wrapper
   */
  @Command
  public void toCart(@BindingParam("wrapper") ProductWrapper wrapper) {
    cartService.addToCart(ProductEntity.get(wrapper.getId()))
    wrapper.setInCart(true)
    refreshRowTemplate(wrapper)
  }

  @Command
  public void refreshRowTemplate(ProductWrapper wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "inCart");
  }

  @Command
  public void applyRowTemplate(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Include include = event.getTarget().getSpaceOwner() as Include
    Div productsDiv = include.getFirstChild().getFirstChild() as Div
    productsDiv.setSclass("products-row-template")
  }

  @Command
  public void applyCellTemplate(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Include include = event.getTarget().getSpaceOwner() as Include
    Div productsDiv = include.getFirstChild().getFirstChild() as Div
    productsDiv.setSclass("products-cell-template")
  }

  /**
   * Добавление
   * @param event
   */
  @Command
  @NotifyChange(["showAppendBtn"])
  public void appendElse(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Include include = event.getTarget().getSpaceOwner() as Include
    Ul root = include.getFirstChild().getFirstChild().getLastChild() as Ul
    addRows(root)
  }

  public void addRows(Ul parent) {
    int nextIndex = currentIndex + 20
    int allProductsSize = allProducts.size()
    List<ProductWrapper> subList
    if (nextIndex < allProductsSize) {
      subList = transform(allProducts.subList(currentIndex, nextIndex))
      currentIndex = currentIndex + 20
    } else if (nextIndex > allProductsSize) {
      subList = transform(allProducts.subList(currentIndex, allProductsSize))
      currentIndex = allProductsSize
      showAppendBtn = false
    }

    subList.each { it ->
      Li li = new Li()
      Div div = new Div()
      Div imageDiv = new Div()
      imageDiv.setAlign("center")

      Image img = new Image()
      img.setSclass("productImage")

      AImage aImg = imageService.getImageFile(ProductEntity.get(it.id), "150")
      img.setContent(aImg)

      img.addEventListener(Events.ON_CLICK, new org.zkoss.zk.ui.event.EventListener() {
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

}
