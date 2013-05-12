package ru.spb.locon.zulModels.admin.filters.data

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/11/13
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FilterTypes {

  TEXT_FIELD("textField"),
  MEASURE_FIELD("mesureField"),
  COMBO_FIELD("comboField"),
  NUMBER_FIELD("numberField")

  String name

  FilterTypes(String name) {
    this.name = name
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
  }
}