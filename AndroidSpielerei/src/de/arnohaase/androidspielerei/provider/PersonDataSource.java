package de.arnohaase.androidspielerei.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;


/**
 * abstraction for the actual storage of person data - in memory, on a server via JSON, ...
 * 
 * @author arno
 */
public interface PersonDataSource {
    List<Map<String, Object>> findPersons(String searchFilter, List<String> sortColumns, int limit);
    Map<String, Object> findSinglePerson(long oid);
    
    long insertSinglePerson(ContentValues values);
    boolean updateSinglePerson(long oid, ContentValues values);
    boolean deleteSinglePerson(long oid);
    
    Set<String> getDistinctCountries();
}
