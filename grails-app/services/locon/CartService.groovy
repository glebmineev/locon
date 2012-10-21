package locon

import ru.spb.locon.ProductEntity
import cart.CartItem
import java.math.RoundingMode
import org.zkoss.zk.ui.util.Clients

class CartService {

  static transactional = true
  static scope = "session";
  static proxy = true

  private Map<Long, Long> cart = new HashMap<Long, Long>()
  private Float totalPrice = 0.0
  private Long totalCount = 0

  public void addToCart(ProductEntity product) {
    if (cart != null)
      updateCart(cart, product)
    else
      createCart(product)

    renderTotalCount()
    renderTotalPrice()
  }

  private void updateCart(Map<Long, Long> cart, ProductEntity product){
    if (cart.containsKey(product.id)) {
      long count = cart.get(product.id)
      cart.put(product.id, count + 1)
      updateSessionAttributes(product.price, 1L)
    }
    else
    {
      cart.put(product.id, 1)
      updateSessionAttributes(product.price, 1L)
    }
  }

  private void createCart(ProductEntity product){
    cart.put(product.id, 1L)
    updateSessionAttributes(product.price, 1L)
  }

  public List<CartItem> getCartProducts(){
    List<CartItem> items = new ArrayList<CartItem>()
    cart.each{id, count ->
      CartItem item = new CartItem(ProductEntity.get(id), count)
      items.add(item)
    }
    return items
  }

  public void removeFromCart(CartItem cartItem){
    ProductEntity product = cartItem.getProduct()
    if (cart.containsKey(product.id)){
      cart.remove(product.id)
      Long count = cartItem.count
      Float price = (product.price * Float.parseFloat(count.toString()))
      updateSessionAttributes(-(price), -cartItem.count)
      renderTotalCount()
      renderTotalPrice()
    }
  }

  public void decrementProduct(CartItem cartItem){
    ProductEntity product = cartItem.getProduct()
    if (cart.containsKey(product.id)){
      cart.put(product.id, cartItem.count - 1L)
      Long count = 1L
      Float price = product.price
      updateSessionAttributes(-(price), -count)
      renderTotalCount()
      renderTotalPrice()
    }
  }
  
  public void cleanCart(){
    cart.clear()
    this.totalPrice = 0.0
    this.totalCount = 0
  }

  private void updateSessionAttributes(Float addPrice, Long addCount) {
    Long totalCount = this.totalCount + addCount
    Float result = totalPrice + addPrice
    BigDecimal rounded = new BigDecimal(result).setScale(1, RoundingMode.HALF_DOWN).floatValue()
    this.totalPrice = rounded
    this.totalCount = totalCount
  }


  public renderTotalPrice(){
    Clients.evalJavaScript("\$('#totalPrice').html('${totalPrice.toString()}')")
  }

  public renderTotalCount(){
    Clients.evalJavaScript("\$('#totalCount').html('${totalCount.toString()}')")
  }
  public Float getTotalPrice() {
    return totalPrice
  }
  public Long getTotalCount() {
    return totalCount
  }


}
