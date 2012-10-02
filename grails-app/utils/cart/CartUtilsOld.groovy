package cart

import ru.spb.locon.CartEntity
import ru.spb.locon.ProductEntity

import org.zkoss.zk.ui.Executions
import com.studentuniverse.grails.plugins.cookie.services.CookieService
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zk.ui.util.Clients

import ru.spb.locon.CartProductEntity

/**
 * User: Gleb
 * Date: 09.09.12
 * Time: 13:42
 */
class CartUtilsOld {

  CookieService cookieService = (CookieService) SpringUtil.getApplicationContext().getBean("cookieService")

  @Deprecated
  public static float getTotalPrice(String uuid) {
    CartEntity cart = CartEntity.findByUuid(uuid)
    float allPrices = 0.0
    if (cart != null &&
        cart.listCartProduct != null)
      cart.listCartProduct.each {CartProductEntity cartProduct ->
        float price = cartProduct.product.price == null ? 0.0 : cartProduct.product.price
        allPrices = allPrices + (cartProduct.productsCount * price)
      }
    return allPrices
  }

  @Deprecated
  public static int getProductCount(String uuid) {
    int productCount = 0
    CartEntity cart = CartEntity.findByUuid(uuid)
    if (cart != null &&
        cart.listCartProduct != null) {
      cart.listCartProduct.each {CartProductEntity cartProduct ->
        productCount = productCount + cartProduct.productsCount
      }
    }
    return productCount
  }

  public void addToCart(Long productID) {
    String uuid = cookieService.get("cart_uuid")
    CartEntity cart = (uuid != null && !uuid.isEmpty()) ? CartEntity.findByUuid(uuid) : null
    if (cart == null)
      createCart(ProductEntity.get(productID))
    else
      refreshCart(cart, ProductEntity.get(productID))
  }

  public void recalculateCart() {
    String uuid = cookieService.get("cart_uuid")
    if (checkUUID(uuid)) {

      CartEntity cart = CartEntity.findByUuid(uuid)
      if (cart != null) {
        int productCount = 0
        float allPrices = 0.0
        Set<CartProductEntity> cartProductsList = cart.listCartProduct
        if (cartProductsList != null){
          cartProductsList.each {CartProductEntity cartProduct ->
            float price = cartProduct.product.price == null ? 0.0 : cartProduct.product.price
            allPrices = allPrices + (cartProduct.productsCount * price)
            productCount = productCount + cartProduct.productsCount
          }

        }
        Clients.evalJavaScript("\$('#countProducts').html('" + productCount + "')")
        Clients.evalJavaScript("\$('#priceProducts').html('" + allPrices + "')")
      }
    }
    else
    {
      nullCart()
    }
  }

  public void nullCart(){
    Clients.evalJavaScript("\$('#countProducts').html('0')")
    Clients.evalJavaScript("\$('#priceProducts').html('0')")
  }

  private void createCart(ProductEntity product) {
    String uuid = UUID.randomUUID().toString().replaceAll("-", "_")
    CartEntity cart = null
    CartEntity.withTransaction{
      cart = new CartEntity(dateCreate: new Date(), uuid: uuid, isOrder: false)
      cart.save(flush: true)
    }
    CartProductEntity.withTransaction {
      saveCartProduct(cart, product)
    }
    updateCookies(uuid)
  }

  private void refreshCart(CartEntity cart, ProductEntity product) {
    CartProductEntity.withTransaction {
      CartProductEntity cartProduct = CartProductEntity.findWhere(cart: cart, product: product)
      if (cartProduct != null)
        updateCartProduct(cartProduct)
      else
        saveCartProduct(cart, product)
    }
  }

  private void updateCartProduct(CartProductEntity cartProduct) {
    cartProduct.productsCount = cartProduct.productsCount + 1
    if (cartProduct.validate())
      cartProduct.merge()
    else
      throw new Exception("Ошибка мерже связки корзины с продуктом.")
  }

  private void saveCartProduct(CartEntity cart, ProductEntity product) {
    CartProductEntity link = new CartProductEntity(cart: cart, product: product, productsCount: 1)
    if (link.validate())
      link.save(flush: true)
    else
      throw new Exception("Ошибка сохранения связки корзины с продуктом.")
  }

  private void updateCookies(String uuid) {
    //если уже была така кука то удаляем.
    cookieService.delete(Executions.current.nativeResponse, "cart_uuid")
    //задам id корзины.
    cookieService.set(Executions.current.nativeResponse, "cart_uuid", uuid, 604800)
  }

  public CartEntity getCart() {
    String uuid = cookieService.get("cart_uuid")
    CartEntity cart = null
    if (uuid != null && !uuid.isEmpty())
      cart = CartEntity.findByUuid(uuid)
    return cart
  }

  public void deleteCart() {
    CartEntity.withTransaction {
      CartEntity cart = getCart()
      cart.delete(flush: true)
    }
    cookieService.delete(Executions.current.nativeResponse, "cart_uuid")
  }

  public void clearCookie(){
    cookieService.delete(Executions.current.nativeResponse, "cart_uuid")
  }

  public boolean checkUUID(String uuid) {
    return (uuid != null && !uuid.isEmpty())
  }

}
