package ru.spb.locon

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.*

import org.zkoss.zkplus.spring.SpringUtil

import ru.spb.locon.windows.ImageWindow
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.*
import org.zkoss.zk.grails.composer.GrailsComposer

class ProductComposer extends GrailsComposer {

  Div productImg
  Button cartButton
  Div categoryPath

  Label usage
  Label description
  Label name
  Label price
  Label volume

  ProductEntity product

  CartService cartService = (CartService) SpringUtil.getApplicationContext().getBean("cartService")
  ImageService imageSyncService = (ImageService) SpringUtil.getApplicationContext().getBean("imageService")

  List<CategoryEntity> categories = new ArrayList<CategoryEntity>()

  def afterCompose = {Window window ->
    Long productId = Long.parseLong(Executions.getCurrent().getParameter("product"))
    product = ProductEntity.get(productId)

    Image image = imageSyncService.getProductImage(product, "300")
    image.setStyle("cursor: pointer;")
    productImg.appendChild(image)

    image.addEventListener(Events.ON_CLICK, new EventListener(){

      @Override
      void onEvent(Event t) {
        Image zoomImage = imageSyncService.getProductImage(product, "500")
        ImageWindow imageWindow = new ImageWindow(zoomImage, product.name)
        imageWindow.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
        imageWindow.doModal()
      }

    })

    String param = Executions.getCurrent().getParameter("category")
    CategoryEntity category
    if (param != null){
      Long categoryId = Long.parseLong(param)
      category = CategoryEntity.get(categoryId)
    } else {
      category = product.listCategoryProduct.category.find {
        it.parentCategory == null
      }
    }

    fillCategories(category)
    initializeCategoryPath()
    initializeFields()
  }

  /*
   * метод проставляет занчения товара.
   */
  void initializeFields() {

    name.setValue(product.name)
    price.setValue(Float.toString(product.price))
    volume.setValue(product.volume)

    usage.setValue(product.usage)
    description.setValue(product.description)
  }

  /**
   * Формирует иерархию категорий начиная с самой последней.
   * @param category - предыдущая катеория.
   */
  void fillCategories(CategoryEntity category){
    categories.add(category)
    if (category.parentCategory != null)
      fillCategories(category.parentCategory)
  }

  /**
   * Формирует путь до корневой категории.
   */
  void initializeCategoryPath(){
    int count = categories.size() - 1
    while (count != -1) {
      CategoryEntity category = categories.get(count)
      A href = new A()
      href.setHref("/shop/catalog?category=${category.id}")
      href.setLabel(category.name)
      href.setStyle("font-size: 16pt;")
      categoryPath.appendChild(href)
      
      if (count != 0) {
        Image right = new Image("/images/right.png")
        categoryPath.appendChild(right)
      }
      count--
    }
  }

  public void onClick_cartButton(Event event) {
    cartService.addToCart(product)
  }

}
