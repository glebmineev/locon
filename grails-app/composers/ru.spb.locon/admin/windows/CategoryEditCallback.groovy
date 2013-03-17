package ru.spb.locon.admin.windows

import ru.spb.locon.CategoryEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/17/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CategoryEditCallback {

  void refreshModel(CategoryEntity category)

}