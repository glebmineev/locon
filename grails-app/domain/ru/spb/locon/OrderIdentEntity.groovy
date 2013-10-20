package ru.spb.locon

class OrderIdentEntity {

  Long ident

  static mapping = {

    datasource 'ALL'

    table: 'orderident'
    columns {
      id column: 'orderident_id'
      ident column: 'orderident_ident'
    }

    version false

  }

  static constraints = {
    ident nullable: false
  }

}
