package utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonUtils {

    /**
     * Convert map to JSONObject
     * @param map map
     * @return jsonDataObject
     * @throws JSONException jsonException
     */
    public static JSONObject toJson(Map<String, Object> map) throws JSONException {
        JSONObject jsonData = new JSONObject();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map<?, ?>) {
                value = toJson((Map<String, Object>) value);
            }
            jsonData.put(key, value);
        }
        return jsonData;
    }
}
