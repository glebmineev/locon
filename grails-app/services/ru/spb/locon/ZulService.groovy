package ru.spb.locon

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.validation.ObjectError
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import ru.spb.locon.windows.ErrorWindow

class ZulService {
  static Logger logger = LoggerFactory.getLogger(ZulService.class)

  MessageSource messageSource

  static transactional = true

  public void showErrors(List<ObjectError> errors) {
    String message = ""
    errors.each {ObjectError error ->
      message += " - " + messageSource.getMessage(error, new Locale('ru')) + "\r\n"
    }

    ErrorWindow wnd = new ErrorWindow(message)
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())

    wnd.setPosition("center")
    wnd.doModal();
  }

  public void showErrors(String message) {
    ErrorWindow wnd = new ErrorWindow(message)
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())

    wnd.setPosition("center")
    wnd.doModal();
  }

}
