<jsp:useBean id="manageBroadcastProxy" scope="session" class="fr.paris.lutece.plugins.broadcastproxy.web.BroadcastproxyJspBean" />
<% String strContent = manageBroadcastProxy.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
