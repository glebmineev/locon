package ru.spb.locon.zulModels.cabinet

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import ru.spb.locon.OrderEntity
import ru.spb.locon.OrderProductEntity
import ru.spb.locon.common.OrderStatuses

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */
class OrderItemViewModel {

  String number
  boolean emoney
  boolean courier
  String status

  ListModelList<OrderProductEntity> ordersModel

  @Init
  public void init() {

    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    Long orderID = arg.get("orderID") as Long
    OrderEntity order = OrderEntity.get(orderID)

    if (order.isProcessed)
      status = OrderStatuses.IS_PROCESSING.getValue()
    if (order.isComplete)
      status = OrderStatuses.COMPLETE.getValue()
    if (order.isCancel)
      status = OrderStatuses.CANCEL.getValue()

    number = order.getNumber()
    emoney = order.getEmoney()
    courier = order.getCourier()

    ordersModel = new ListModelList<OrderProductEntity>(order.orderProductList)

  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    Window wnd = event.getTarget().getSpaceOwner() as Window
    Window owner = wnd.getParent().getParent().getParent() as Window
    owner.detach()
  }

}
