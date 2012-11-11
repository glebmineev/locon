package ru.spb.locon.importer

import org.zkoss.zk.grails.composer.*

import org.zkoss.zul.Window
import org.zkoss.zul.Textbox
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.*
import org.zkoss.util.media.Media

import org.zkoss.zul.Combobox

import jxl.Workbook

import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.ImportService
import org.zkoss.zul.Listbox
import org.zkoss.zul.Div
import org.zkoss.zul.ListModelList
import org.zkoss.zk.ui.Executions

class ImportComposer extends GrailsComposer {

  Div upload
  Textbox fileField
  Button uploadButton
  Button startButton
  Combobox category

  Div process
  Listbox importResult
  ListModelList<ResultItem> model = new ListModelList<ResultItem>()

  ImportService importService = (ImportService) ApplicationHolder.getApplication().getMainContext().getBean("importService")

  Media media

  ImportComposer() {
    //динамически добавляем метод runAsync при конструировании класса компоузера.
    ImportComposer.class.metaClass.runAsync = { Runnable runme ->
      ApplicationHolder.getApplication().getMainContext().executorService.withPersistence(runme)
    }
  }

  def afterCompose = {Window window ->
    enablePush()

    uploadButton.addEventListener(Events.ON_UPLOAD, uploadLister)
    startButton.addEventListener(Events.ON_CLICK, startListener)
    category.appendItem("Для волос")

    importResult.setModel(model)
    importResult.setItemRenderer(new ImportResultRenderer())

    // подписываемся на очередь событий импорта
    EventQueue<ImportEvent> queue = EventQueues.lookup("catalogImportQueue", EventQueues.DESKTOP, true)
    queue.subscribe(new org.zkoss.zk.ui.event.EventListener<ImportEvent>() {

      @Override
      void onEvent(ImportEvent event) {
        if (event.state == ImportEvent.STATES.START) {
          model.add(new ResultItem(event))
        } else {
          ResultItem item = model.asList().find {it.id == event.id}
          if (item != null) {
            ((ResultCell) importResult.getFellow("item_${event.id}_PROCESS")).changeState(event)
          }
        }
      }

    })
  }

  EventListener uploadLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      if (t instanceof UploadEvent) {
        UploadEvent event = (UploadEvent) t
        //берем содержимое файла.
        media = event.getMedia()
        // записываем в поле имя файла
        fileField.value = media.name
        startButton.disabled = false
      }
    }
  }

  EventListener startListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      if (t instanceof MouseEvent) {

        upload.setVisible(false)
        process.setVisible(true)

        InputStream is = media.getStreamData()
        String manufacturer = media.getName().replace(".xls", "")
        String category = category.getValue()
        //String applicationPath = Executions.current.nativeRequest.getSession().getServletContext().getRealPath("/")
        if (is != null) {
          runAsync {
            importService.setDesktop(desktop)
            //importService.setApplicationPath(applicationPath)
            importService.setMenuCategory(category)
            importService.setManufacturer(manufacturer)
            importService.setWrkbook(Workbook.getWorkbook(is))
            importService.doImport()
          }
        }
      }
    }
  }

}
