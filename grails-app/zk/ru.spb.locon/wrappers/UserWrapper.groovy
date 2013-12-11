package ru.spb.locon.wrappers

import com.google.common.base.Function
import com.google.common.collect.Collections2
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.ListModelList
import ru.spb.locon.*

class UserWrapper extends IdentWrapper implements Wrapper {

  String login
  String password

  String fio
  String phone
  String email
  String address

  ListModelList<RoleWrapper> roles

  UserWrapper memento
  boolean editingStatus = false

  UserWrapper() {}

  UserWrapper(UserEntity user) {
    this.id = user.id
    this.login = user.login
    this.password = null
    this.fio = user.fio
    this.phone = user.phone
    this.email = user.email
    this.address = user.address

    //Тянем все группы.
    List<RoleWrapper> allRoles = Collections2.transform(RoleEntity.list(), new Function<RoleEntity, RoleWrapper>() {
      @Override
      RoleWrapper apply(RoleEntity entity) {
        return new RoleWrapper(RoleEntity.get(entity.id))
      }
    }) as List<RoleWrapper>

    //Выделяем в листбоксе группы в которых состоит пользователь.
    List<RoleWrapper> userRoles = new ArrayList<RoleWrapper>()
    user.groups.each {it ->
      allRoles.each {fromAll ->
        if (it.id == fromAll.id)
          userRoles.add(fromAll)
      }
    }

    this.roles = new BindingListModelList<RoleWrapper>(allRoles, true)
    this.roles.setMultiple(true)
    this.roles.setSelection(userRoles)
  }

  public void restore(){
    this.id = memento.id
    this.name = memento.login
    this.password = null
    this.fio = memento.fio
    this.phone = memento.phone
    this.email = memento.email
    this.address = memento.address
    this.roles.clear()
    this.roles.addAll(memento.roles)
  }

  @Override
  @SuppressWarnings(value = "unchecked")
  protected UserWrapper clone(){
    UserWrapper cloned = new UserWrapper()
    cloned.setId(id)
    cloned.setName(name)
    cloned.setPassword(null)
    cloned.setFio(fio)
    cloned.setAddress(address)
    cloned.setEmail(email)
    cloned.setLogin(login)
    cloned.setPhone(phone)
    ListModelList<RoleWrapper> roles = new ListModelList<RoleWrapper>(roles)
    cloned.setRoles(roles)
    return cloned
  }

}
