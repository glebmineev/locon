package ru.spb.locon.admin.filters

import ru.spb.locon.admin.filters.data.ObjectFilter

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 20:21
 */
interface IFilterCallback {

  void changed(ObjectFilter objectFilter)

}
