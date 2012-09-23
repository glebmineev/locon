package cart

import ru.spb.locon.CartEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.CartProductEntity
import org.zkoss.zk.ui.Executions
import com.studentuniverse.grails.plugins.cookie.services.CookieService
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zk.ui.util.Clients

/**
 * User: Gleb
 * Date: 09.09.12
 * Time: 13:42
 */
class CartUtils {

  CookieService cookieService = (CookieService) SpringUtil.getApplicationContext().getBean("cookieService")

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

  public void addToCart(Long productID){
    String uuid = cookieService.get("cart_uuid")
    CartEntity cart = (uuid != null && !uuid.isEmpty()) ? CartEntity.findByUuid(uuid) : null
    if (cart == null)
      createCart(ProductEntity.get(productID))
    else
      refreshCart(cart, ProductEntity.get(productID))

    recalculateCart()
  }

  public void recalculateCart(){
    String uuid = cookieService.get("cart_uuid")
    Clients.evalJavaScript("\$('#countProducts').html('" + getProductCount(uuid) + "')")
    Clients.evalJavaScript("\$('#priceProducts').html('" + getTotalPrice(uuid) + "')")
  }

  private void createCart(ProductEntity product) {
    CartProductEntity.withTransaction {
      String uuid = UUID.randomUUID().toString().replaceAll("-", "_")
      cart = new CartEntity(dateCreate: new Date(), uuid: uuid)
      cart.save()
      saveCartProduct(cart, product)
      updateCookies(uuid)
    }
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

  private void updateCartProduct(CartProductEntity cartProduct){
    cartProduct.productsCount = cartProduct.productsCount + 1
    if (cartProduct.validate())
      cartProduct.merge()
    else
      throw new Exception("Ошибка мерже связки корзины с продуктом.")
  }

  private void saveCartProduct(CartEntity cart, ProductEntity product){
    CartProductEntity link = new CartProductEntity(cart: cart, product: product, productsCount: 1)
    if (link.validate())
      link.save(flush: true)
    else
      throw new Exception("Ошибка сохранения связки корзины с продуктом.")
  }

  private void updateCookies(String uuid){
    //если уже была така кука то удаляем.
    cookieService.delete(Executions.current.nativeResponse, "cart_uuid")
    //задам id корзины.
    cookieService.set(Executions.current.nativeResponse, "cart_uuid", uuid, 604800)
  }

  public CartEntity getCart() {
    String uuid = cookieService.get("cart_uuid")
    return CartEntity.findByUuid(uuid)
  }

}
