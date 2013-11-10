package ru.spb.locon.zulModels.admin

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.Div
import org.zkoss.zul.Filedownload
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Vlayout
import org.zkoss.zul.Window
import ru.spb.locon.LoginService
import ru.spb.locon.OrderEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.PathUtils
import ru.spb.locon.excel.UploadExcelHandler

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */
class OrdersViewModel {

  ListModelList<OrderEntity> ordersModel
  LoginService loginService = (LoginService) SpringUtil.getApplicationContext().getBean("loginService")

  @Init
  public void init() {
    ordersModel = new ListModelList<OrderEntity>(OrderEntity.list(sort: "fio"))
    ordersModel.setMultiple(true)
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
    Executions.createComponents("/zul/admin/orderItem.zul", panel, params)
    wnd.doModal()
    wnd.setVisible(true)
  }

  @Command
  public void unloadInExcel(){
      Set<OrderEntity> selection = ordersModel.getSelection()

      UploadExcelHandler excelHandler = new UploadExcelHandler()
      selection.each {it ->

        excelHandler.createExcelFromOrderEntity(it)
      }

    PathUtils stringUtils = new PathUtils()
    String twoLevelUp = stringUtils.buildPath(2, ApplicationHolder.application.mainContext.servletContext.getRealPath(System.getProperty("file.separator")))

    String to = new PathBuilder()
        .appendPath(twoLevelUp)
        .appendPath("admin")
        .appendPath("orders")
        .appendString(excelHandler.uuid.toString())
        .appendExt("zip")
        .build()

    InputStream is = new FileInputStream(new File(to))
    Filedownload.save(is, "application", "${excelHandler.uuid.toString()}.zip");
  }

}
