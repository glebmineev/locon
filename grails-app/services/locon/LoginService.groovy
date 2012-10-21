package locon

import login.LoginUtils
import ru.spb.locon.UserEntity

class LoginService {

  static transactional = true
  static scope = "session";
  static proxy = true

  private boolean logged = false
  private UserEntity currentUser = null
  private def params = null
  private List<String> userGroups = new ArrayList<String>()

  public void login(String email, String password){
    logged = LoginUtils.authenticate(email, password)
    if (logged) {
      currentUser = LoginUtils.getUser(email, password)
      userGroups = LoginUtils.userGroups(currentUser)
    }

  }

  public void logout(){
    logged = false
    currentUser = null
    userGroups.clear()
  }

  public void setParams(def params) {
    this.params = params
  }

  public def getParams() {
    return params
  }

  public List<String> getUserGroups() {
    return userGroups
  }

  public boolean isLogged() {
    return logged;
  }

  public UserEntity getCurrentUser() {
    return currentUser
  }

  public void setCurrentUser(UserEntity currentUser) {
    this.currentUser = currentUser
  }


}
