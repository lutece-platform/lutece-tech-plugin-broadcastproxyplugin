<jsp:useBean id="manageBroadCastProxy" scope="session" class="fr.paris.lutece.plugins.broadcastproxy.web.BroadCastProxyJspBean" />
<% String strContent = manageBroadCastProxy.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
