package ru.spb.locon

class InfoEntity {

  String contacts
  String about
  String delivery
  String details

  static mapping = {
    table: 'info'
    columns {
      id column: 'info_id'
      contacts column: 'info_contacts'
      about column: 'info_about'
      delivery column: 'info_delivery'
      details column: 'info_details'
    }

    version false
  }

  static constraints = {
    contacts nullable: true
    about nullable: true
    delivery nullable: true
    details nullable: true
  }

}
