package ru.spb.locon.renderers

import locon.CategoryEntity
import org.zkoss.zul.DefaultTreeNode
import org.zkoss.zul.TreeitemRenderer
import org.zkoss.zul.Treerow
import org.zkoss.zul.Treecell
import ru.spb.locon.tree.node.CategoryTreeNode
import org.zkoss.zul.Treeitem
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Treechildren
import org.zkoss.zul.TreeNode

/**
 * User: Gleb
 * Date: 27.08.12
 * Time: 22:11
 */
class TreeCategoryRenderer implements TreeitemRenderer<CategoryTreeNode> {

  @Override
  void render(org.zkoss.zul.Treeitem treeitem, CategoryTreeNode node, int i) {
    CategoryEntity category = node.getData()
    treeitem.setValue(category)
    treeitem.setOpen(node.open)
    treeitem.setSelected(node.selected)
    Treerow row = new Treerow()
    treeitem.appendChild(row)
    row.appendChild(new Treecell(category.getName()))
  }

}
