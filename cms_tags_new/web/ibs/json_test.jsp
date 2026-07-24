<%@ page contentType="text/html;charset=WINDOWS-1251" language="java" %>
<%@ page import="java.sql.*,java.util.*,java.net.*, uz.fido_biznes.cms.*" %>
<%@ page import="oracle.sql.*, oracle.jdbc.*" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="t" %>
<jsp:useBean id="stored" class="uz.fido_biznes.sql.StoredObject" scope="session" />
<%
//-------------------------------------------------------------------------------------------------
    try {
        ServletCallableStatement cs = null;

        cs = new ServletCallableStatement(stored, request);
        cs.setProcedure("Test");
//        cs.setString("name","valueeee");
//        cs.setJson( "{name:ali,age:30}");
        cs.setJsonName("i_Login");
        cs.execute();

    } catch (Exception ex) {
        out.print(Util.getUserMessage(ex));
    } catch (Throwable ex) {
        out.print(ex.toString());
        ex.printStackTrace();
    }
%><%!
    private boolean isIncorrectReferer(String referer) {
        return false;
    }
%>

<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
</html>
