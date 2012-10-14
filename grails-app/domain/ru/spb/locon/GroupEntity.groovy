package ru.spb.locon

class GroupEntity {

  String name

  static mapping = {
    table: 'usergroup'
    columns {
      id column: 'usergroup_id'
      name column: 'usergroup_name'
    }

    userGroupList lazy: false, cascade: 'all-delete-orphan'

    version false
  }

  static hasMany = [
      userGroupList: UserGroupEntity
  ]

  static constraints = {
    name nullable: true
  }

}
