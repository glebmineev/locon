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

  /**
   * Активирован ли пользователь.
   */
  boolean isActive = false

  /**
  * Код активации пользователя.
   */
  String activateCode = "admin"

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
      groups joinTable: [name: 'user_role', key: 'user_id']
      isActive column: 'user_is_active'
      activateCode column: 'user_activate_code'
    }

    /**
     * Если писать groups cascade: 'all-delete-orphan'
     * удалиться группа.
     */

    orderList cascade: 'all-delete-orphan'
    messageList cascade: 'all-delete-orphan'

    version false

  }

  static belongsTo = RoleEntity

  static hasMany = [
    orderList: OrderEntity,
    messageList: MessageEntity,
    groups: RoleEntity
  ]

  static constraints = {
    login nullable: false
    password nullable: false
    fio nullable: false
    phone nullable: true
    email nullable: false
    address nullable: false
    isActive nullable: false
    activateCode nullable: false
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
