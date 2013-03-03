package ru.spb.locon.admin

import org.zkoss.bind.annotation.Init
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.DefaultTreeModel
import org.zkoss.zul.DefaultTreeNode
import org.zkoss.zul.ListModelList
import ru.spb.locon.CategoryEntity
import ru.spb.locon.FilterEntity
import ru.spb.locon.FilterGroupEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.tree.node.CategoryTreeNode


/**
 * Created with IntelliJ IDEA.
 * User: Gleb-PC
 * Date: 12.02.13
 * Time: 20:07
 * To change this template use File | Settings | File Templates.
 */
class EditorViewModel {

  DefaultTreeModel categoryTreeModel
  ListModelList<ProductEntity> productsModel
  ProductsRenderer productsRenderer

  ListModelList<FilterEntity> manufModel
  ListModelList<FilterEntity> usageModel

  @Init
  public void init() {
    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    CategoryTreeNode root = new CategoryTreeNode(null, new ArrayList<CategoryTreeNode>())
    createTreeModel(root, categories)
    categoryTreeModel = new DefaultTreeModel(root)

    productsModel = new BindingListModelList<ProductEntity>(ProductEntity.list(), true)
    productsRenderer = new ProductsRenderer()

    manufModel = new BindingListModelList<FilterEntity>(FilterEntity.findAllWhere(filterGroup: FilterGroupEntity.findByName("Производитель")), true)
    usageModel = new BindingListModelList<FilterEntity>(FilterEntity.findAllWhere(filterGroup: FilterGroupEntity.findByName("Применение")), true)
  }

  void createTreeModel(CategoryTreeNode parent, List<CategoryEntity> children) {
    children.each { CategoryEntity category ->
      CategoryTreeNode node = new CategoryTreeNode(category, new ArrayList<CategoryTreeNode>())
      parent.children.add(node)
      if (category.listCategory != null &&
          category.listCategory.size() > 0) {
        createTreeModel(node, category.listCategory.asList())
      }
    }
  }



}
