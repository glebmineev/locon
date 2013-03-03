package ru.spb.locon.admin.renderer

import org.zkoss.zk.ui.Execution
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.*
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.*
import ru.spb.locon.CategoryEntity
import ru.spb.locon.tree.node.CategoryTreeNode

/**
 * Created with IntelliJ IDEA.
 * User: Gleb-PC
 * Date: 15.02.13
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
class EditorTreeRenderer implements TreeitemRenderer<CategoryTreeNode> {

  @Override
  void render(Treeitem treeitem, CategoryTreeNode categoryTreeNode, int i) throws Exception {

    treeitem.setValue(categoryTreeNode.getData())

    Label label = new Label()
    label.setValue(categoryTreeNode.name)

    Textbox textbox = new Textbox()
    textbox.setValue(categoryTreeNode.name)
    textbox.setVisible(false)

    Button ok = new Button()
    Button cancel = new Button()
    Button edit = new Button()
    Button add = new Button()
    add.setImage("/images/add.png")
    add.setVisible(true)

    edit.setImage("/images/pencil.png")
    edit.setVisible(true)

    edit.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {

        label.setVisible(false)
        textbox.setVisible(true)
        ok.setVisible(true)
        cancel.setVisible(true)
        edit.setVisible(false)
        add.setVisible(false)
      }
    })

    add.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {

        Window wnd = new Window()
        Vbox vbox = new Vbox()
        Hbox hbox = new Hbox()

        Textbox name = new Textbox()
        Button save = new Button("Создать")

        save.addEventListener(Events.ON_CLICK, new EventListener() {
          @Override
          void onEvent(Event event) throws Exception {
            String value = name.getValue()
            if (value != null && !value.isEmpty())
              CategoryEntity.withTransaction {
                CategoryEntity data = categoryTreeNode.getData()
                CategoryEntity categoryEntity = new CategoryEntity(
                    name: value,
                    parentCategory: data)
                categoryEntity.save(flush: true)
                wnd.setVisible(false)
                treeitem.getTree().getModel()
              }
          }
        })

        Button close = new Button("Отмена")
        close.addEventListener(Events.ON_CLICK, new EventListener() {
          @Override
          void onEvent(Event event) throws Exception {
            wnd.setVisible(false)
          }
        })
        hbox.appendChild(save)
        hbox.appendChild(close)
        vbox.appendChild(name)
        vbox.appendChild(hbox)
        wnd.appendChild(vbox)
        wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())

        wnd.setPosition("center")
        wnd.doModal();

      }
    })

    //ok.setHoverImage("/success.gif")
    ok.setImage("/images/success.gif")
    ok.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {
        String categoryName = textbox.getValue()
        CategoryEntity data = categoryTreeNode.getData()
        CategoryEntity.withTransaction {
          data.setName(categoryName)
          data.save(flush: true)

          label.setValue(categoryName)
          ok.setVisible(false)
          cancel.setVisible(false)
          edit.setVisible(true)
          add.setVisible(true)
          textbox.setVisible(false)
          label.setVisible(true)

        }
      }
    })

    ok.setVisible(false)
    //cancel.setHoverImage("/success.gif")
    cancel.setImage("/images/failed.png")
    cancel.setVisible(false)

    cancel.addEventListener(Events.ON_CLICK, new EventListener() {
      @Override
      void onEvent(Event t) throws Exception {
        ok.setVisible(false)
        cancel.setVisible(false)
        edit.setVisible(true)
        add.setVisible(true)
        textbox.setVisible(false)
        label.setVisible(true)
      }
    })

    Treerow treerow = new Treerow()

    Treecell labelCell = new Treecell()
    labelCell.appendChild(label)
    labelCell.appendChild(textbox)
    Treecell buttonCell = new Treecell()
    buttonCell.appendChild(ok)
    buttonCell.appendChild(edit)
    buttonCell.appendChild(add)

    buttonCell.appendChild(cancel)

    treerow.appendChild(labelCell)
    treerow.appendChild(buttonCell)
    treeitem.appendChild(treerow)

  }

}
