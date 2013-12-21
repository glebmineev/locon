package ru.spb.locon

import com.google.common.base.Strings
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.AfterCompose
import org.zkoss.zk.grails.composer.GrailsBindComposer
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.EventQueues
import ru.spb.locon.wrappers.ProductWrapper
import ru.spb.locon.zulModels.shop.ShowcaseViewModel

class ShowcaseBindComposer extends BindComposer implements IShowcaseComposer {

  ShowcaseService showcaseService = ApplicationHolder.getApplication().getMainContext().getBean("showcaseService") as ShowcaseService

  ShowcaseBindComposer() {
    //динамически добавляем метод runAsync при конструировании класса компоузера.
    ShowcaseBindComposer.class.metaClass.runAsync = { Runnable runme ->
      ApplicationHolder.getApplication().getMainContext().executorService.withPersistence(runme)
    }
  }
  @AfterCompose
  void doAfterCompose(Component component){
    if (Strings.isNullOrEmpty(component.getId()) && !"catalog".equals(component.getId())) {
    Desktop desktop = Executions.getCurrent().getDesktop()
    desktop.enableServerPush(true)
    ShowcaseViewModel showcaseViewModel = getViewModel() as ShowcaseViewModel
    runAsync {
      showcaseService.setComposer(this)
      showcaseService.setDesktop(desktop)
      showcaseService.processEntities(showcaseViewModel.getAllProducts())
    }
    }
    int r =0
  }

  @Override
  void isBusy(boolean isBusy) {
    Map<String, Object> args = new HashMap<String, Object>()
    args.put("isVisible", isBusy)
    BindUtils.postGlobalCommand("showcasequeue", EventQueues.DESKTOP, "isBusy", args);
  }

  @Override
  void doProcess() {
    BindUtils.postGlobalCommand(null, null, "refreshShowcase", null);
  }

  @Override
  void complete(List<ProductWrapper> data) {
    Map<String, Object> args = new HashMap<String, Object>()
    args.put("data", data)
    BindUtils.postGlobalCommand("showcasequeue", EventQueues.DESKTOP, "refreshShowcase", args);
  }
}
