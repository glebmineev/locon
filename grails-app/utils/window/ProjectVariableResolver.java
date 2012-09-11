package window;

import org.zkoss.xel.XelException;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zkplus.spring.SpringUtil;

public class ProjectVariableResolver extends DelegatingVariableResolver {
  @Override
  public Object resolveVariable(String s) throws XelException {
    if ("applicationTagLib".equals(s))
      return SpringUtil.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib");
    else return super.resolveVariable(s);
  }
}

