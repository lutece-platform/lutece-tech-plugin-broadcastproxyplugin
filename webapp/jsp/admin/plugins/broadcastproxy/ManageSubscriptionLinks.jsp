<jsp:useBean id="managesubscriptionSubscriptionLink" scope="session" class="fr.paris.lutece.plugins.broadcastproxy.web.SubscriptionLinkJspBean" />
<% String strContent = managesubscriptionSubscriptionLink.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
