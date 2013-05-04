package ru.spb.locon.admin.windows

import org.zkoss.zk.ui.Page
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.*
import org.zkoss.zk.ui.event.*
import ru.spb.locon.*

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/17/13
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
class CategoryEditWnd extends Window {

  ZulService zulService = SpringUtil.getApplicationContext().getBean("zulService") as ZulService

  CategoryEditWnd(Page page, CategoryEntity parent, CategoryEditCallback callback) {
    super()

    setTitle("Создание новой категории")
    setWidth("440px")
    setHeight("150px")
    setShadow(true)
    setPage(page)

    doModal()
    setClosable(true)
    setPosition("center")

    Vbox vbox = new Vbox()

    Groupbox groupbox = new Groupbox()

    //Блок категорий.
    Hbox comboboxContainer = new Hbox()
    String categoryName
    if (parent != null)
      categoryName = parent.name
    else
      categoryName = "Корневая категория"

    comboboxContainer.appendChild(new Label("Категория входит в: "))
    comboboxContainer.appendChild(new Label(categoryName))

    //Блок наименования
    Hbox nameContainer = new Hbox()
    nameContainer.setWidths("200px, 240px")
    Textbox textbox = new Textbox()
    textbox.setWidth("240px")
    nameContainer.appendChild(new Label("Наименование"))
    nameContainer.appendChild(textbox)

    //Блок кнопок.
    Hbox buttonContainer = new Hbox()
    buttonContainer.setAlign("center")
    Button ok = new Button("Сохранить")
    ok.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) {
        String name = textbox.getValue()
        if (!name.isEmpty()) {
          CategoryEntity.withTransaction {
            CategoryEntity categoryEntity = new CategoryEntity()
            categoryEntity.setName(name)

            if (parent != null)
              categoryEntity.setParentCategory(parent)

            categoryEntity.save(flush: true)
            callback.refreshModel(categoryEntity)
          }

          detach()
        } else {
          zulService.showErrors("Поле наименование не может быть пустым")
        }

      }
    })

    Button close = new Button("Отмена")
    close.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) {
        detach()
      }
    })

    buttonContainer.appendChild(ok)
    buttonContainer.appendChild(close)

    vbox.appendChild(comboboxContainer)
    vbox.appendChild(nameContainer)
    vbox.appendChild(buttonContainer)
    groupbox.appendChild(vbox)
    appendChild(groupbox)
  }

}
