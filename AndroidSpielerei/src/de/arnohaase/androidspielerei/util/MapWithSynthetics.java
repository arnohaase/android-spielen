package de.arnohaase.androidspielerei.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This is a special Map implementation that adds 'synthetic' values, i.e. one can register expressions
 *  for a given key that are evaluated whenever <code>get()</code> is called with this key. 
 * 
 * @author arno
 */
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
	        result.add(new Map.Entry<K, V> () { //TODO create named inner class
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
}
