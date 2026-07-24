package uz.fido_biznes.sql;

import java.io.IOException;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import uz.fido_biznes.cms.ServletCallableStatement;
import uz.fido_biznes.cms.Util;

public class StoredObject implements HttpSessionBindingListener {
    public static final String DEFAULT_DELIMETER = "@";
    public static final String NUMBER_FORMAT = "'999999999999999999999D9999999999','NLS_NUMERIC_CHARACTERS=''. '''";
    static final String ARRAY_VARCHAR2 = "ARRAY_VARCHAR2";
    static final String ARRAY_NUMBER = "ARRAY_NUMBER";
    static final String ARRAY_DATE = "ARRAY_DATE";
    private Connection conn = null;
    private CallableStatement caller = null;
    private String lastQuery;
    private ArrayDescriptor arrayVarchar2Descriptor;
    private ArrayDescriptor arrayNumberDescriptor;
    private ArrayDescriptor arrayDateDescriptor;
    private boolean debug;
    private boolean cache;

    public void setDebug(boolean debug) throws Exception {
        this.debug = debug;
        if (debug) {
            this.execProcedure("SQL_Util.Set_Debug(true)");
        }

    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public ArrayDescriptor getArrayDescriptor() {
        return this.arrayVarchar2Descriptor;
    }

    public ArrayDescriptor getArrayDateDescriptor() {
        return this.arrayDateDescriptor;
    }

    public ArrayDescriptor getArrayNumberDescriptor() {
        return this.arrayNumberDescriptor;
    }

    public ArrayDescriptor getArrayVarchar2Descriptor() {
        return this.arrayVarchar2Descriptor;
    }

    public ARRAY convertToARRAY(String[] array) throws SQLException {
        return new ARRAY(this.arrayVarchar2Descriptor, this.conn, array);
    }

    public Connection getConnection() {
        return this.conn;
    }

    public void setConnection(Connection newConnection) throws SQLException {
        if (this.conn != newConnection) {
            if (this.caller != null) {
                try {
                    this.caller.close();
                } catch (Exception var3) {
                }

                this.caller = null;
            }

            this.conn = newConnection;
            this.arrayVarchar2Descriptor = ArrayDescriptor.createDescriptor("ARRAY_VARCHAR2", this.conn);
            this.arrayNumberDescriptor = ArrayDescriptor.createDescriptor("ARRAY_NUMBER", this.conn);
            this.arrayDateDescriptor = ArrayDescriptor.createDescriptor("ARRAY_DATE", this.conn);
            if (this.conn != null) {
                this.caller = this.conn.prepareCall("{?=call SQL_Util.ExecuteStored(?,?)}");
                this.caller.registerOutParameter(1, 12);
            }

        }
    }

    private String execStored(int objType, String objName) throws SQLException {
        synchronized (this.caller) {
            if (this.debug) {
                System.out.println(this.lastQuery);
            }

            this.lastQuery = null;
            this.caller.setInt(2, objType);
            this.caller.setString(3, Util.desanitize(objName));
            this.caller.execute();
            String result = this.caller.getString(1);
            return result;
        }
    }

    public String execFunction(String funcName) throws SQLException {
        this.lastQuery = funcName;
        return this.execStored(1, funcName);
    }

    public String[] execFunction(String funcName, char paramDelimeter) throws SQLException {
        this.lastQuery = funcName;
        String s = this.execStored(1, funcName);
        if (s == null) {
            return null;
        } else {
            String[] tmp = new String[1024];
            int paramCount = 0;

            for (int paramPos = 0; paramPos <= s.length(); ++paramCount) {
                int paramEnd = s.indexOf(paramDelimeter, paramPos);
                if (paramEnd < 0) {
                    paramEnd = s.length();
                }

                tmp[paramCount] = s.substring(paramPos, paramEnd);
                paramPos = paramEnd + 1;
            }

            String[] res = new String[paramCount];

            for (int i = 0; i < paramCount; ++i) {
                res[i] = tmp[i];
            }

            return res;
        }
    }

    public void execProcedure(String procName) throws SQLException {
        this.lastQuery = procName;
        this.execStored(0, procName);
    }

    public String execRequestFunction(String functionName, ServletRequest request) throws Exception {
        ServletCallableStatement cs = new ServletCallableStatement(this, request);
        cs.setFunction(functionName);
        cs.setAllParameters("request");
        cs.execute();
        return cs.getStringResult();
    }

    public void execRequestProcedure(String procedureName, ServletRequest request) throws Exception {
        ServletCallableStatement cs = new ServletCallableStatement(this, request);
        cs.setProcedure(procedureName);
        cs.setAllParameters("request");
        cs.execute();
    }

    public String execJsonRequestFunction(String functionName, ServletRequest request) throws Exception {
        ServletCallableStatement cs = new ServletCallableStatement(this, request);
        cs.setFunction(functionName);
        cs.setJson();
        cs.execute();
        return cs.getStringResult();
    }

    public void execJsonRequestProcedure(String procedureName, ServletRequest request) throws Exception {
        ServletCallableStatement cs = new ServletCallableStatement(this, request);
        cs.setProcedure(procedureName);
        cs.setJson();
        cs.execute();
    }

    public String execSelect(String SQL) throws Exception {
        return this.execSelect(SQL, "");
    }

    public String execSelect(String SQL, String lineDelimeter) throws Exception {
        this.lastQuery = SQL;
        String funcName = "SQL_Util.SQL_GetFirst_Ex('" + quotesSQL(SQL) + "','" + lineDelimeter + "')";
        if (this.debug) {
            System.out.println("======================================");
            System.out.println(funcName);
        }

        return this.execSelectFunc(funcName);
    }

    public String execSelect(String SQL, int pageNumber, int linesPerPage, String lineDelimeter) throws Exception {
        this.lastQuery = SQL;
        String funcName = "SQL_Util.SQL_GetFirst_Ex('" + quotesSQL(SQL) + "'," + pageNumber + ',' + linesPerPage + ",'" + lineDelimeter + "')";
        return this.execSelectFunc(funcName);
    }

    public String[] getPersonalTable(String func) throws SQLException {
        OracleCallableStatement cs = null;

        String[] var6;
        try {
            String query = "begin ? := " + Util.desanitize(func) + "; end;";
            cs = (OracleCallableStatement) this.conn.prepareCall(query);
            cs.registerOutParameter(1, 2003, "PERSONAL_TABLE");
            cs.execute();
            ARRAY arr = cs.getARRAY(1);
            String[] s = (String[]) arr.getArray();
            var6 = s;
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return var6;
    }

    public void printPersonalTable(String func, Writer out) throws SQLException, IOException {
        String[] s = this.getPersonalTable(func);
        if (s != null) {
            for (int i = 0; i < s.length; ++i) {
                out.write(s[i]);
            }
        }

    }

    private String execSelectFunc(String funcName) throws Exception {
        String s = this.execStored(1, funcName);
        int hasMoreData = extractIntValue(s, 0);
        int dataPos = getParamPos(s, 1);
        if (hasMoreData <= 0) {
            return s.substring(dataPos);
        } else {
            StringBuffer sb = new StringBuffer(s.substring(dataPos));

            while (hasMoreData > 0) {
                s = this.execStored(1, "SQL_Util.SQL_GetNext_Ex");
                hasMoreData = extractIntValue(s, 0);
                dataPos = getParamPos(s, 1);
                sb.append(s.substring(dataPos));
            }

            return sb.toString();
        }
    }

    public static int getParamPos(String paramValues, int paramIndex, String paramDelim) {
        if (paramValues == null) {
            return -1;
        } else {
            int delimLen = paramDelim.length();
            int p = 0;

            for (int i = 0; i < paramIndex; ++i) {
                p = paramValues.indexOf(paramDelim, p);
                if (p < 0) {
                    return -1;
                }

                p += delimLen;
            }

            return p;
        }
    }

    public static int getParamPos(String paramValues, int paramIndex) {
        return getParamPos(paramValues, paramIndex, "@");
    }

    public static int getParamEnd(String paramValues, int paramIndex, String paramDelim) {
        int p = getParamPos(paramValues, paramIndex, paramDelim);
        return p < 0 ? -1 : getParamEndAt(paramValues, p, paramDelim);
    }

    public static int getParamEnd(String paramValues, int paramIndex) {
        return getParamEnd(paramValues, paramIndex, "@");
    }

    public static int getParamEndAt(String paramValues, int atPos, String paramDelim) {
        if (paramValues == null) {
            return -1;
        } else {
            int p = paramValues.indexOf(paramDelim, atPos);
            return p < 0 ? paramValues.length() : p;
        }
    }

    public static String extractValue(String paramValues, int paramIndex, String paramDelim) {
        int pp = getParamPos(paramValues, paramIndex, paramDelim);
        if (pp < 0) {
            return "";
        } else {
            int pe = getParamEndAt(paramValues, pp, paramDelim);
            return paramValues.substring(pp, pe);
        }
    }

    public static String extractValue(String paramValues, int paramIndex) {
        return extractValue(paramValues, paramIndex, "@");
    }

    public static int extractIntValue(String paramValues, int paramIndex, int defVal, String paramDelim) {
        String s = extractValue(paramValues, paramIndex, paramDelim);
        if (s != null && s.length() != 0) {
            try {
                return Integer.parseInt(s);
            } catch (Exception var6) {
                return defVal;
            }
        } else {
            return defVal;
        }
    }

    public static int extractIntValue(String paramValues, int paramIndex, int defVal) {
        return extractIntValue(paramValues, paramIndex, defVal, "@");
    }

    public static int extractIntValue(String paramValues, int paramIndex) {
        return extractIntValue(paramValues, paramIndex, 0, "@");
    }

    public static double extractDoubleValue(String paramValues, int paramIndex, double defVal, String paramDelim) {
        String s = extractValue(paramValues, paramIndex, paramDelim);
        if (s != null && s.length() != 0) {
            try {
                return Double.parseDouble(s);
            } catch (Exception var7) {
                return defVal;
            }
        } else {
            return defVal;
        }
    }

    public static double extractDoubleValue(String paramValues, int paramIndex, double defVal) {
        return extractDoubleValue(paramValues, paramIndex, defVal, "@");
    }

    public static double extractDoubleValue(String paramValues, int paramIndex) {
        return extractDoubleValue(paramValues, paramIndex, (double) 0.0F, "@");
    }

    public static String quotesSQL(String s) {
        if (s != null && s.length() != 0) {
            StringBuffer sb = null;

            for (int p = s.length() - 1; (p = s.lastIndexOf(39, p)) >= 0; --p) {
                if (sb == null) {
                    sb = new StringBuffer(s);
                }

                sb.insert(p, '\'');
            }

            if (sb == null) {
                return s;
            } else {
                return sb.toString();
            }
        } else {
            return s;
        }
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public synchronized void valueUnbound(HttpSessionBindingEvent event) {
        try {
            this.setConnection((Connection) null);
        } catch (Exception var3) {
        }

    }

    public String decryptParameterValue(ServletRequest request, String paramName, String entityName) throws Exception {
        String paramValue = Util.quotesEsc(request.getParameter(paramName));
        return this.decryptValue(paramValue, entityName);
    }

    /***Added for Dynamic Grid*****/
    public String decryptParameterValue(ServletRequest request, String paramName, String entityName, String user, String fieldName) throws Exception {
        String paramValue = Util.quotesEsc(request.getParameter(paramName));
        return this.decryptValue(paramValue, entityName, user, fieldName);
    }

    public String[] decryptParameterValues(ServletRequest request, String paramName, String entityName) throws Exception {
        String[] paramValues = request.getParameterValues(paramName);
        return this.decryptValues(paramValues, entityName);
    }

    /***Added for Dynamic Grid*****/
    public String[] decryptParameterValues(ServletRequest request, String paramName, String entityName, String user, String fieldName) throws Exception {
        String[] paramValues = request.getParameterValues(paramName);
        return this.decryptValues(paramValues, entityName, user, fieldName);
    }

    public String decryptValue(String value, String entity) throws Exception {
        return this.isNull(value) ? "" : this.execFunction("Core_Secure_Util.Decrypt('" + Util.quotesSQL(value) + "','" + entity + "')");
    }

    /***Added for Dynamic Grid*****/
    public String decryptValue(String value, String entity, String user, String fieldName) throws Exception {
        return this.isNull(value) ? "" : this.execFunction("Sql_Util.Decrypt_Field('" + Util.quotesSQL(value) + "','" + this.sqlLiteral(entity) + "','" + this.sqlLiteral(user) + "','" + this.sqlLiteral(fieldName) + "')");
    }

    public String[] decryptValues(String[] values, String entity) throws Exception {
        if (values == null) {
            return null;
        } else {
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.decryptValue(values[i], entity);
            }

            return values;
        }
    }

    /***Added for Dynamic Grid*****/
    public String[] decryptValues(String[] values, String entity, String user, String fieldName) throws Exception {
        if (values == null) {
            return null;
        } else {
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.decryptValue(values[i], entity, user, fieldName);
            }

            return values;
        }
    }

    public String encryptParameterValue(ServletRequest request, String paramName, String entityName) throws Exception {
        String paramValue = Util.quotesEsc(request.getParameter(paramName));
        return this.encryptValue(paramValue, entityName);
    }

    public String[] encryptParameterValues(ServletRequest request, String paramName, String entityName) throws Exception {
        String[] paramValues = request.getParameterValues(paramName);
        return this.encryptValues(paramValues, entityName);
    }

    public String encryptValue(String value, String entity) throws Exception {
        return this.isNull(value) ? "" : this.execFunction("Core_Secure_Util.Encrypt('" + Util.quotesSQL(value) + "','" + entity + "')");
    }

    /***Added for Dynamic Grid*****/
    public String encryptValue(String value, String entity, String user, String fieldName) throws Exception {
        return this.isNull(value) ? "" : this.execFunction("Sql_Util.Encrypt_Field('" + Util.quotesSQL(value) + "','" + this.sqlLiteral(entity) + "','" + this.sqlLiteral(user) + "','" + this.sqlLiteral(fieldName) + "')");
    }

    public String[] encryptValues(String[] values, String entity) throws Exception {
        if (values == null) {
            return null;
        } else {
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.encryptValue(values[i], entity);
            }

            return values;
        }
    }

    /***Added for Dynamic Grid*****/
    public String[] encryptValues(String[] values, String entity, String user, String fieldName) throws Exception {
        if (values == null) {
            return null;
        } else {
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.encryptValue(values[i], entity, user, fieldName);
            }

            return values;
        }
    }

    /***Added for Dynamic Grid*****/
    private String sqlLiteral(String value) {
        return value == null ? "" : Util.quotesSQL(value);
    }

    private boolean isNull(String value) {
        return value != null && !value.isEmpty() ? "null".equals(value.toLowerCase()) : true;
    }
}
