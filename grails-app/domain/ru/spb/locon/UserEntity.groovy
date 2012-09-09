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

    version: false

  }

  static constraints = {
    firstName nullable: true
    lastName nullable: true
    userGroup nullable: true
  }
  
}
