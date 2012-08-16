package de.arnohaase.androidspielerei.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * This class decodes a JSON string into Java primitives, i.e. java.util.List and java.util.Map
 * 
 * @author arno
 */
public class JsonDecoder {
    public Object decode(String json) throws JSONException {
        return decodeInternal(new JSONTokener(json).nextValue());
    }

    private Object decodeInternal(Object o) throws JSONException {
        if (o instanceof JSONObject) {
            return decodeJsonObject((JSONObject) o);
        }
        if (o instanceof JSONArray) {
            return decodeJsonArray((JSONArray) o);
        }
        return o;
    }


    private Map<String, Object> decodeJsonObject(JSONObject o) throws JSONException {
        final Map<String, Object> result = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        final Iterator<String> iter = o.keys();
        while (iter.hasNext()) {
            final String key = iter.next();
            result.put(key, decodeInternal(o.get(key)));
        }
        return result;
    }
    
    private List<Object> decodeJsonArray(JSONArray o) throws JSONException {
        final List<Object> result = new ArrayList<Object>();
        for (int i=0; i<o.length(); i++) {
            result.add(decodeInternal(o.get(i)));
        }
        return result;
    }
    
}
