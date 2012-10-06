package cart

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.util.Clients
import ru.spb.locon.ProductEntity
import java.math.RoundingMode

/**
 * User: Gleb
 * Date: 05.10.12
 * Time: 20:41
 */
class SessionUtils {

  public static void addToCart(ProductEntity product) {
    Session session = Executions.current.session
    Map<Long, Long> cart = (Map<Long, Long>) session.getAttribute("cart")
    if (cart != null)
      updateCart(cart, product)
    else
      createCart(product)

    renderTotalCount()
    renderTotalPrice()
  }

  private static void updateCart(Map<Long, Long> cart, ProductEntity product){
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

  private static void createCart(ProductEntity product){
    Session session = Executions.current.session
    Map<Long, Long> cart = new HashMap<Long, Long>()
    cart.put(product.id, 1L)
    session.setAttribute("cart", cart)
    updateSessionAttributes(product.price, 1L)
  }

  public static List<CartItem> getCartProducts(){
    Session session = Executions.current.session
    Map<Long, Long> cart = (Map<Long, Long>) session.getAttribute("cart")
    List<CartItem> items = new ArrayList<CartItem>()
    cart.each{id, count ->
      CartItem item = new CartItem(ProductEntity.get(id), count)
      items.add(item)
    }
    return items
  }

  public static void removeFromCart(CartItem cartItem){
    Session session = Executions.current.session
    Map<Long, Long> cart = (Map<Long, Long>) session.getAttribute("cart")
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

  public static void cleanCart(){
    Session session = Executions.current.session
    session.setAttribute("totalPrice", 0.0)
    session.setAttribute("totalCount", 0)
  }

  private static void updateSessionAttributes(Float addPrice, Long addCount) {
    Session session = Executions.current.session
    Float totalPrice = (Float) getSessionValue("totalPrice")
    Long sessionTotalCount = (Long) getSessionValue("totalCount")
    Long totalCount = sessionTotalCount + addCount
    Float result = totalPrice + addPrice
    BigDecimal rounded = new BigDecimal(result).setScale(1, RoundingMode.HALF_DOWN).floatValue()
    session.setAttribute("totalPrice", rounded)
    session.setAttribute("totalCount", totalCount)
  }

  private static Object getSessionValue(String attrName){
    Session session = Executions.current.session
    Object attribute = session.getAttribute(attrName)
    if (attribute == null)
      attribute = 0
    return attribute
  }

  public static renderTotalPrice(){
    Session session = Executions.current.session
    Float totalPrice = (Float) session.getAttribute("totalPrice")
    Clients.evalJavaScript("\$('#totalPrice').html('${totalPrice.toString()}')")
  }

  public static renderTotalCount(){
    Session session = Executions.current.session
    Long totalCount = (Long) session.getAttribute("totalCount")
    Clients.evalJavaScript("\$('#totalCount').html('${totalCount.toString()}')")
  }

}
