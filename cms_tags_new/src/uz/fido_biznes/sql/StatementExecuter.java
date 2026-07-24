package uz.fido_biznes.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import oracle.jdbc.OracleCallableStatement;
import uz.fido_biznes.cms.PsbJson;


public class StatementExecuter {
    private String statementName;
    private int statementType;
    private StoredObject stored;
    private Vector parameters;
    private Vector jHashEncryptionValues;
    private Parameter functionResult;
    private String hashParameterName;
    private Hashtable hashParameterValue;
    private PsbJson psbJsonParameterValue;
    private String psbJsonParamName;
    private static final int FUNCTION = 1;
    private static final int PROCEDURE = 2;
    private static final int QUERY = 3;

    private void checkStatementIsDefined() {
        if (this.statementName == null) {
            throw new IllegalStateException("Procedure or Function name is missing.");
        }
    }

    private void checkQueryStatementIsDefined() {
        if (this.statementName == null || this.statementType != 3) {
            throw new IllegalStateException("Statement is not Query.");
        }
    }

    private void setStatement(int statementType, String statementName) {
        if (this.statementName != null) {
            throw new IllegalStateException("Procedure or Function name is already defined.");
        } else {
            this.statementType = statementType;
            this.statementName = statementName;
            this.parameters = new Vector();
            this.jHashEncryptionValues = new Vector();
        }
    }

    public void setStored(StoredObject stored) {
        if (stored == null) {
            throw new NullPointerException("StoredObject is not found.");
        } else {
            this.stored = stored;
        }
    }

    public void setProcedure(String procedureName) {
        this.setStatement(2, procedureName);
    }

    public void setFunction(String functionName) {
        this.setStatement(1, functionName);
    }

    public void setQuery(String query) {
        this.setStatement(3, query);
    }

    public void setFunctionResult(Parameter parameter) {
        this.checkStatementIsDefined();
        parameter.setDirection(Direction.OUT);
        this.functionResult = parameter;
    }

    public void addParameter(Parameter parameter) {
        this.checkStatementIsDefined();
        this.parameters.addElement(parameter);
    }

    public void addJhashEncryptionValue(Parameter parameter) {
        this.checkStatementIsDefined();
        this.jHashEncryptionValues.addElement(parameter);
    }

    public void setAllParameter(String name, Hashtable value) {
        this.checkStatementIsDefined();
        this.hashParameterName = name;
        this.hashParameterValue = value;
    }

    public void setJson(PsbJson value) {
        this.checkStatementIsDefined();
        this.psbJsonParameterValue = value;
    }

    // setJson — param nomi bilan
    public void setJson(String paramName, PsbJson value) {
        this.checkStatementIsDefined();
        this.psbJsonParamName = paramName;
        this.psbJsonParameterValue = value;
    }

    // exist tekshiruvi
    private boolean existPspJsonParameter() {
        return this.psbJsonParameterValue != null;
    }


    public String getStringResult() {
        return (String) this.functionResult.getValue();
    }

    public String[] getArrayResult() {
        return (String[]) this.functionResult.getValue();
    }

    public ResultSet getCursorResult() {
        return (ResultSet) this.functionResult.getValue();
    }

    private Parameter getOutParameter(String name) {
        for (int i = 0; i < this.parameters.size(); ++i) {
            Parameter parameter = (Parameter) this.parameters.elementAt(i);
            if (parameter.isOutDirection() && parameter.equalName(name)) {
                return parameter;
            }
        }

        throw new IllegalArgumentException(name + " parameter is not found.");
    }

    public String getString(String name) {
        Parameter parameter = this.getOutParameter(name);
        return (String) parameter.getValue();
    }

    public String[] getArray(String name) {
        Parameter parameter = this.getOutParameter(name);
        return (String[]) parameter.getValue();
    }

    public ResultSet getCursor(String name) {
        Parameter parameter = this.getOutParameter(name);
        return (ResultSet) parameter.getValue();
    }

    public void execute() throws Exception {
        this.checkStatementIsDefined();
        OracleCallableStatement cs = null;

        try {
            QueryHelper queryHelper = new QueryHelper();
            queryHelper.initialize();
            String query = queryHelper.queryBuf.toString();
            if (this.stored.isDebug()) {
                System.out.println(queryHelper.values);
            }

            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(query);
            Vector values = queryHelper.values;
            this.bindValues(cs, values);
            cs.execute();
            this.retrieveValues(cs, values);
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

    }

    public List<Map<String, Object>> executeQuery() throws Exception {
        this.checkQueryStatementIsDefined();
        OracleCallableStatement cs = null;
        List<Map<String, Object>> list = new ArrayList();

        Map<String, Object> result;
        try {
            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(this.statementName);
            this.bindValues(cs, this.parameters);
            ResultSet rs = cs.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            while (rs.next()) {
                result = new HashMap();

                for (int i = 1; i <= meta.getColumnCount(); ++i) {
                    String key = meta.getColumnName(i);
                    Object value = rs.getObject(key);
                    result.put(key, value);
                }

                list.add(result);
            }

            result = (Map<String, Object>) list;
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return (List<Map<String, Object>>) result;
    }

    public List<Map<String, Object>> executeQuery(String query, Object... args) throws SQLException {
        OracleCallableStatement cs = null;
        List<Map<String, Object>> list = new ArrayList();

        Map<String, Object> result;
        try {
            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(query);
            this.bindArgs(cs, args);
            ResultSet rs = cs.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            while (rs.next()) {
                result = new HashMap();

                for (int i = 1; i <= meta.getColumnCount(); ++i) {
                    String key = meta.getColumnName(i);
                    Object value = rs.getObject(key);
                    result.put(key, value);
                }

                list.add(result);
            }

            result = (Map<String, Object>) list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return (List<Map<String, Object>>) result;
    }

    public <T> T executeQueryForObject(Class<T> requiredType) throws Exception {
        this.checkQueryStatementIsDefined();
        OracleCallableStatement cs = null;

        Object var5;
        try {
            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(this.statementName);
            this.bindValues(cs, this.parameters);
            ResultSet rs = cs.executeQuery();
            rs.next();
            if (!requiredType.isArray()) {
                Object var9 = rs.getObject(1, requiredType);
                return (T) var9;
            }

            Array array = rs.getArray(1);
            if (array != null) {
                var5 = array.getArray();
                return (T) var5;
            }

            var5 = null;
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return (T) var5;
    }

    public <T> T executeQueryForObject(String query, Class<T> requiredType, Object... args) throws Exception {
        this.checkQueryStatementIsDefined();
        OracleCallableStatement cs = null;

        Object var7;
        try {
            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(query);
            this.bindArgs(cs, args);
            ResultSet rs = cs.executeQuery();
            rs.next();
            if (!requiredType.isArray()) {
                Object var11 = rs.getObject(1, requiredType);
                return (T) var11;
            }

            Array array = rs.getArray(1);
            if (array != null) {
                var7 = array.getArray();
                return (T) var7;
            }

            var7 = null;
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return (T) var7;
    }

    public Map<String, Object> executeQueryForMap() throws Exception {
        this.checkQueryStatementIsDefined();
        OracleCallableStatement cs = null;

        Object var11;
        try {
            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(this.statementName);
            this.bindValues(cs, this.parameters);
            ResultSet rs = cs.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            Map<String, Object> result = new HashMap();
            rs.next();

            for (int i = 1; i <= meta.getColumnCount(); ++i) {
                String key = meta.getColumnName(i);
                Object value = rs.getObject(key);
                result.put(key, value);
            }

            var11 = result;
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return (Map<String, Object>) var11;
    }

    public Map<String, Object> executeQueryForMap(String query, Object... args) throws Exception {
        this.checkQueryStatementIsDefined();
        OracleCallableStatement cs = null;

        Object var13;
        try {
            cs = (OracleCallableStatement) this.stored.getConnection().prepareCall(query);
            this.bindArgs(cs, args);
            ResultSet rs = cs.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            Map<String, Object> result = new HashMap();
            rs.next();

            for (int i = 1; i <= meta.getColumnCount(); ++i) {
                String key = meta.getColumnName(i);
                Object value = rs.getObject(key);
                result.put(key, value);
            }

            var13 = result;
        } finally {
            if (cs != null) {
                cs.close();
            }

        }

        return (Map<String, Object>) var13;
    }

    private void bindValues(OracleCallableStatement cs, Vector values) throws Exception {

        for (int i = 0; i < values.size(); ++i) {
            Object value = values.elementAt(i);
            if (value instanceof String) {
                cs.setString(i + 1, (String) value);
            } else if (value instanceof String[]) {
                cs.setARRAY(i + 1, this.stored.convertToARRAY((String[]) value));
            } else if (value instanceof Parameter) {
                ((Parameter) value).bindValue(this.stored, cs, i + 1);
            } else if (value instanceof PsbJson) {
                String jsonStr = ((PsbJson) value).toJsonString();
                System.out.println(jsonStr);
                Clob clob = this.stored.getConnection().createClob();
                clob.setString(1, jsonStr);
                cs.setClob(i + 1, clob);
            } else {
                throw new RuntimeException("Incorrect value type.");
            }
        }

    }

    private void retrieveValues(OracleCallableStatement cs, Vector values) throws SQLException {
        for (int i = 0; i < values.size(); ++i) {
            Object value = values.elementAt(i);
            if (value instanceof Parameter) {
                ((Parameter) value).retrieveValue(this.stored, cs, i + 1);
            }
        }

    }

    private boolean existHashParameter() {
        return this.hashParameterName != null;
    }

    private boolean existPsbJsonParameter() {
        return this.psbJsonParameterValue != null;
    }

    private void bindArgs(OracleCallableStatement cs, Object... args) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof Date) {
                    Date date = (Date) args[i];
                    cs.setDate(i + 1, new java.sql.Date(date.getTime()));
                } else {
                    cs.setObject(i + 1, args[i]);
                }
            }
        }

    }

    private class QueryHelper {
        private StringBuffer queryBuf = new StringBuffer();
        Vector values = new Vector();

        QueryHelper() {
        }

        void initialize() {
            this.queryBuf.append(" Begin Sql_Util.Set_User; ");
            if (StatementExecuter.this.existHashParameter()) {
                this.collectHashParameter();
            }

            if (!StatementExecuter.this.jHashEncryptionValues.isEmpty()) {
                this.setJhashEncryptionValues();
            }

            this.createBodyBlock();
            this.queryBuf.append(" Sql_Util.Check_User; commit; exception when others then Sql_Util.Check_User; raise;");
            this.queryBuf.append(" end;");


//            if (StatementExecuter.this.existPsbJsonParameter()) {
//
//                this.queryBuf.append("{ ");
//                this.addBodyBlock();
//            } else {
//                this.queryBuf.append(" Begin Sql_Util.Set_User; ");
//                if (StatementExecuter.this.existHashParameter()) {
//                    this.collectHashParameter();
//                }
//
//                if (!StatementExecuter.this.jHashEncryptionValues.isEmpty()) {
//                    this.setJhashEncryptionValues();
//                }
//
//                this.createBodyBlock();
//                this.queryBuf.append(" Sql_Util.Check_User; commit; exception when others then Sql_Util.Check_User; raise;");
//                this.queryBuf.append(" end;");
//            }

        }

        void addBodyBlock() {
            if (StatementExecuter.this.statementType == 1) {
                if (StatementExecuter.this.functionResult == null) {
                    StatementExecuter.this.functionResult = new Varchar2Parameter();
                    StatementExecuter.this.functionResult.setDirection(Direction.OUT);
                    StatementExecuter.this.functionResult.setName("result");
                }

                this.queryBuf.append("? = ");
                this.values.addElement(StatementExecuter.this.functionResult);
            }

            this.addStatement();
        }

        void addStatement() {
            this.queryBuf.append("call ");
            this.queryBuf.append(StatementExecuter.this.statementName);
            this.queryBuf.append("(");
            int len = StatementExecuter.this.parameters.size();

            for (int i = 0; i < len; ++i) {
                Parameter parameter = (Parameter) StatementExecuter.this.parameters.get(i);
                this.values.addElement(parameter);
                this.queryBuf.append("?");
                if (i + 1 != len) {
                    this.queryBuf.append(", ");
                }
            }

            if (StatementExecuter.this.existPsbJsonParameter()) {
                if (len > 0) {
                    this.queryBuf.append(", ");
                }

                this.queryBuf.append("?");
                this.values.addElement(StatementExecuter.this.psbJsonParameterValue);
            }

            this.queryBuf.append(")}");
        }

        void collectHashParameter() {
            this.queryBuf.insert(0, "DECLARE v hashtable:=hashtable(); ");
            Enumeration e = StatementExecuter.this.hashParameterValue.keys();

            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                this.queryBuf.append("v.put(?,?);");
                this.values.addElement(key);
                String[] v = (String[]) StatementExecuter.this.hashParameterValue.get(key);
                if (v.length == 1) {
                    this.values.addElement(v[0]);
                } else {
                    this.values.addElement(v);
                }
            }

        }

        void createBodyBlock() {
            if (StatementExecuter.this.statementType == 1) {
                if (StatementExecuter.this.functionResult == null) {
                    StatementExecuter.this.functionResult = new Varchar2Parameter();
                    StatementExecuter.this.functionResult.setDirection(Direction.OUT);
                }

                this.queryBuf.append("?:=");
                this.values.addElement(StatementExecuter.this.functionResult);
            }

            this.createStatement();
        }

        void createStatement() {
            this.queryBuf.append(StatementExecuter.this.statementName);
            this.queryBuf.append("(");
            int len = StatementExecuter.this.parameters.size();

            for (int i = 0; i < len; ++i) {
                this.putParameter((Parameter) StatementExecuter.this.parameters.elementAt(i));
                if (i + 1 != len) {
                    this.queryBuf.append(",");
                }
            }

            if (StatementExecuter.this.existHashParameter()) {
                if (len > 0) {
                    this.queryBuf.append(",");
                }

                this.queryBuf.append(StatementExecuter.this.hashParameterName);
                this.queryBuf.append("=>v");
            }

            if (StatementExecuter.this.existPsbJsonParameter()) {
                if (len > 0 || StatementExecuter.this.existHashParameter()) {
                    this.queryBuf.append(",");
                }
                // Param nomi berilgan bo'lsa ishlatish, bo'lmasa p_json
                String paramName = StatementExecuter.this.psbJsonParamName != null
                        ? StatementExecuter.this.psbJsonParamName
                        : "i_json";
                this.queryBuf.append(paramName);
                this.queryBuf.append("=>?");
                this.values.addElement(StatementExecuter.this.psbJsonParameterValue);
            }

            this.queryBuf.append(");");
        }

        void putParameter(Parameter parameter) {
            this.queryBuf.append(parameter.getName());
            this.queryBuf.append("=>?");
            this.values.addElement(parameter);
        }

        void setJhashEncryptionValues() {
            this.queryBuf.append("J_HASH_UTIL.Set_Values(i_param_names=>?,i_param_entity_names=>?); ");

            for (int i = 0; i < StatementExecuter.this.jHashEncryptionValues.size(); ++i) {
                this.values.addElement(StatementExecuter.this.jHashEncryptionValues.elementAt(i));
            }

        }
    }
}
