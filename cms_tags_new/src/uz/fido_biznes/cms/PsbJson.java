package uz.fido_biznes.cms;

import java.util.*;

public class PsbJson {

    private String rawJson;
    private Map<String, Object> data;

    // 1. Bo'sh — putString bilan to'ldirish uchun
    public PsbJson() {
        this.data = new LinkedHashMap<>();
    }

    // 2. Raw JSON string bilan
    public PsbJson(String rawJson) {
        this.rawJson = rawJson;
    }

    // Map ko'rinishidagi stringdan yasash
    public static PsbJson fromMapString(String mapStr) {
        String json = convertToJson(mapStr.trim());
        return new PsbJson(json);
    }

    // char[] bilan — request parametrlardan
    public void putString(String key, char[] value) {
        putNested(this.data, key, new String(value));
    }

    // String bilan
    public void putString(String key, String value) {
        putNested(this.data, key, value);
    }

    // String[] bilan — array uchun
    public void putString(String key, String[] value) {
        if (value.length > 1) {
            List<Object> list = new ArrayList<>();
            for (String v : value) list.add(v);
            putNested(this.data, key, list);
        } else {
            putNested(this.data, key, value[0]);
        }
    }

    // Number
    public void put(String key, Number value) {
        putNested(this.data, key, value);
    }

    // Boolean
    public void put(String key, Boolean value) {
        putNested(this.data, key, value);
    }

    // Nested key ni Map ga joylashtirish
    private void putNested(Map<String, Object> map, String key, Object value) {
        String[] parts = key.split("\\.");

        if (parts.length == 1) {
            String cleanKey = parts[0].replaceAll("\\[\\d+\\]", "");
            if (parts[0].contains("[")) {
                List<Object> list = (List<Object>) map.getOrDefault(cleanKey, new ArrayList<>());
                list.add(value);
                map.put(cleanKey, list);
            } else {
                map.put(cleanKey, value);
            }
        } else {
            String currentKey = parts[0].replaceAll("\\[\\d+\\]", "");
            if (!map.containsKey(currentKey)) {
                map.put(currentKey, new LinkedHashMap<>());
            }
            Map<String, Object> child = (Map<String, Object>) map.get(currentKey);
            String remainingKey = key.substring(key.indexOf(".") + 1);
            putNested(child, remainingKey, value);
        }
    }

    // Oracle ga yuborish uchun
    public String toJsonString() {
        if (this.rawJson != null) {
            return this.rawJson;
        }
        return objToJson(this.data);
    }

    // Rekursiv JSON yasash
    private String objToJson(Object obj) {
        if (obj instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) obj).entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(objToJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();

        } else if (obj instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : (List<?>) obj) {
                if (!first) sb.append(",");
                sb.append(objToJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();

        } else {
            String val = obj.toString();
            if (val.matches("-?\\d+(\\.\\d+)?")) return val;
            if (val.equals("true") || val.equals("false")) return val;
            if (val.equals("null")) return "null";
            return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    }

    // Map ko'rinishini to'g'ri JSON ga o'girish
    private static String convertToJson(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ':') {
                sb.append(c);
                i++;
            } else if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                i++;
            } else if (c == '"' || c == '\'') {
                char quote = c;
                StringBuilder token = new StringBuilder();
                i++;
                while (i < input.length() && input.charAt(i) != quote) {
                    token.append(input.charAt(i));
                    i++;
                }
                i++;
                sb.append('"').append(token).append('"');
            } else {
                StringBuilder token = new StringBuilder();
                while (i < input.length()
                        && input.charAt(i) != ':'
                        && input.charAt(i) != ','
                        && input.charAt(i) != '}'
                        && input.charAt(i) != ']'
                        && input.charAt(i) != ' '
                        && input.charAt(i) != '\n') {
                    token.append(input.charAt(i));
                    i++;
                }
                String tok = token.toString().trim();
                if (tok.matches("-?\\d+(\\.\\d+)?")
                        || tok.equals("true")
                        || tok.equals("false")
                        || tok.equals("null")) {
                    sb.append(tok);
                } else {
                    sb.append('"').append(tok).append('"');
                }
            }
        }

        return sb.toString();
    }

    // Map ko'rinishimi tekshirish
    public static boolean isMapLike(String str) {
        return str != null
                && str.trim().startsWith("{")
                && str.matches(".*\\{\\s*[^\"']\\w+\\s*:.*");
    }
}
