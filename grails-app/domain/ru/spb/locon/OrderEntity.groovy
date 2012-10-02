package ru.spb.locon

import org.grails.datastore.mapping.validation.ValidationErrors
import org.springframework.validation.Errors
import javax.xml.bind.annotation.XmlTransient


class OrderEntity {

  @XmlTransient
  Errors errors = new ValidationErrors(this)

  String number
  String fio
  String phone
  String email
  String address
  String notes

  UserEntity user

  Boolean courier
  Boolean emoney

  static mapping = {

    datasource 'ALL'

    table: 'order'
    columns {
      id column: 'order_id'
      number column: 'order_number'
      phone column: 'order_phone'
      email column: 'order_email'
      address column: 'order_address'
      notes column: 'order_notes'
      user fetch: "join", column: 'order_user_id'
      courier column: 'order_courier'
      emoney column: 'order_emoney'
    }

    orderProductList sort: "product", order: "desc", lazy: false, cascade: 'all-delete-orphan'

    version: false

  }

  static hasMany = [
      orderProductList: OrderProductEntity
  ]

  static constraints = {
    number nullable: true
    email maxSize: 1000, nullable: false
    address maxSize: 1000, nullable: false
    fio nullable: false
    phone nullable: true
    notes nullable: true
    user nullable: true
    courier nullable: true
    emoney nullable: true
  }

  public String toString() {
    return fio
  }

  Errors getErrors() {
    return errors
  }

  void setErrors(Errors errors) {
    this.errors = errors
  }

}
