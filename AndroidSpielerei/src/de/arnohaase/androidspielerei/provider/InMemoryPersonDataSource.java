package de.arnohaase.androidspielerei.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import de.arnohaase.androidspielerei.person.PersonConstants;
import de.arnohaase.androidspielerei.util.ByColumnComparator;
import de.arnohaase.androidspielerei.util.MapSyntheticValueExtractor;


public class InMemoryPersonDataSource implements PersonDataSource, PersonConstants {
    private final Map<Long, Map<String, Object>> allPersons = new ConcurrentHashMap<Long, Map<String,Object>>();
    {
        for (int idx=0; idx<20; idx++) {
            final boolean male = idx % 2 == 0; 

            final Map<String, Object> p = new HashMap<String, Object>();
            p.put(COL_OID, (long) idx);
            p.put(COL_FIRSTNAME, male ? "Arno " + idx : "Testa" + idx);
            p.put(COL_LASTNAME,  male ? "Haase" : "Testarossa");
            p.put(COL_SEX,       male ? "m" : "f");
            p.put(COL_ADDR_STREET, "Sesame Street");
            p.put(COL_ADDR_NO, "" + idx);
            p.put(COL_ADDR_ZIP, "12345");
            p.put(COL_ADDR_CITY, "Dodge City");
            p.put(COL_ADDR_COUNTRY, "Germany");

            allPersons.put(Long.valueOf(idx), p); 
        }
    }

    public List<Map<String, Object>> findPersons(String searchFilter, List<String> sortColumns, int limit) {
        final String normalizedSearchText = searchFilter == null ? "" : searchFilter.trim().toLowerCase();
        
        final List<Map<String, Object>> allMatches = new ArrayList<Map<String,Object>>();
        
        for (Map<String, Object> candidate: allPersons.values()) {
            final MapSyntheticValueExtractor extractor = new MapSyntheticValueExtractor(candidate);
            final String name = extractor.getAsString("${" + COL_FIRSTNAME + "} ${" + COL_LASTNAME + "}");
            
            if (String.valueOf(name).toLowerCase().contains(normalizedSearchText)) {
                allMatches.add(candidate);
            }
        }

        Collections.sort(allMatches, new ByColumnComparator(COL_LASTNAME, COL_FIRSTNAME));

        if (allMatches.size() < limit) {
            return allMatches;
        }
        else {
            return allMatches.subList(0, limit);
        }
    }

    public Map<String, Object> findSinglePerson(long oid) {
        return allPersons.get(oid);
    }
    
    public long insertSinglePerson(ContentValues values) {
        final long oid = createNewOid();
        final Map<String, Object> data = new HashMap<String, Object>();
        
        copyIntoMap(values, data);
        data.put(COL_OID, oid);
        allPersons.put(oid, data);
        
        return oid;
    }

    private void copyIntoMap(ContentValues values, Map<String, Object> map) {
        for (String key: values.keySet()) {
            map.put(key, values.get(key));
        }
    }

    private long createNewOid () {
        long max = 0;
        for (long candidate: allPersons.keySet()) {
            max = Math.max(candidate, max);
        }
        return max+1; // this is not thread safe, but it suffices for now
    }

    public boolean updateSinglePerson(long oid, ContentValues values) {
        // modify a copy and set it afterwards --> thread safety
        final Map<String, Object> origData = allPersons.get(oid);
        if (origData == null) {
            return false;
        }
        
        final Map<String, Object> data = new HashMap<String, Object>(origData);

        copyIntoMap(values, data);
        allPersons.put(oid, data);
        
        return true;
    }

    public boolean deleteSinglePerson(long oid) {
        return allPersons.remove(oid) != null;
    }
    
    public Set<String> getDistinctCountries() {
        final Set<String> result = new TreeSet<String>();
        for (Map<String, Object> person: allPersons.values()) {
            result.add((String) person.get(COL_ADDR_COUNTRY));
        }
        return result;
    }
}


