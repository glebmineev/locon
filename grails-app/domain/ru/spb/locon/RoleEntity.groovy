package ru.spb.locon

class RoleEntity {

  String name

  static mapping = {
    table: 'usergroup'
    columns {
      id column: 'usergroup_id'
      name column: 'usergroup_name'
      users joinTable: [name: 'user_role', key: 'role_id']
    }

    version false
  }

  static hasMany = [
      users: UserEntity
  ]

  static constraints = {
    name nullable: true
  }

}
