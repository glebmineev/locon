package ru.spb.locon.zulModels.admin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/11/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
class CategoryWndViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(CategoryWndViewModel.class)

  String name
  String parent
  Long parentID

  @Init
  public void init() {
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    parentID = arg.get("parentID") as Long
    if (parentID != null)
      this.parent = CategoryEntity.get(parentID).name
    else
      this.parent = "Корневая категория"
  }

  @Command
  public void addCategory(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    CategoryEntity.withTransaction {
      CategoryEntity categoryEntity = new CategoryEntity()
      categoryEntity.setName(name)
      if (parent != null)
        categoryEntity.setParentCategory(CategoryEntity.get(parentID))
      categoryEntity.save(flush: true)

      Executions.sendRedirect("/admin/editor?categoryID=${categoryEntity.id}")

    }

  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    try {
      Window wnd = event.getTarget().getSpaceOwner() as Window
      Window owner = wnd as Window
      owner.detach()
    } catch (Exception ex) {
      log.debug(ex.getMessage())
    }
  }

}
