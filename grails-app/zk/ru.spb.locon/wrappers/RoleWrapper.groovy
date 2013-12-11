package ru.spb.locon.wrappers

import ru.spb.locon.RoleEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 12/8/13
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
class RoleWrapper extends IdentWrapper implements Wrapper {

  RoleWrapper memento

  public RoleWrapper(RoleEntity role){
    this.id = role.id
    this.name = RoleEntity.get(role.id).name
  }

  @Override
  @SuppressWarnings(value = "unchecked")
  protected RoleWrapper clone(){
    RoleWrapper cloned = new RoleWrapper()
    cloned.setId(id)
    cloned.setName(name)
    return cloned
  }

  @Override
  void restore() {
    id = memento.id
    name = memento.name
  }
}
