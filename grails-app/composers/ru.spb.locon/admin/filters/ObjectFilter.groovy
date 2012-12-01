package ru.spb.locon.admin.filters

import java.lang.reflect.Field

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 21:32
 */
class ObjectFilter {

  Field field
  Object value

  ObjectFilter(Field field, Object value) {
    this.field = field
    this.value = value
  }

}
