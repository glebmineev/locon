<%@ page import="importer.ConvertUtils" %>
<%
  def loginService = grailsApplication.mainContext.getBean("loginService");
%>
<table width="100%">
  <tr>
    <td>
      <table width="100%">
        <tr>
          <g:if test="${loginService.isLogged()}">
            <td>
              <g:img dir="images" file="truck.png"/>
            </td>
            <td>
              <g:link controller="user" action="orders">
                Мои Заказы
              </g:link>
            </td>
            <td>
              <g:link controller="auth" action="logout">
                Выйти
              </g:link>
            </td>
          </g:if>
          <g:else>
            <td>
              <g:img dir="images" file="unlock.png"/>
            </td>
            <td>
              <g:link controller="shop" action="register">
                Регистрация
              </g:link>
            </td>
            <td>
              <g:link controller="auth" action="login">
                Войти
              </g:link>
            </td>
          </g:else>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <g:link controller="shop" action="index">
        <g:img dir="images" file="logo.png"/>
      </g:link>
    </td>
    <td>
      <table width="100%">
        <tr>
          <td colspan="4">
            <table width="100%">
              <tr>
                <td align="right">
                  <g:img dir="images" file="phone.png"/>
                </td>
                <td align="left">
                  8 (812) 448 77 55
                </td>
              </tr>
            </table>
          </td>
          <td colspan="2">
            <table width="100%">
              <tr>
                <td align="right">
                  <g:img dir="images" file="clock.png"/>
                </td>
                <td align="left">
                  мы работаем с 9 до 21
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td>
            <table width="100%">
              <tr>
                <td align="right">
                  <g:img dir="images" file="contacts.png"/>
                </td>
                <td align="left">
                  <g:link controller="shop" action="about" params="[type: 'contacts']">Контакты</g:link>
                </td>
              </tr>
            </table>
          </td>
          <td>
            <table width="100%">
              <tr>
                <td align="right">
                  <g:img dir="images" file="about_us.png"/>
                </td>
                <td align="left">
                  <g:link controller="shop" action="about" params="[type: 'about']">О нас</g:link>
                </td>
              </tr>
            </table>
          </td>
          <td>
            <table width="100%">
              <tr>
                <td align="right">
                  <g:img dir="images" file="transport.png"/>
                </td>
                <td align="left">
                  <g:link controller="shop" action="about" params="[type: 'transport']"
                          style="font-size: 10pt">О доставке</g:link>
                </td>
              </tr>
            </table>
          </td>
          <td>
            <table width="100%">
              <tr>
                <td align="right">
                  <g:img dir="images" file="how_buy.png"/>
                </td>
                <td align="left">
                  <g:link controller="shop" action="about" params="[type: 'transport']">Как покупать</g:link>
                </td>
              </tr>
            </table>
          </td>
          <td colspan="2">
            <g:form controller="search" action="index">
              <g:textField name="query" size="16"/>
              <g:submitButton name="Найти">
                <g:img dir="images" file="search.png"/>
              </g:submitButton>
            </g:form>
          </td>
        </tr>
        <tr>
          <td colspan="6" align="right">
            <table width="50%">
              <tr>
                <td width="100px" align="right">
                  <table>
                    <tr>
                      <td>
                        товаров:
                      </td>
                      <td>
                        <div
                            id="totalCount">${session.getAttribute("totalCount") != null ? session.getAttribute("totalCount") : 0}</div>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="100px" align="right">
                  <table>
                    <tr>
                      <td>
                        <div
                            id="totalPrice">${session.getAttribute("totalPrice") != null ? ConvertUtils.roundFloat((Float) session.getAttribute("totalPrice")) : 0.0F}</div>

                        <div id="sessionCart"></div>
                      </td>
                      <td>
                        руб.
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="48px" align="right">
                  <g:link controller="shop" action="cart">
                    <g:img dir="images" file="cart_red.png"/>
                  </g:link>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>