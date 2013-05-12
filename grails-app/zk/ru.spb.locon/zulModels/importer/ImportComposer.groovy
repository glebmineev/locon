package ru.spb.locon.zulModels.importer

import org.zkoss.zk.grails.composer.*

import org.zkoss.zk.ui.event.*
import org.zkoss.util.media.Media

import org.zkoss.zul.Combobox
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.IImporterComposer
import ru.spb.locon.ImportService
import org.zkoss.zul.*
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ZulService
import ru.spb.locon.zulModels.importer.exceptions.ImportException

class ImportComposer extends GrailsComposer implements IImporterComposer {

  Div upload
  Textbox fileField
  Button uploadButton
  Button startButton
  Combobox category

  Div process
  Label info
  Label catalogName
  Progressmeter progressmeter
  Grid importResult

  ListModelList<ResultItem> model = new ListModelList<ResultItem>()

  ImportService importService = ApplicationHolder.getApplication().getMainContext().getBean("importService") as ImportService
  ZulService zulService = ApplicationHolder.getApplication().getMainContext().getBean("zulService") as ZulService

  Media media

  ImportComposer() {
    //динамически добавляем метод runAsync при конструировании класса компоузера.
    ImportComposer.class.metaClass.runAsync = { Runnable runme ->
      ApplicationHolder.getApplication().getMainContext().executorService.withPersistence(runme)
    }
  }

  def afterCompose = { Window window ->
    enablePush()

    uploadButton.addEventListener(Events.ON_UPLOAD, uploadLister)

    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    categories.each { CategoryEntity item ->
      category.appendItem(item.name)
    }

    importResult.setModel(model)
    importResult.setRowRenderer(new ImportResultRenderer())

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

  public void onClick_startButton(Event event) {
    try {

      if (!"xls".equals(media.getFormat()))
        throw new ImportException("Формат загружаемого файла должен быть xls!")

      upload.setVisible(false)
      process.setVisible(true)

      InputStream is = media.getStreamData()
      String manufacturer = media.getName().replace(".xls", "")
      catalogName.setValue("Импорт каталога ${manufacturer}")
      catalogName.setStyle("font-size: 16pt;")
      String category = category.getValue()
      if (is != null) {
        runAsync {
          importService.setDesktop(desktop)
          importService.setComposer(this)
          importService.setMenuCategory(category)
          importService.setManufacturer(manufacturer)
          importService.doImport(is)
        }
      }
    } catch (ImportException ex) {
      zulService.showErrors("Формат загружаемого файла должен быть xls!")
    }

  }

  @Override
  void addRow(ImportEvent event) {
    if (event.state == ImportEvent.STATES.START) {
      model.add(new ResultItem(event))
    } else {
      ResultItem item = model.asList().find { it.id == event.id }
      if (item != null) {
        ((ResultCell) importResult.getFellow("item_${event.id}_PROCESS")).changeState(event)
      }
    }
  }

  @Override
  void doProgress(int value, String message) {
    info.setValue(message)
    progressmeter.setValue(value)
  }

  @Override
  void complete(String message) {
    info.setValue(message)
    progressmeter.setValue(100)
  }
}
