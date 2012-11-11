package ru.spb.locon

import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Window
import org.zkoss.zul.Label

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zul.impl.XulElement
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import ru.spb.locon.domain.DomainUtils

import org.zkoss.zkplus.spring.SpringUtil

import org.zkoss.zul.Image
import ru.spb.locon.importer.ConverterRU_EN
import ru.spb.locon.importer.ImageHandler
import ru.spb.locon.windows.ImageWindow
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.A
import org.zkoss.zul.Div

class ProductComposer extends SelectorComposer<Window> {

  @Wire("label")
  List<XulElement> outputs

  @Wire("#productImg")
  Image productImage

  @Wire("#cartButton")
  Button cartButton

  @Wire("#backButton")
  Button backButton

  @Wire("#categoryPath")
  Div categoryPath

  Long productId
  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")

  @Override
  public void doAfterCompose(Window window) {
    super.doAfterCompose(window)
    productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    ProductEntity product = ProductEntity.get(productId)

    String applicationPath = Executions.current.nativeRequest.getSession().getServletContext().getRealPath("/")

    String imagePath = ConverterRU_EN.translit(product.imagePath)
    String path = "${applicationPath}\\images\\catalog\\${imagePath}"
    ImageHandler dirUtils = new ImageHandler()
    List<String> images = dirUtils.findImages(path)
    String resultPath = "/images/empty.png"
    if ( images.size() > 0){
      resultPath = "/images/catalog/${imagePath}"
      productImage.setSrc("/images/catalog/${imagePath}/1-228.jpg")
    } else
      productImage.setSrc("/images/empty.png")

    productImage.setStyle("cursor: pointer;")
    productImage.addEventListener(Events.ON_CLICK, new EventListener(){

      @Override
      void onEvent(Event t) {
        Image zoomImage = new Image("${resultPath}/1-500.jpg")
        zoomImage.setWidth("500px")
        zoomImage.setHeight("500px")
        ImageWindow imageWindow = new ImageWindow(zoomImage, product.name)
        imageWindow.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
        imageWindow.doModal()
      }

    })

    cartButton.addEventListener(Events.ON_CLICK, new EventListener() {

      @Override
      void onEvent(Event t) {
        //значения выбранных товаров храняться в cookie.
        cartService.addToCart(product)

      }
    })

    String categoryId = Long.parseLong(Executions.getCurrent().getParameter("category"))
    CategoryEntity category = CategoryEntity.get(categoryId)

    initializeCategoryPath(category)
    initializeFields()
  }

  /*
   * метод проставляет занчения товара.
   */
  void initializeFields() {
    ProductEntity product = ProductEntity.get(productId)
    if (product != null) {
      outputs.each {XulElement element ->
        String fieldName = element.id
        if (element instanceof Label &&
            !fieldName.isEmpty()) {
          Label label = (Label) element
          Object value = product."${fieldName}"
          label.setValue(DomainUtils.parseTo(value))
        }
      }
    }
  }

  void initializeCategoryPath(CategoryEntity category){
    A link = new A()
    link.setHref("/shop/catalog?category=${category.id}")
    link.setLabel(category.name)
    categoryPath.appendChild(link)
    if (category.parentCategory != null)
      initializeCategoryPath(category.parentCategory)
  }

}
