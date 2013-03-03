package ru.spb.locon.tree.node

import org.zkoss.zul.DefaultTreeNode
import ru.spb.locon.CategoryEntity

/**
 * User: Gleb
 * Date: 01.09.12
 * Time: 18:05
 */
class CategoryTreeNode extends DefaultTreeNode<CategoryEntity>{

  private boolean open = false
  private boolean selected = false
  String name

  CategoryTreeNode(CategoryEntity data) {
    super(data)
    if (data != null)
      this.name = data.name
  }

  CategoryTreeNode(CategoryEntity data, java.util.List<? extends DefaultTreeNode<CategoryEntity>> children) {
    super(data, children)
    if (data != null)
      this.name = data.name
  }

  void setOpen(boolean open) {
    this.open = open
  }

  boolean getOpen() {
    return open
  }

  void setSelected(boolean selected) {
    this.selected = selected
  }

  boolean getSelected() {
    return selected
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
  }
}
