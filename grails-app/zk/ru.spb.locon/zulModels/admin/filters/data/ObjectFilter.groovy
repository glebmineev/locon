package ru.spb.locon.zulModels.admin.filters.data

import java.lang.reflect.Field

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 21:32
 */
class ObjectFilter {

  Field field
  Object startValue
  Object endValue

  ObjectFilter(Field field, Object startValue, Object endValue) {
    this.field = field
    this.startValue = startValue
    this.endValue = endValue
  }

  Field getField() {
    return field
  }

  void setField(Field field) {
    this.field = field
  }

  Object getStartValue() {
    return startValue
  }

  void setStartValue(Object startValue) {
    this.startValue = startValue
  }

  Object getEndValue() {
    return endValue
  }

  void setEndValue(Object endValue) {
    this.endValue = endValue
  }

}
