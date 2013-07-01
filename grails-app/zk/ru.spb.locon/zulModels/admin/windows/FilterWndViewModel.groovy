package ru.spb.locon.zulModels.admin.windows

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
import ru.spb.locon.FilterEntity

class FilterWndViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(FilterWndViewModel.class)

  String name
  Long categoryID

  @Init
  public void init() {
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    categoryID = arg.get("categoryID") as Long
  }

  @Command
  public void saveFilter() {
    FilterEntity.withTransaction {
      FilterEntity toSave = new FilterEntity()
      toSave.setName(name)
      toSave.addToCategories(CategoryEntity.get(categoryID))
      if (toSave.validate())
        toSave.save(flush: true)
    }
    Executions.sendRedirect("/admin/filters?categoryID=${categoryID}")
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
