package a;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapWithSynthetics<K,V> implements Map<K,V> {
	private final Map<K, V> inner;
	private final Map<K, Expression<K,V>> synthetics;

	public MapWithSynthetics(Map<K, V> inner, Map<K, Expression<K,V>> synthetics) {
        this.inner = inner;
        this.synthetics = synthetics;
    }

    public void clear() {
		inner.clear();
	}

	public boolean containsKey(Object key) {
	    return inner.containsKey(key) || synthetics.containsKey(key);
	}

	public boolean containsValue(Object value) {
	    if (inner.containsValue(value)) {
	        return true;
	    }
	    for (Expression<K,V> expr: synthetics.values()) {
	        if (value.equals(expr.getValue(inner))) {
	            return true;
	        }
	    }
	    return false;
	}

	public Set<Map.Entry<K, V>> entrySet() {
	    final Set<Map.Entry<K, V>> result = new HashSet<Map.Entry<K, V>>(inner.entrySet ());
	    for (final Map.Entry<K, Expression<K,V>> e: synthetics.entrySet()) {
	        result.add(new Map.Entry<K, V> () {
                public K getKey() {
                    return e.getKey();
                }

                public V getValue() {
                    return e.getValue().getValue(inner);
                }

                public V setValue(V object) {
                    throw new UnsupportedOperationException();
                }
	        });
	    }
	    
	    return result;
	}

	public V get(Object key) {
	    if(inner.containsKey(key)) {
	        return inner.get(key);
	    }
	    if(synthetics.containsKey(key)) {
	        return synthetics.get(key).getValue(inner);
	    }
	    return null;
	}

	public boolean isEmpty() {
		return inner.isEmpty();
	}

	public Set<K> keySet() {
	    final Set<K> result = new HashSet<K>(inner.keySet());
	    result.addAll(synthetics.keySet());
	    return result;
	}

	public V put(K key, V value) {
	    if(synthetics.containsKey(key)) {
	        throw new IllegalArgumentException("key '" + key + "' is synthetic");
	    }
	    return inner.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> arg0) {
	    inner.putAll(arg0);
	}

	public V remove(Object key) {
	    return inner.remove(key);
	}

	public int size() {
	    return inner.size();
	}

	public Collection<V> values() {
	    final List<V> result = new ArrayList<V>(inner.values());
	    for (Expression<K,V> expr: synthetics.values()) {
	        result.add(expr.getValue(inner));
	    }
	    return result;
	}
	
	public interface Expression<K,V> {
	    V getValue(Map<K,V> raw);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    private static final Map<String, MapWithSynthetics.Expression<String, Object>> __synthetics = new HashMap<String, MapWithSynthetics.Expression<String, Object>>();
    
    static {
        __synthetics.put("name", new MapWithSynthetics.Expression<String, Object>() {
            public Object getValue(Map<String, Object> raw) {
                return raw.get("firstname") + " " + raw.get("lastname");
            }
        });
    }

	public static void main(String[] args) {
	    final List<Map<String, Object>> raw = createPersons();
	    final List<Map<String, Object>> withSynthetics = new ArrayList<Map<String,Object>>();
	    for (Map<String, Object> row: raw) {
	        withSynthetics.add(new MapWithSynthetics<String, Object>(row, __synthetics));
	    }
        
	    
	    for (Map<String, Object> m: withSynthetics) {
	        System.out.println(m.get("name"));
	    }
    }
	
	private static List<Map<String, Object>> createPersons() {
	    final List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

	    for (int i=0; i<20; i++) {
	        result.add(createPerson(i));
	    }

	    return result;
	}

	private static Map<String, Object> createPerson(int idx) {
	    final Map<String, Object> result = new HashMap<String, Object>();

	    result.put("oid", idx);
	    result.put("firstname", "first " + idx);
	    result.put("lastname", "last " + idx);

	    result.put("street", "Sesame Street");
	    result.put("no", "" + idx);
	    result.put("city", "Dodge City");

	    return result;
	}

}
