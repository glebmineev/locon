package ru.spb.locon.zulModels.admin

import com.google.common.base.Function
import com.google.common.collect.Collections2
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.ListModelList
import ru.spb.locon.RoleEntity
import ru.spb.locon.wrappers.RoleWrapper

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 12/8/13
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */
class Test {

  ListModelList<RoleWrapper> roles

  @Init
  public void init(){
    def arg = Executions.getCurrent().getArg()
    List<RoleWrapper> allRoles = Collections2.transform(RoleEntity.list(), new Function<RoleEntity, RoleWrapper>() {
      @Override
      RoleWrapper apply(RoleEntity entity) {
        return new RoleWrapper(RoleEntity.get(entity.id))
      }
    }) as List<RoleWrapper>

    this.roles = new ListModelList<RoleWrapper>(allRoles)
  }

}
