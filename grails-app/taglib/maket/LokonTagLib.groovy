package maket

class LokonTagLib {

    static namespace = "lokon"

    def redirectMainPage = {
        response.sendRedirect("${request.contextPath}/shop/index")
    }

}
