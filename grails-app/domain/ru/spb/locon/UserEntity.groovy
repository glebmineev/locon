package ru.spb.locon

import javax.xml.bind.annotation.XmlTransient
import org.springframework.validation.Errors
import org.grails.datastore.mapping.validation.ValidationErrors

class UserEntity {

  @XmlTransient
  Errors errors = new ValidationErrors(this)

  String login
  String password

  String fio
  String phone
  String email
  String address
  
  static mapping = {
    table: 'user'
    columns {
      id column: 'user_id'
      login column: 'user_login'
      password column: 'user_password'
      fio column: 'user_fio'
      phone column: 'user_phone'
      email column: 'user_email'
      address column: 'user_address'
    }

    orderList lazy: false, cascade: 'all-delete-orphan'
    userGroupList lazy: false, cascade: 'all-delete-orphan'
    version false

  }

  static hasMany = [
    orderList: OrderEntity,
    userGroupList: UserGroupEntity
  ]

  static constraints = {
    login nullable: false
    password nullable: false
    fio nullable: false
    phone nullable: true
    email nullable: false
    address nullable: false
  }

  static transients = ['errors']

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
