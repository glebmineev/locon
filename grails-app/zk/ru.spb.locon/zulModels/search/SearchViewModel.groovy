package ru.spb.locon.zulModels.search

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/28/13
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
class SearchViewModel {

  @Init
  public void init(){}

  @Command
  public void sendRequest(@BindingParam("request") String request){
    Executions.sendRedirect("/search/index?keyword=${request}")
  }

}
