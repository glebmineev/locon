package maket

import org.zkoss.zk.fn.JspFns
import org.zkoss.zk.grails.ZulResponse

class TemplatesTagLib {
    static namespace = "tpl"

    def multiBlock = {attrs, body ->
        multiBlock2(name: attrs.name, body: body())
    }

    def multiBlock2 = {attrs ->
        if (attrs.name?.isEmpty()) {
            throw new Error("Tag multiBlock should have name attribute")
        }
        //if attrs.name allready exists
        if (pageScope.templateParams[attrs.name]) {
            if (pageScope.templateParams[attrs.name] instanceof Collection) {
                pageScope.templateParams[attrs.name] << attrs.body
            } else {
                throw new Error("Tag block was already used with name ${attrs.name}, cannot reuse in multiBlock")
            }
        } else {
            //doesn't exist, initialize
            pageScope.templateParams[attrs.name] = []
            pageScope.templateParams[attrs.name] << attrs.body
        }
    }

    def block = {attrs, body ->
        block2(name: attrs.name, body: body())
    }

    def block2 = {attrs ->
        if (attrs.name?.isEmpty()) {
            throw new Error("Tag block should have name attribute")
        }
        //put body of block tag to templateParams
        pageScope.templateParams[attrs.name] = attrs.body
        for (attr in attrs) {
            if (attr.key != 'name') {
                pageScope.templateParams[attrs.name + '_' + attr.key] = attr.value
            }
        }
    }

    def useTemplate = {attrs, body ->
        if (attrs.template?.isEmpty()) {
            throw new Error("Tag useTemplate should have template attribute")
        }

        //creating map for template params
        try {
            //checking if templateParams exists
            //noinspection GroovyUnusedAssignment
            def size = pageScope.templateParams.size()
            //println(pageScope.templateParams)
        } catch (Exception mpe) {
            pageScope.templateParams = [:]
        }

        //parse body to fill templateparams
        body()

        //use render to use template with model of templateparams
        out << render(template: attrs.template, model: pageScope.templateParams, plugin: attrs.plugin)

        //clean templateparams
        //pageScope.templateParams = null
    }

    def zkHead = {attrs, body ->
        out << JspFns.outZkHtmlTags(servletContext, request, response, null)
    }

    def zkBody = {attrs, body ->
        def zul = attrs.remove('zul')
        if (zul == null) {
            zul = "/${controllerName}/${actionName}.zul"
        }
        def zres = new ZulResponse(zul, request, response, servletContext)
        out << zres.model['source']
    }

}
