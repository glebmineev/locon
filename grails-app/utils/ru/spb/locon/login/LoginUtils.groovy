package ru.spb.locon.login

import ru.spb.locon.*

/**
 * User: Gleb
 * Date: 14.10.12
 * Time: 0:16
 */
class LoginUtils {
  
  public static boolean authenticate(String email, String password){
    boolean result = false
    UserEntity.withTransaction{
      UserEntity user = UserEntity.findWhere(email: email, password: password.encodeAsSHA1())
      if (user != null)
        result = true
    }
    return result
  }

  public static List<String> userGroups(UserEntity user){
    List<String> groups = new ArrayList<String>()
    UserEntity.withTransaction{
      user.groups.each {RoleEntity group ->
        groups.add(group.name)
      }
    }
    return groups
  }

  public static UserEntity getUser(String email, String password){
    UserEntity result = null
    UserEntity.withTransaction{
      result = UserEntity.findWhere(email: email, password: password.encodeAsSHA1())
    }
    return result
  }

}
