package cart

import ru.spb.locon.CartEntity
import ru.spb.locon.ProductEntity

/**
 * User: Gleb
 * Date: 09.09.12
 * Time: 13:42
 */
class CartUtils {

  public static float initTotalPrice(String uuid) {
    CartEntity cart = CartEntity.findByUuid(uuid)
    float allPrices = 0.0
    if (cart != null &&
        cart.listCartProduct != null)
      cart.listCartProduct.product.each {ProductEntity product ->
        float price = product.price == null ? 0.0 : product.price
        allPrices = allPrices + price
      }
    return allPrices
  }

  public static int initProductCount(String uuid) {
    int productCount = 0
    CartEntity cart = CartEntity.findByUuid(uuid)
    if (cart != null &&
        cart.listCartProduct != null)
      productCount = cart.listCartProduct.size()
    return productCount
  }

}
