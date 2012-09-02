package ru.spb.locon.tree.node

import org.zkoss.zul.DefaultTreeNode
import locon.CategoryEntity
import org.zkoss.zul.TreeNode

/**
 * User: Gleb
 * Date: 01.09.12
 * Time: 18:05
 */
class CategoryTreeNode extends DefaultTreeNode<CategoryEntity>{

  private boolean open = false
  private boolean selected = false

  CategoryTreeNode(CategoryEntity data) {
    super(data)
  }

  CategoryTreeNode(CategoryEntity data, java.util.List<? extends DefaultTreeNode<CategoryEntity>> children) {
    super(data, children)
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

}
