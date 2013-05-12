package ru.spb.locon.zulModels.cabinet

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Div
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Vlayout
import org.zkoss.zul.Window
import ru.spb.locon.LoginService
import ru.spb.locon.OrderEntity

class OrdersViewModel {

  ListModelList<OrderEntity> ordersModel
  LoginService loginService = SpringUtil.getApplicationContext().getBean("loginService") as LoginService

  @Init
  public void init() {
    List<OrderEntity> ordersList = getOrders()
    if (ordersList != null)
      ordersModel = new ListModelList<OrderEntity>(ordersList)
  }

  List<OrderEntity> getOrders(){
    return OrderEntity.findAllWhere(user: loginService.getCurrentUser()) as List<OrderEntity>
  }

  @Command
  public void openOrder(@BindingParam("order") OrderEntity order){
    Window wnd = new Window()
    wnd.setWidth("80%")
    wnd.setHeight("500px")
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    Div div = new Div()
    div.setStyle("overflow: auto;")
    div.setWidth("100%")
    div.setHeight("500px")
    wnd.appendChild(div)
    Vlayout panel = new Vlayout()
    div.appendChild(panel)

    Map<String, Object> params = new HashMap<String, Object>()
    params.put("orderID", order.id)
    Executions.createComponents("/zul/cabinet/orderItem.zul", panel, params)
    wnd.doModal()
    wnd.setVisible(true)
  }

}
