package ru.spb.locon.common

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 11:24 PM
 * To change this template use File | Settings | File Templates.
 */
public enum OrderStatuses {

  IS_PROCESSING("В обработке"), COMPLETE("Завершен"), CANCEL("Отменен")

  String value

  OrderStatuses(String value) {
    this.value = value
  }

  String getValue() {
    return value
  }

}