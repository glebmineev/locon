package ru.spb.locon

import org.zkoss.zul.Listbox
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import com.studentuniverse.grails.plugins.cookie.services.CookieService
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.renderers.ProductRenderer
import ru.spb.locon.renderers.CartRenderer
import org.zkoss.zul.ListModelList

/**
 * User: Gleb
 * Date: 22.09.12
 * Time: 0:07
 */
class CartComposer extends GrailsComposer {

  Listbox products
  ListModelList<CartProductEntity> productsModel

  CookieService cookieService = (CookieService) SpringUtil.getApplicationContext().getBean("cookieService")

  def afterCompose = {Window window ->
    setModel()
    initializeListBox()
  }

  private void setModel(){

    Collection<CartProductEntity> modelList = getCart().listCartProduct as List<CartProductEntity>
    if (modelList != null){
      productsModel = new ListModelList<CartProductEntity>(modelList)
    }
  }

  private CartEntity getCart() {
    String uuid = cookieService.get("cart_uuid")
    return CartEntity.findByUuid(uuid)
  }

  private void initializeListBox() {
    products.setModel(productsModel)
    products.setItemRenderer(new CartRenderer(productsModel))
  }

}
