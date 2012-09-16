package ru.spb.locon

import org.zkoss.zk.grails.composer.*

import org.zkoss.zul.Window
import org.zkoss.zul.Textbox
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.*
import org.zkoss.util.media.Media
import ru.spb.locon.importer.ImporterExcel
import org.zkoss.zul.Combobox
import org.zkoss.zul.ListModel
import org.zkoss.zk.ui.Executions
import jxl.Workbook

class ImportComposer extends GrailsComposer {

  Textbox fileField
  Button uploadButton
  Button startButton
  Button createDirsButton
  Combobox category

  Media media

  def afterCompose = {Window window ->
    uploadButton.addEventListener(Events.ON_UPLOAD, uploadLister)
    startButton.addEventListener(Events.ON_CLICK, startListener)
    createDirsButton.addEventListener(Events.ON_CLICK, createDirsListener)
    category.appendItem("Для волос")
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
        InputStream is = media.getStreamData()
        String manufacturer = media.getName().replace(".xls", "")
        String category = category.getValue()
        //корневая директория приложения.
        String applicationPath = Executions.current.nativeRequest.getSession().getServletContext().getRealPath("/")
        if (is != null) {
          ImporterExcel importer = new ImporterExcel(Workbook.getWorkbook(is), category, manufacturer)
          importer.doImport()
        }
      }
    }
  }

  EventListener createDirsListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      if (t instanceof MouseEvent) {
        InputStream is = media.getStreamData()
        String manufacturer = media.getName().replace(".xls", "")
        String category = category.getValue()
        //корневая директория приложения.
        String applicationPath = Executions.current.nativeRequest.getSession().getServletContext().getRealPath("/")
        if (is != null) {
          ImporterExcel importer = new ImporterExcel(Workbook.getWorkbook(is), category, manufacturer)
          importer.createImageDirs(applicationPath)
        }
      }
    }
  }

}
