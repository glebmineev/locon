package ru.spb.locon.zulModels.admin.filters.data

import ru.spb.locon.zulModels.admin.filters.IFilterCallback

import java.lang.reflect.Field

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/6/13
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
class FilterBean {

  IFilterCallback callback
  Field field
  FilterTypes filterType

  FilterBean(IFilterCallback callback, Field field, FilterTypes filterType) {
    this.callback = callback
    this.field = field
    this.filterType = filterType
  }

  IFilterCallback getCallback() {
    return callback
  }

  void setCallback(IFilterCallback callback) {
    this.callback = callback
  }

  Field getField() {
    return field
  }

  void setField(Field field) {
    this.field = field
  }

  String getFilterType() {
    return filterType.getName()
  }

  void setFilterType(FilterTypes type) {
    this.filterType = type
  }
}
