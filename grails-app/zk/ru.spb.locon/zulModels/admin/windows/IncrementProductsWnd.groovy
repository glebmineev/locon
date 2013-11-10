package ru.spb.locon.zulModels.admin.windows

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Window
import ru.spb.locon.ProductEntity
import ru.spb.locon.wrappers.ProductWrapper

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/11/13
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
class IncrementProductsWnd {

  List<ProductWrapper> selectedProducts
  Integer percent

  @Init
  public void init(){
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    selectedProducts = arg.get("selectedProducts") as List<ProductWrapper>
  }

  @Command
  public void incrementSelected(){
    increment(selectedProducts.id)
    Executions.sendRedirect("/admin/products")
  }

  @Command
  public void incrementAll(){
    increment(ProductEntity.list().id)
    Executions.sendRedirect("/admin/products")
  }

  void increment(List<Long> productIds) {
    productIds.each { id ->
      ProductEntity product = ProductEntity.get(id)
      Long price = product.price
      BigDecimal append = (price * percent) / 100
      Long newPrice = price + append
      product.setPrice(newPrice)
      if (product.validate())
        product.save(flush: true)
    }
  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    try {
      Window wnd = event.getTarget().getSpaceOwner() as Window
      Window owner = wnd as Window
      owner.detach()
    } catch (Exception ex) {
      log.debug(ex.getMessage())
    }
  }

}
