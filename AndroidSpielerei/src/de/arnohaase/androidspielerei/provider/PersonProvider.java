package de.arnohaase.androidspielerei.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import de.arnohaase.androidspielerei.person.PersonConstants;
import de.arnohaase.androidspielerei.util.MapSyntheticValueExtractor;


public class PersonProvider extends ContentProvider implements PersonConstants {
    public static final String AUTHORITY = PersonProvider.class.getName();

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    public static final Uri URI_PERSON_SEARCH_PREFIX = Uri.parse("content://" + AUTHORITY + "/search_suggest_query/");

    public static final Uri URI_PERSON_LIST = Uri.parse("content://" + AUTHORITY + "/person");
    public static final Uri URI_PERSON_SINGLE = Uri.parse("content://" + AUTHORITY + "/person/");

    
    private static final int ID_URI_SEARCH_SUGGEST = 1;
    private static final int ID_URI_PERSON_LIST = 2;
    private static final int ID_URI_PERSON_SINGLE = 3;
    
    static {
        uriMatcher.addURI(AUTHORITY, "search_suggest_query/*", ID_URI_SEARCH_SUGGEST); 
        uriMatcher.addURI(AUTHORITY, "person", ID_URI_PERSON_LIST); 
        uriMatcher.addURI(AUTHORITY, "person/#", ID_URI_PERSON_SINGLE); 
    }

    private static final Map<Long, Map<String, Object>> allPersons = new ConcurrentHashMap<Long, Map<String,Object>>();
    static {
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

    
    @Override
    public boolean onCreate() {
        return true;
    }
    
    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException();//TODO
    }

    private int parseIntQueryParameter(Uri uri, String paramName, int defaultValue) {
        try {
            return Integer.parseInt(uri.getQueryParameter(paramName));
        }
        catch(Exception exc) {
            return defaultValue;
        }
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
        case ID_URI_SEARCH_SUGGEST:
            return getSearchSuggestions(uri.getLastPathSegment(), parseIntQueryParameter(uri, "limit",  20));
        case ID_URI_PERSON_LIST:    return getPersonList(projection, sortOrder);
        case ID_URI_PERSON_SINGLE:  return getSinglePerson(ContentUris.parseId(uri), projection);
        default:
            throw new IllegalArgumentException ("unrecognized URI " + uri);   
        }
    }

    private Cursor getPersonList(String[] projection, String sortOrder) {
        //TODO sortOrder --> parse the string into a Comparator of Maps
        final MatrixCursor result = new MatrixCursor(projection);
        for (Map<String, Object> person: allPersons.values()) {
            result.addRow(createRowData(person, projection));
        }
        return result;
    }

    private Cursor getSinglePerson(long id, String[] projection) {
        final MatrixCursor result = new MatrixCursor(projection);
        result.addRow(createRowData(allPersons.get(id), projection));
        return result;
    }

    private Object[] createRowData(Map<String, Object> orig, String[] projection) {
        final Object[] result = new Object[projection.length];
        for (int i=0; i<projection.length; i++) {
            result[i] = orig.get(projection[i]);
        }
        return result;
    }
    
    private Cursor getSearchSuggestions(String searchText, int limit) {
        final MatrixCursor result = new MatrixCursor(new String[] {"_ID", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2});
        
        if (searchText == null) {
            return result;
        }
        
        final String lowerSearchText = searchText.toLowerCase();
        
        //TODO sort
        for (Map<String, Object> candidate: allPersons.values()) {
            final MapSyntheticValueExtractor extractor = new MapSyntheticValueExtractor(candidate);
            final String name = extractor.getAsString("${" + COL_FIRSTNAME + "} ${" + COL_LASTNAME + "}");
            
            if (String.valueOf(name).toLowerCase().contains(lowerSearchText)) {
                
                result.addRow(new Object[] {
                        candidate.get(COL_OID),
                        name,
                        extractor.get("${" + COL_ADDR_STREET + "} ${" + COL_ADDR_NO + "}, ${" + COL_ADDR_CITY + "}")});
            }
        }
        
        return result;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
        case ID_URI_PERSON_SINGLE:  return doUpdateSinglePerson(ContentUris.parseId(uri), values);
        default:
            throw new IllegalArgumentException ("unrecognized URI " + uri);   
        }
    }

    private int doUpdateSinglePerson(long personId, ContentValues values) {
        // modify a copy and set it afterwards --> thread safety
        final Map<String, Object> origData = allPersons.get(personId);
        if (origData == null) {
            return 0;
        }
        
        final Map<String, Object> data = new HashMap<String, Object>(origData);
        
        for (String key: values.keySet()) {
            data.put(key, values.get(key));
        }
        allPersons.put(personId, data);
        
        return 1;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();//TODO
    }
    
    
}
