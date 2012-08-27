package de.arnohaase.androidspielerei.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * This class compares maps based on one or more configurable keys, breaking ambiguity based
 *  on System.identityHashCode().<br>
 *  
 * It assumes that the Maps' values are Comparable - at least for the configured keys.
 * 
 * @author arno
 */
public class ByColumnComparator implements Comparator<Map<String, Object>> {
    private final List<String> keys;

    public ByColumnComparator(String... keys) {
        this(Arrays.asList(keys));
    }
    
    public ByColumnComparator(List<String> keys) {
        this.keys = keys;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int comparePartial(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return ((Comparable) o1).compareTo(o2);
    }
    
    public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
        for (String key: keys) {
            final int partialResult = comparePartial(lhs.get(key), rhs.get(key));
            if (partialResult != 0) {
                return partialResult;
            }
        }
        return System.identityHashCode(lhs) - System.identityHashCode(rhs);
    }
}
