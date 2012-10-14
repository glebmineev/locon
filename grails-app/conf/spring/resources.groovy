import grails.spring.BeanBuilder
import grails.util.Environment
import org.apache.commons.lang.StringUtils
import org.apache.naming.resources.ProxyDirContext
import org.codehaus.groovy.grails.context.support.PluginAwareResourceBundleMessageSource
import org.codehaus.groovy.grails.web.context.GrailsConfigUtils
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ContextResource
import javax.naming.directory.DirContext
// Place your Spring DSL code here
beans = {
  messageSource(PluginAwareResourceBundleMessageSource) {
    //  Формируем classPath для сообщений
    //  за основу взят org.codehaus.groovy.grails.plugins.i18n.I18nGrailsPlugin

    // базовая директория с ресурсами
    String baseDir = "grails-app/i18n"

    def bb = new BeanBuilder()
    ApplicationContext appContext = bb.createApplicationContext()

    Set baseNames = []

    // список ресурсов
    def messageResources = ""

    //при запуске под Tomcat приходиться вот так искать путь до messageResources.
    Map properties = appContext.getClassLoader().properties
    ProxyDirContext dirContext = properties.get("resources")
    if (dirContext != null) {
      DirContext context = dirContext.getDirContext()
      Map dirContextProps = context.getProperties()
      String base = (String) dirContextProps.get("docBase")
      messageResources = appContext?.getResources("file:///" + base + "/WEB-INF/${baseDir}/**/*.properties")?.toList()
    } else {
      messageResources = appContext?.getResources("${baseDir}/**/*.properties")?.toList()
    }

    if (messageResources) {
      for (resource in messageResources) {
        // Extract the file path of the file's parent directory
        // that comes after "grails-app/i18n".
        String path
        if (resource instanceof ContextResource) {
          path = StringUtils.substringAfter(resource.pathWithinContext, baseDir)
        }
        else {
          path = StringUtils.substringAfter(resource.path, baseDir)
        }
        // look for an underscore in the file name (not the full path)
        String fileName = resource.filename
        int firstUnderscore = fileName.indexOf('_')

        if (firstUnderscore > 0) {
          // grab everyting up to but not including
          // the first underscore in the file name
          int numberOfCharsToRemove = fileName.length() - firstUnderscore
          int lastCharacterToRetain = -1 * (numberOfCharsToRemove + 1)
          path = path[0..lastCharacterToRetain]
        }
        else {
          // Lop off the extension - the "basenames" property in the
          // message source cannot have entries with an extension.
          path -= ".properties"
        }

        // добавляем в набор только корневую директорию и ту, которая подходит по конфигу
        if (path.lastIndexOf("/") == 0 || path.contains(application.config.app.name)) {
          baseNames << "WEB-INF/" + baseDir + path
        }
      }
    }

    // сортируем чтобы корневая папка была последней
    basenames = baseNames.toArray().sort {it.lastIndexOf("/")}.reverse()
    fallbackToSystemLocale = false
    if (Environment.current.isReloadEnabled() || GrailsConfigUtils.isConfigTrue(application, GroovyPagesTemplateEngine.CONFIG_PROPERTY_GSP_ENABLE_RELOAD)) {
      def cacheSecondsSetting = application?.flatConfig?.get('grails.i18n.cache.seconds')
      if (cacheSecondsSetting != null) {
        cacheSeconds = cacheSecondsSetting as Integer
      } else {
        cacheSeconds = 5
      }
    }
  }

}
