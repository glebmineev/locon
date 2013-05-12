package ru.spb.locon.wrappers

import ru.spb.locon.ManufacturerEntity

class ManufacturerModel implements Cloneable {

  Long id
  String name
  String shortName
  String description

  ManufacturerModel memento
  boolean editingStatus = false

  ManufacturerModel(ManufacturerEntity manufacturer) {
    this.id = manufacturer.id
    this.name = manufacturer.name
    this.shortName = manufacturer.shortName
    this.description = manufacturer.description
  }

  public void restore(){
    this.id = memento.id
    this.name = memento.name
    this.shortName = memento.shortName
    this.description = memento.description
  }

}
