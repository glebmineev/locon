package ru.spb.locon

class UserEntity {

  String firstName
  String lastName

  UserGroupEntity userGroup

  static mapping = {
    table: 'user'
    columns {
      id column: 'user_id'
      firstName column: 'user_firstName'
      lastName column: 'user_lastName'
      userGroup column: 'user_usergroup_id'
    }

    orderList /*lazy: false, */cascade: 'all-delete-orphan'

    version: false

  }

  static hasMany = [
      orderList: OrderEntity
  ]

  static constraints = {
    firstName nullable: true
    lastName nullable: true
    userGroup nullable: true
  }
  
}
