package ru.spb.locon.admin.filters

import org.zkoss.zul.Textbox
import org.zkoss.zk.ui.event.*
import java.lang.reflect.Field

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 20:07
 */
class TextBoxFilter extends Textbox {

  TextBoxFilter(IFilterCallback callback, Field field) {
    this.setWidth("80%")
    this.setInstant(true)

    this.addEventListener(Events.ON_CHANGING, new EventListener() {

      @Override
      void onEvent(Event t) {
        InputEvent inputEvent = (InputEvent) t

        callback.changed(new ObjectFilter(field, inputEvent.value))

        setSelectionRange(inputEvent.start, inputEvent.start)
      }

    })

  }

}
