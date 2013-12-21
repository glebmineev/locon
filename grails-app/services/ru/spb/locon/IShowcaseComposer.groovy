package ru.spb.locon

import ru.spb.locon.wrappers.ProductWrapper

public interface IShowcaseComposer {

  void isBusy(boolean isBusy)

  void doProcess()

  void complete(List<ProductWrapper> data)

}