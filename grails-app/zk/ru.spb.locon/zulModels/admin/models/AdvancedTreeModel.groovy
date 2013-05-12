package ru.spb.locon.zulModels.admin.models

import org.zkoss.zul.DefaultTreeModel
import org.zkoss.zul.DefaultTreeNode
import ru.spb.locon.CategoryEntity
import ru.spb.locon.wrappers.CategoryTreeNode

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/17/13
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
class AdvancedTreeModel extends DefaultTreeModel<CategoryEntity> {

  /**
   *
   */
  private static final long serialVersionUID = -5513180500300189445L;

  DefaultTreeNode<CategoryEntity> _root;

  public AdvancedTreeModel(CategoryTreeNode contactTreeNode) {
    super(contactTreeNode);
    _root = contactTreeNode;
  }

  /**
   * remove the nodes which parent is <code>parent</code> with indexes
   * <code>indexes</code>
   *
   * @param parent
   *            The parent of nodes are removed
   * @param indexFrom
   *            the lower index of the change range
   * @param indexTo
   *            the upper index of the change range
   * @throws IndexOutOfBoundsException
   *             - indexFrom < 0 or indexTo > number of parent's children
   */
  public void remove(DefaultTreeNode<CategoryEntity> parent, int indexFrom, int indexTo) throws IndexOutOfBoundsException {
    DefaultTreeNode<CategoryEntity> stn = parent;
    for (int i = indexTo; i >= indexFrom; i--)
      try {
        stn.getChildren().remove(i);
      } catch (Exception exp) {
        exp.printStackTrace();
      }
  }

  public void remove(DefaultTreeNode<CategoryEntity> target) throws IndexOutOfBoundsException {
    int index = 0;
    DefaultTreeNode<CategoryEntity> parent = null;
    // find the parent and index of target
    parent = dfSearchParent(_root, target);
    for (index = 0; index < parent.getChildCount(); index++) {
      if (parent.getChildAt(index).equals(target)) {
        break;
      }
    }
    remove(parent, index, index);
  }

  /**
   * insert new nodes which parent is <code>parent</code> with indexes
   * <code>indexes</code> by new nodes <code>newNodes</code>
   *
   * @param parent
   *            The parent of nodes are inserted
   * @param indexFrom
   *            the lower index of the change range
   * @param indexTo
   *            the upper index of the change range
   * @param newNodes
   *            New nodes which are inserted
   * @throws IndexOutOfBoundsException
   *             - indexFrom < 0 or indexTo > number of parent's children
   */
  public void insert(DefaultTreeNode<CategoryEntity> parent, int indexFrom, int indexTo, DefaultTreeNode<CategoryEntity>[] newNodes)
  throws IndexOutOfBoundsException {
    DefaultTreeNode<CategoryEntity> stn = parent;
    for (int i = indexFrom; i <= indexTo; i++) {
      try {
        stn.getChildren().add(i, newNodes[i - indexFrom])
      } catch (Exception exp) {
        throw new IndexOutOfBoundsException("Out of bound: " + i + " while size=" + stn.getChildren().size());
      }
    }
  }

  /**
   * append new nodes which parent is <code>parent</code> by new nodes
   * <code>newNodes</code>
   *
   * @param parent
   *            The parent of nodes are appended
   * @param newNodes
   *            New nodes which are appended
   */
  public void add(DefaultTreeNode<CategoryEntity> parent, DefaultTreeNode<CategoryEntity>[] newNodes) {
    DefaultTreeNode<CategoryEntity> stn = (DefaultTreeNode<CategoryEntity>) parent;

    //for (int i = 0; i < newNodes.length; i++)
    //  stn.getChildren().add(newNodes[i]);
    stn.children = new ArrayList<DefaultTreeNode<CategoryEntity>>()

  }

  private DefaultTreeNode<CategoryEntity> dfSearchParent(DefaultTreeNode<CategoryEntity> node, DefaultTreeNode<CategoryEntity> target) {
    if (node.getChildren() != null && node.getChildren().contains(target)) {
      return node;
    } else {
      int size = getChildCount(node);
      for (int i = 0; i < size; i++) {
        DefaultTreeNode<CategoryEntity> parent = dfSearchParent((DefaultTreeNode<CategoryEntity>) getChild(node, i), target);
        if (parent != null) {
          return parent;
        }
      }
    }
    return null;
  }

}
