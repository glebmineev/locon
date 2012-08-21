package ru.spb.locon

import org.zkoss.zk.grails.composer.*

import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zul.Window
import org.zkoss.zul.Tree
import org.zkoss.zul.Treecols
import org.zkoss.zul.Treecol
import org.zkoss.zul.Treechildren
import locon.CategoryEntity
import org.zkoss.zul.Treeitem
import org.zkoss.zul.Treerow
import org.zkoss.zul.Treecell
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listheader
import org.zkoss.zul.ListModelList
import locon.ProductEntity
import org.zkoss.zul.Listhead

class CatalogComposer extends GrailsComposer {

    Tree categoryTree

    Listbox products

    String categoryId
  
    def afterCompose = {Window window ->

      //заполняем listbox.
      products.appendChild(new Listhead())
      products.listhead.appendChild(new Listheader("Наименование"))
      products.setMold("paging")
      products.setPageSize(20)
      ListModelList<ProductEntity> model = ProductEntity.findAll()
      products.setModel(model)


      //Заголовок дерева.
      Treecols header = new Treecols()
      header.setSizable(true)
      Treecol name = new Treecol("Наименование")
      header.appendChild(name)

      categoryTree.appendChild(header)
      Treechildren start = new Treechildren()

      //берем все категории без парента.
      List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
      createNode(start, categories)
      categoryTree.appendChild(start)
    }

    private void createNode(Treechildren parent, Collection<CategoryEntity> categories) {
      categories.each {CategoryEntity category ->
        //строим ноду дерева.
        Treeitem treeitem = new Treeitem()
        
        Treerow treerow = new Treerow()
        Treecell treecell = new Treecell(category.name)
        treerow.appendChild(treecell)
        treeitem.appendChild(treerow)

        //берем все дочерние категории.
        Set<CategoryEntity> childs = category.getListCategory()
        if (childs != null && childs.size() > 0) {
          Treechildren treechildren = new Treechildren()
          treeitem.appendChild(treechildren)
          createNode(treechildren, childs)
        }

        parent.appendChild(treeitem)
      }

    }
}
