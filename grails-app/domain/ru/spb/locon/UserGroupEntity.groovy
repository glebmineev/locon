package ru.spb.locon

class UserGroupEntity {

  GroupEntity group
  UserEntity user
  
  static mapping = {
    table: 'usergroup'
    columns {
      id column: 'usergroup_id'
      group fetch: "join", column: 'usergroup_group_id'
      user fetch: "join", column: 'usergroup_user_id'
    }

    version false
  }

  static constraints = {
    group nullable: true
    user nullable: true
  }

}
