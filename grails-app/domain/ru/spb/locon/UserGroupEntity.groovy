package ru.spb.locon

class UserGroupEntity {

  String name

  static mapping = {
    table: 'usergroup'
    columns {
      id column: 'usergroup_id'
      name column: 'usergroup_name'
    }

    listUsers cascade: 'all-delete-orphan'
    version: false
  }

  static hasMany = [
      listUsers: UserEntity
  ]

  static constraints = {
    name nullable: true
  }

}
