<jsp:useBean id="managecategoryCategory" scope="session" class="fr.paris.lutece.plugins.broadcastproxy.web.CategoryJspBean" />
<% String strContent = managecategoryCategory.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
