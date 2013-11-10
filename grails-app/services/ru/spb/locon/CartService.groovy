package ru.spb.locon

import org.zkoss.zk.ui.util.Clients
import ru.spb.locon.wrappers.ProductWrapper

/**
 * Сервис для работы с корзиной покупателя.
 */
class CartService {

  static transactional = true
  static scope = "session";
  static proxy = true

  private Map<Long, Long> cart = new HashMap<Long, Long>()
  private Long totalPrice = 0
  private Long totalCount = 0

  public void addToCart(ProductEntity product) {
    if (cart != null)
      updateCart(cart, product)
    else
      createCart(product)

    renderTotalCount()
    renderTotalPrice()
  }

  private void updateCart(Map<Long, Long> cart, ProductEntity product) {
    if (cart.containsKey(product.id)) {
      long count = cart.get(product.id)
      cart.put(product.id, count + 1)
      updateSessionAttributes(product.price, 1L)
    } else {
      cart.put(product.id, 1)
      updateSessionAttributes(product.price, 1L)
    }
  }

  /**
   * Создает корзину.
   * @param product - товар.
   */
  private void createCart(ProductEntity product) {
    cart.put(product.id, 1L)
    updateSessionAttributes(product.price, 1L)
  }

  /**
   * Есть ли товар в корзине.
   * @param productID - ид товара.
   * @return
   */
  public boolean findedInCart(Long productID) {
    return cart.get(productID)
  }

  /**
   * Возвращает все товары в корзине.
   * @return
   */
  public List<ProductEntity> getCartProducts() {
    List<ProductEntity> items = new ArrayList<ProductEntity>()
    cart.each { id, count ->
      items.add(ProductEntity.get(id),)
    }
    return items
  }

  /**
   * Увеличивает или уменьшает количество товаров в корзине.
   * @param productEntity
   * @param mark
   */
  public void incrementCount(ProductEntity productEntity, long mark) {
    Long productID = productEntity.id
    Long price = productEntity.price
    cart.put(productID, cart.get(productID) + mark)
    updateSessionAttributes((mark * price), mark)
    renderTotalCount()
    renderTotalPrice()
  }

  /**
   * Возвращает количество товара в корзине.
   * @param productID
   * @return
   */
  public Long getProductCount(Long productID) {
    return cart.get(productID) != null ? cart.get(productID) : 0
  }

  /**
   * Удаление товара из корзины.
   * @param product
   */
  public void removeFromCart(ProductEntity product) {
    Long count = cart.get(product.id)
    Long price = (product.price * count)
    updateSessionAttributes(-price, -count)
    renderTotalCount()
    renderTotalPrice()
    cart.remove(product.id)
  }

  public void cleanCart() {
    cart.clear()
    this.totalPrice = 0.0
    this.totalCount = 0
  }

  private void updateSessionAttributes(Long addPrice, Long addCount) {
    Long totalCount = this.totalCount + addCount
    Long result = totalPrice + addPrice
    //BigDecimal rounded = new BigDecimal(result).setScale(1, RoundingMode.HALF_DOWN).floatValue()
    this.totalPrice = result
    this.totalCount = totalCount
  }

  /**
   * Инициализация полей обертки товара, если товар есть в корзине.
   */
  public initAsCartItem(ProductWrapper wrapper){
    wrapper.count = getProductCount(wrapper.id)
    if (wrapper.count > 0){
      wrapper.inCart = true
      wrapper.totalPrice = (wrapper.count * wrapper.price)
    }
  }

  public renderTotalPrice() {
    Clients.evalJavaScript("\$('#totalPrice').html('${totalPrice as String}')")
  }

  public renderTotalCount() {
    Clients.evalJavaScript("\$('#totalCount').html('${totalCount as String}')")
  }

  public Long getTotalPrice() {
    return totalPrice
  }

  public Long getTotalCount() {
    return totalCount
  }


}
