package ru.spb.locon.wrappers

import org.zkoss.zul.DefaultTreeNode
import org.zkoss.zul.TreeNode
import ru.spb.locon.CategoryEntity

/**
 * User: Gleb
 * Date: 01.09.12
 * Time: 18:05
 */
class CategoryTreeNode extends DefaultTreeNode<CategoryEntity>{

  private boolean open = false
  private boolean selected = false
  private boolean editingStatus = false
  String name

  CategoryTreeNode(CategoryEntity data, String name) {
    super(data, new LinkedList<TreeNode<CategoryEntity>>())
    this.name = name
  }

  public boolean isLeaf() {
    return getData() != null &&
           getData().getListCategory().isEmpty()
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

  boolean getEditingStatus() {
    return editingStatus
  }

  void setEditingStatus(boolean editingStatus) {
    this.editingStatus = editingStatus
  }



}
