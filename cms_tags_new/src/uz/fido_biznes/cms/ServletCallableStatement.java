package uz.fido_biznes.cms;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
//import uz.fido_biznes.objects.FbJson;
//import uz.fb.parser.FbJsonParser;
import uz.fido_biznes.sql.*;

public class ServletCallableStatement {
    private ServletRequest request;
    private StatementExecuter se;
    public final Direction IN_OUT;
    private StoredObject stored;
    private Map<String, String> encryptedInParams;
    /***Added for Dynamic Grid*****/
    private Map<String, String> encryptedInParamUsers;
    private Map<String, String> encryptedInParamFields;
    private Map<String, String> encryptedOutParams;

    public ServletCallableStatement(StoredObject stored) {
        this.IN_OUT = Direction.IN_OUT;
        this.encryptedInParams = new HashMap();
        /***Added for Dynamic Grid*****/
        this.encryptedInParamUsers = new HashMap();
        this.encryptedInParamFields = new HashMap();
        this.encryptedOutParams = new HashMap();
        this.se = new StatementExecuter();
        this.se.setStored(stored);
        this.stored = stored;
    }

    public ServletCallableStatement(StoredObject stored, ServletRequest request) {
        this.IN_OUT = Direction.IN_OUT;
        this.encryptedInParams = new HashMap();
        /***Added for Dynamic Grid*****/
        this.encryptedInParamUsers = new HashMap();
        this.encryptedInParamFields = new HashMap();
        this.encryptedOutParams = new HashMap();
        this.se = new StatementExecuter();
        this.se.setStored(stored);
        this.request = request;
        this.stored = stored;
    }

    public void setProcedure(String procedureName) {
        this.se.setProcedure(procedureName);
    }

    public void setFunction(String functionName) {
        this.se.setFunction(functionName);
    }

    public void setQuery(String query) {
        this.se.setQuery(query);
    }

    public void registerStringResult() {
        this.se.setFunctionResult(new Varchar2Parameter());
    }

    public void registerNumberResult() {
        this.se.setFunctionResult(new NumberParameter());
    }

    public void registerDateResult() {
        this.se.setFunctionResult(new DateParameter());
    }

    public void registerArrayStringResult() {
        this.se.setFunctionResult(new ArrayVarchar2Parameter());
    }

    public void registerArrayNumberResult() {
        this.se.setFunctionResult(new ArrayNumberParameter());
    }

    public void registerArrayDateResult() {
        this.se.setFunctionResult(new ArrayDateParameter());
    }

    private void registerCursorResult() {
        this.se.setFunctionResult(new CursorParameter());
    }

    public void setString(String name, String value, Direction direction) {
        this.se.addParameter(new Varchar2Parameter(name, Util.desanitize(value), direction));
    }

    public void setString(String name, String value) {
        this.setString(name, value, Direction.IN);
    }

    public void setRaw(String name, byte[] value, Direction direction) {
        this.se.addParameter(new RawParameter(name, value, direction));
    }

    public void setRaw(String name, byte[] value) {
        this.setRaw(name, value, Direction.IN);
    }

    public void registerString(String name) {
        this.setString(name, (String) null, Direction.OUT);
    }

    public void setStringParameter(String name, String parameterName, Direction direction) throws Exception {
        String value = Util.encodeISO(this.request.getParameter(parameterName));
        if (this.encryptedInParams.containsKey(parameterName)) {
            /***Added for Dynamic Grid*****/
            this.setString(name, this.decryptParameter(parameterName, value), direction);
        } else {
            this.setString(name, value, direction);
        }

    }

    public void setStringParameter(String name, String parameterName) throws Exception {
        this.setStringParameter(name, parameterName, Direction.IN);
    }

    public void setNumber(String name, String value, Direction direction) {
        this.se.addParameter(new NumberParameter(name, Util.desanitize(value), direction));
    }

    public void setNumber(String name, String value) {
        this.setNumber(name, value, Direction.IN);
    }

    public void registerNumber(String name) {
        this.setNumber(name, (String) null, Direction.OUT);
    }

    public void setNumberParameter(String name, String parameterName, Direction direction) throws Exception {
        String value = this.request.getParameter(parameterName);
        if (this.encryptedInParams.containsKey(parameterName)) {
            /***Added for Dynamic Grid*****/
            this.setNumber(name, this.decryptParameter(parameterName, value), direction);
        } else {
            this.setNumber(name, value, direction);
        }

    }

    public void setNumberParameter(String name, String parameterName) throws Exception {
        this.setNumberParameter(name, parameterName, Direction.IN);
    }

    public void setDate(String name, String value, String format, Direction direction) {
        this.se.addParameter(new DateParameter(name, Util.desanitize(value), direction, format));
    }

    public void setDate(String name, String value, String format) {
        this.setDate(name, value, format, Direction.IN);
    }

    public void setDate(String name, String value, Direction direction) {
        this.setDate(name, value, (String) null, direction);
    }

    public void setDate(String name, String value) {
        this.setDate(name, value, (String) null, Direction.IN);
    }

    public void registerDate(String name, String format) {
        this.setDate(name, (String) null, format, Direction.OUT);
    }

    public void registerDate(String name) {
        this.setDate(name, (String) null, (String) null, Direction.OUT);
    }

    public void setDateParameter(String name, String parameterName, String format, Direction direction) throws Exception {
        String value = this.request.getParameter(parameterName);
        if (this.encryptedInParams.containsKey(parameterName)) {
            /***Added for Dynamic Grid*****/
            this.setDate(name, this.decryptParameter(parameterName, value), format, direction);
        } else {
            this.setDate(name, value, format, direction);
        }

    }

    public void setDateParameter(String name, String parameterName, String format) throws Exception {
        this.setDateParameter(name, parameterName, format, Direction.IN);
    }

    public void setDateParameter(String name, String parameterName, Direction direction) throws Exception {
        this.setDateParameter(name, parameterName, (String) null, direction);
    }

    public void setDateParameter(String name, String parameterName) throws Exception {
        this.setDateParameter(name, parameterName, (String) null, Direction.IN);
    }

    public void setArrayString(String name, String[] value, Direction direction) {
        this.se.addParameter(new ArrayVarchar2Parameter(name, Util.desanitize(value), direction));
    }

    public void setArrayString(String name, String[] value) {
        this.setArrayString(name, value, Direction.IN);
    }

    public void setArrayString(String name, String[] value, String toEncode) throws UnsupportedEncodingException {
        this.setArrayString(name, Util.encodeISO(value), Direction.IN);
    }

    public void registerArrayString(String name) {
        this.setArrayString(name, (String[]) null, (Direction) Direction.OUT);
    }

    public void setArrayStringParameter(String name, String parameterName, Direction direction) throws Exception {
        String[] values = Util.encodeISO(this.request.getParameterValues(parameterName));
        if (this.encryptedInParams.containsKey(parameterName)) {
            /***Added for Dynamic Grid*****/
            this.setArrayString(name, this.decryptParameterValues(parameterName, values), direction);
        } else {
            this.setArrayString(name, values, direction);
        }

    }

    public void setArrayStringParameter(String name, String parameterName) throws Exception {
        this.setArrayStringParameter(name, parameterName, Direction.IN);
    }

    public void setArrayNumber(String name, String[] value, Direction direction) {
        this.se.addParameter(new ArrayNumberParameter(name, Util.desanitize(value), direction));
    }

    public void setArrayNumber(String name, String[] value) {
        this.setArrayNumber(name, value, Direction.IN);
    }

    public void registerArrayNumber(String name) {
        this.setArrayNumber(name, (String[]) null, Direction.OUT);
    }

    public void setArrayNumberParameter(String name, String parameterName, Direction direction) throws Exception {
        String[] value = this.request.getParameterValues(parameterName);
        if (this.encryptedInParams.containsKey(parameterName)) {
            /***Added for Dynamic Grid*****/
            this.setArrayNumber(name, this.decryptParameterValues(parameterName, value), direction);
        } else {
            this.setArrayNumber(name, value, direction);
        }

    }

    public void setArrayNumberParameter(String name, String parameterName) throws Exception {
        this.setArrayNumberParameter(name, parameterName, Direction.IN);
    }

    public void setArrayDate(String name, String[] value, String format, Direction direction) {
        this.se.addParameter(new ArrayDateParameter(name, Util.desanitize(value), direction, format));
    }

    public void setArrayDate(String name, String[] value, String format) {
        this.setArrayDate(name, value, format, Direction.IN);
    }

    public void setArrayDate(String name, String[] value, Direction direction) {
        this.setArrayDate(name, value, (String) null, direction);
    }

    public void setArrayDate(String name, String[] value) {
        this.setArrayDate(name, value, (String) null, Direction.IN);
    }

    public void registerArrayDate(String name, String format) {
        this.setArrayDate(name, (String[]) null, format, Direction.OUT);
    }

    public void registerArrayDate(String name) {
        this.setArrayDate(name, (String[]) null, (String) null, Direction.OUT);
    }

    public void setArrayDateParameter(String name, String parameterName, String format, Direction direction) throws Exception {
        String[] value = this.request.getParameterValues(parameterName);
        if (this.encryptedInParams.containsKey(parameterName)) {
            /***Added for Dynamic Grid*****/
            this.setArrayDate(name, this.decryptParameterValues(parameterName, value), format, direction);
        } else {
            this.setArrayDate(name, value, format, direction);
        }

    }

    public void setArrayDateParameter(String name, String parameterName, String format) throws Exception {
        this.setArrayDateParameter(name, parameterName, format, Direction.IN);
    }

    public void setArrayDateParameter(String name, String parameterName, Direction direction) throws Exception {
        this.setArrayDateParameter(name, parameterName, (String) null, direction);
    }

    public void setArrayDateParameter(String name, String parameterName) throws Exception {
        this.setDateParameter(name, parameterName, (String) null, Direction.IN);
    }

    private void registerCursor(String name) {
        this.se.addParameter(new CursorParameter(name));
    }

    public void setEncryptedParameters(Map<String, String> parametersWithEntity) {
        this.encryptedInParams.putAll(parametersWithEntity);
    }

    public void setEncryptedParameter(String parameterName, String entityName) {
        this.encryptedInParams.put(parameterName, entityName);
    }

    /***Added for Dynamic Grid*****/
    public void setEncryptedParameter(String parameterName, String entityName, String fieldName) {
        this.setEncryptedParameter(parameterName, entityName, this.getSecureUserCode(), fieldName);
    }

    public void setEncryptedParameter(String parameterName, String entityName, String user, String fieldName) {
        this.encryptedInParams.put(parameterName, entityName);
        this.encryptedInParamUsers.put(parameterName, user);
        this.encryptedInParamFields.put(parameterName, fieldName);
    }

    public void setAllParameters(String name) throws Exception {
        Hashtable values = new Hashtable();
        Enumeration e = this.request.getParameterNames();

        while (e.hasMoreElements()) {
            String parameterName = (String) e.nextElement();
            String[] value = Util.encodeISO(this.request.getParameterValues(parameterName));
            if (this.encryptedInParams.containsKey(parameterName)) {
                /***Added for Dynamic Grid*****/
                values.put(parameterName, Util.desanitize(this.decryptParameterValues(parameterName, value)));
            } else {
                values.put(parameterName, Util.desanitize(value));
            }
        }

        this.se.setAllParameter(name, values);
    }


    // 1. Request parametrlardan JSON — param nomi bilan
    public void setJsonName(String name) throws Exception {
        PsbJson values = new PsbJson();
        Enumeration e = this.request.getParameterNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String[] value = Util.encodeISO(this.request.getParameterValues(key));

            if (this.encryptedInParams.containsKey(key)) {
                values.putString(key, Util.desanitize(
                        /***Added for Dynamic Grid*****/
                        this.decryptParameterValues(key, value)
                ));
            } else {
                values.putString(key, Util.desanitize(value));
            }
        }

        this.se.setJson(name, values);
    }

    // 2. Request parametrlardan JSON — param nomisiz
    public void setJson() throws Exception {
        PsbJson values = new PsbJson();
        Enumeration e = this.request.getParameterNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String[] value = Util.encodeISO(this.request.getParameterValues(key));

            if (this.encryptedInParams.containsKey(key)) {
                values.putString(key, Util.desanitize(
                        /***Added for Dynamic Grid*****/
                        this.decryptParameterValues(key, value)
                ));
            } else {
                values.putString(key, Util.desanitize(value));
            }
        }

        this.se.setJson(values);
    }

    // 3. String JSON bilan
    public void setJson(String name, String json) throws Exception {
        String prepared = Util.desanitize(Util.encodeISO(json));
        PsbJson fbJson = PsbJson.isMapLike(prepared)
                ? PsbJson.fromMapString(prepared)
                : new PsbJson(prepared);
        this.se.setJson(name, fbJson);
    }

    // 4. String JSON — param nomisiz
    public void setJson(String json) throws Exception {
        String prepared = Util.desanitize(Util.encodeISO(json));
        PsbJson fbJson = PsbJson.isMapLike(prepared)
                ? PsbJson.fromMapString(prepared)
                : new PsbJson(prepared);
        this.se.setJson(fbJson);
    }

    // 5. Encoding bilan
    public void setJson(String name, String json, String toEncode) throws Exception {
        String prepared = Util.desanitize(Util.encodeISO(json, toEncode));
        PsbJson fbJson = PsbJson.isMapLike(prepared)
                ? PsbJson.fromMapString(prepared)
                : new PsbJson(prepared);
        this.se.setJson(name, fbJson);
    }

    // 6. Request body dan raw JSON
    public void setJsonReady(String name) throws Exception {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader = this.request.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        this.se.setJson(name, new PsbJson(sb.toString()));
    }


    public void setJsonEncode(String toEncode) throws Exception {
        PsbJson values = new PsbJson();
        Enumeration e = this.request.getParameterNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = Util.encodeISO(this.request.getParameter(key), toEncode);

            if (this.encryptedInParams.containsKey(key)) {
                values.putString(key, Util.desanitize(
                        /***Added for Dynamic Grid*****/
                        decryptParameter(key, value)
                ).toCharArray());
            } else {
                values.putString(key, Util.desanitize(value).toCharArray());
            }
        }

        this.se.setJson(values);
    }


    public void setAllParameters(String name, String toEncode) throws Exception {
        Hashtable values = new Hashtable();
        Enumeration e = this.request.getParameterNames();

        while (e.hasMoreElements()) {
            String parameterName = (String) e.nextElement();
            String[] value = Util.encodeISO(this.request.getParameterValues(parameterName), toEncode);
            if (this.encryptedInParams.containsKey(parameterName)) {
                /***Added for Dynamic Grid*****/
                values.put(parameterName, Util.desanitize(this.decryptParameterValues(parameterName, value)));
            } else {
                values.put(parameterName, Util.desanitize(value));
            }
        }

        this.se.setAllParameter(name, values);
    }

    public void setAllParameters(String name, Hashtable values) {
        this.se.setAllParameter(name, values);
    }

    public void setAllParameters(String name, String[] keys, String[] values) {
        if (keys != null && values != null) {
            if (keys.length != values.length) {
                throw new StringIndexOutOfBoundsException("keys.length!=values.length");
            } else {
                Hashtable hashtable = new Hashtable();

                for (int i = 0; i < keys.length; ++i) {
                    hashtable.put(keys[i], Util.desanitize(new String[]{Util.nvl(values[i], "")}));
                }

                this.se.setAllParameter(name, hashtable);
            }
        } else {
            throw new NullPointerException("keys==null or values==null");
        }
    }

    public void execute() throws Exception {
        if (!this.encryptedOutParams.isEmpty()) {
            String[] keys = (String[]) this.encryptedOutParams.keySet().toArray(new String[0]);
            String[] entityNames = (String[]) this.encryptedOutParams.values().toArray(new String[0]);
            this.se.addJhashEncryptionValue(new ArrayVarchar2Parameter("i_param_names", keys, Direction.IN));
            this.se.addJhashEncryptionValue(new ArrayVarchar2Parameter("i_param_entity_names", entityNames, Direction.IN));
        }

        this.se.execute();
    }

    public List<Map<String, Object>> executeQuery() throws Exception {
        return this.se.executeQuery();
    }

    public List<Map<String, Object>> executeQuery(String query, Object... args) throws Exception {
        return this.se.executeQuery(query, args);
    }

    public List<Map<String, Object>> executeQueryWithParameters(String query, String... paramNames) throws Exception {
        this.checkRequestIsDefined();
        Object[] paramValues = this.getRequestValues(paramNames);
        return this.se.executeQuery(query, paramValues);
    }

    public <T> T executeQueryForObject(Class<T> requiredType) throws Exception {
        return (T) this.se.executeQueryForObject(requiredType);
    }

    public <T> T executeQueryForObject(String query, Class<T> requiredType, Object... args) throws Exception {
        return (T) this.se.executeQueryForObject(query, requiredType, args);
    }

    public Map<String, Object> executeQueryForMap() throws Exception {
        return this.se.executeQueryForMap();
    }

    public Map<String, Object> executeQueryForMap(String query, Object... args) throws Exception {
        return this.se.executeQueryForMap(query, args);
    }

    public String getStringResult() {
        return this.se.getStringResult();
    }

    public String getString(String name) {
        return this.se.getString(name);
    }

    public String[] getArrayResult() {
        return this.se.getArrayResult();
    }

    public String[] getArray(String name) {
        return this.se.getArray(name);
    }

    public void setOutParameterEncrypted(String key, String entityName) {
        if (key != null && entityName != null) {
            this.encryptedOutParams.put(key, entityName);
        } else {
            throw new NullPointerException("key==null or entityName==null");
        }
    }

    public void setOutParameterEncrypted(HashMap<String, String> encryptedOutParams) {
        if (encryptedOutParams == null) {
            throw new NullPointerException("encryptedOutParams is null");
        } else {
            encryptedOutParams.putAll(encryptedOutParams);
        }
    }

    public void setOutParameterEncrypted(String[] keys, String... entityNames) {
        if (keys != null && entityNames != null) {
            if (entityNames.length == 1 && keys.length > 1) {
                entityNames = (String[]) Collections.nCopies(keys.length, entityNames[0]).toArray(new String[0]);
            }

            if (keys.length != entityNames.length) {
                throw new StringIndexOutOfBoundsException("keys.length!=entityNames.length");
            } else {
                for (int i = 0; i < keys.length; ++i) {
                    this.encryptedOutParams.put(keys[i], entityNames[i]);
                }

            }
        } else {
            throw new NullPointerException("keys==null or entityNames==null");
        }
    }

    private ResultSet getCursorResult() {
        return this.se.getCursorResult();
    }

    private ResultSet getCursor(String name) {
        return this.se.getCursor(name);
    }

    private String decrypt(String value, String entity) throws Exception {
        return this.stored.decryptValue(value, entity);
    }

    private String[] decrypt(String[] values, String entity) throws Exception {
        return this.stored.decryptValues(values, entity);
    }

    /***Added for Dynamic Grid*****/
    private String decryptParameter(String parameterName, String value) throws Exception {
        String entity = this.encryptedInParams.get(parameterName);
        String fieldName = this.encryptedInParamFields.get(parameterName);
        if (fieldName != null && fieldName.trim().length() > 0) {
            return this.stored.decryptValue(value, entity, this.encryptedInParamUsers.get(parameterName), fieldName);
        }
        return this.decrypt(value, entity);
    }

    private String[] decryptParameterValues(String parameterName, String[] values) throws Exception {
        String entity = this.encryptedInParams.get(parameterName);
        String fieldName = this.encryptedInParamFields.get(parameterName);
        if (fieldName != null && fieldName.trim().length() > 0) {
            return this.stored.decryptValues(values, entity, this.encryptedInParamUsers.get(parameterName), fieldName);
        }
        return this.decrypt(values, entity);
    }

    private String getSecureUserCode() {
        if (this.request instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest)this.request).getSession(false);
            if (session != null) {
                Object employee = session.getAttribute("employee");
                if (employee == null) {
                    try {
                        employee = session.getValue("employee");
                    } catch (Exception ignored) {
                    }
                }
                if (employee != null && employee.toString().trim().length() > 0) {
                    return employee.toString();
                }
            }
        }
        return "";
    }

    private Object[] getRequestValues(String[] paramNames) throws Exception {
        for (int i = 0; i < paramNames.length; ++i) {
            if (this.encryptedInParams.containsKey(paramNames[i])) {
                /***Added for Dynamic Grid*****/
                paramNames[i] = this.decryptParameter(paramNames[i], this.request.getParameter(paramNames[i]));
            } else {
                paramNames[i] = this.request.getParameter(paramNames[i]);
            }
        }

        return paramNames;
    }

    private void checkRequestIsDefined() {
        if (this.request == null) {
            throw new IllegalStateException("Request is missing.");
        }
    }
}
