package ru.spb.locon.wrappers

import ru.spb.locon.FilterEntity

class FilterWrapper implements Wrapper {

  Long id
  String name

  FilterWrapper memento
  boolean editingStatus = false

  FilterWrapper(FilterEntity entity){
    this.id = entity.id
    this.name = entity.name
  }

  public void restore(){
    this.name = memento.name
  }

}
