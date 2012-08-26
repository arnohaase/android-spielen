package de.arnohaase.androidspielerei.util;

import java.util.Map;


/**
 * This class can extract values from a string-keyed map. If the key contains expressions like '${...}',
 *  it treats the key as a template expression, replacing the '${...}' with the values of the respective
 *  keys and merging them into single string.<br>
 *  
 * Otherwise, it returns the raw value for the key - *without* any type conversion. So for example
 *  get("${oid}") returns a string representation of the oid, while 
 *  get("oid") returns the oid as a long (or whatever data type is used to store it in the map).
 * 
 * @author arno
 */
public class MapSyntheticValueExtractor {
    private final Map<String, Object> map;

    public MapSyntheticValueExtractor(Map<String, Object> map) {
        this.map = map;
    }

    public String getAsString(String keyOrExpression) {
        return String.valueOf(get(keyOrExpression));
    }
    
    public Object get(String keyOrExpression) {
        if (keyOrExpression.contains("${")) {
            return doGetByExpression(keyOrExpression);
        }
        else {
            return map.get(keyOrExpression);
        }
    }

    private Object doGetByExpression(String expr) {
        final StringBuilder result = new StringBuilder();
        
        int indStart;
        int indEnd = 0;
        while((indStart = expr.indexOf("${", indEnd)) != -1) {
            result.append(expr.substring(indEnd, indStart));
            indEnd = expr.indexOf('}', indStart);
            
            if (indEnd == -1) {
                throw new IllegalArgumentException("invalid expression '" + expr + "': no matching '}' for '${' at " + indStart);
            }
            result.append(map.get(expr.substring(indStart + 2, indEnd)));
            indEnd++;
        }
        result.append(expr.substring(indEnd));
        
        return result.toString();
    }
}


