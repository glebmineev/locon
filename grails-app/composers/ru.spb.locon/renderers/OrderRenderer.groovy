package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import ru.spb.locon.OrderEntity
import org.zkoss.zul.Listcell

/**
 * User: Gleb
 * Date: 30.09.12
 * Time: 16:51
 */
class OrderRenderer implements ListitemRenderer<OrderEntity> {

  @Override
  void render(org.zkoss.zul.Listitem listitem, OrderEntity entity, int i) {
    listitem.setValue(entity)

    //Номер
    Listcell numberCell = new Listcell(entity.number)
    numberCell.setParent(listitem)

    //Номер
    Listcell fioCell = new Listcell(entity.fio)
    fioCell.setParent(listitem)

    //Телефон
    Listcell phoneCell = new Listcell(entity.phone)
    phoneCell.setParent(listitem)

    //E-mail
    Listcell emailCell = new Listcell(entity.email)
    emailCell.setParent(listitem)

    //Статус
    String status = "Проблемы с определением статуса"
    if (entity.isProcessed) 
      status = "В обработке"
    if (entity.isCancel)
      status = "Отменен"
    if (entity.isComplete)
      status = "Выполнен"
    Listcell statusCell = new Listcell(status)
    statusCell.setParent(listitem)

  }
}
