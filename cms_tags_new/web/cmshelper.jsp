<%@page language="java" import="uz.fido_biznes.cms.*"%><%
  User user = (User) session.(Resource.STR_USER);
  Resource.evalRequest(user, request, response);
%>