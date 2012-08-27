package de.arnohaase.androidspielerei.provider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import de.arnohaase.androidspielerei.person.PersonConstants;
import de.arnohaase.androidspielerei.util.MapSyntheticValueExtractor;


public class PersonProvider extends ContentProvider implements PersonConstants {
    public static final String AUTHORITY = PersonProvider.class.getName();

    public static final String INTENT_ACTION_PERSON_LIST_CHANGED = "PERSON_LIST_CHANGED";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    public static final Uri URI_PERSON_SEARCH = Uri.parse("content://" + AUTHORITY + "/search_suggest_query");

    public static final Uri URI_PERSON_LIST = Uri.parse("content://" + AUTHORITY + "/person");
    public static final Uri URI_PERSON_SINGLE = Uri.parse("content://" + AUTHORITY + "/person/");

    public static final Uri URI_PERSON_COUNTRIES = Uri.parse("content://" + AUTHORITY + "/countries");
    
    private static final int ID_URI_SEARCH_SUGGEST_WITHOUT_FILTER = 1;
    private static final int ID_URI_SEARCH_SUGGEST_WITH_FILTER = 2;
    private static final int ID_URI_PERSON_LIST = 3;
    private static final int ID_URI_PERSON_SINGLE = 4;
    private static final int ID_URI_COUNTRIES = 5;
    
    static {
        uriMatcher.addURI(AUTHORITY, "search_suggest_query",   ID_URI_SEARCH_SUGGEST_WITHOUT_FILTER); 
        uriMatcher.addURI(AUTHORITY, "search_suggest_query/*", ID_URI_SEARCH_SUGGEST_WITH_FILTER); 
        uriMatcher.addURI(AUTHORITY, "person", ID_URI_PERSON_LIST); 
        uriMatcher.addURI(AUTHORITY, "person/#", ID_URI_PERSON_SINGLE);
        uriMatcher.addURI(AUTHORITY, "countries", ID_URI_COUNTRIES);
    }

    private static final PersonDataSource dataSource = new InMemoryPersonDataSource();

    
    @Override
    public boolean onCreate() {
        return true;
    }
    
    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException();//TODO MIME type
    }

    private int parseIntQueryParameter(Uri uri, String paramName, int defaultValue) {
        try {
            return Integer.parseInt(uri.getQueryParameter(paramName));
        }
        catch(Exception exc) {
            return defaultValue;
        }
    }

    private void broadcastPersonListChanged() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(INTENT_ACTION_PERSON_LIST_CHANGED));
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
        case ID_URI_SEARCH_SUGGEST_WITHOUT_FILTER: return getSearchSuggestions(null, parseIntQueryParameter(uri, "limit",  20));
        case ID_URI_SEARCH_SUGGEST_WITH_FILTER: return getSearchSuggestions(uri.getLastPathSegment(), parseIntQueryParameter(uri, "limit",  20));
        case ID_URI_PERSON_LIST:    return getPersonList(projection, sortOrder, parseIntQueryParameter(uri, "limit",  20));
        case ID_URI_PERSON_SINGLE:  return getSinglePerson(ContentUris.parseId(uri), projection);
        case ID_URI_COUNTRIES: return getDistinctCountries();
        default:
            throw new IllegalArgumentException ("unrecognized URI " + uri);   
        }
    }

    private Cursor getDistinctCountries() {
        final MatrixCursor result = new MatrixCursor(new String[] {COL_ADDR_COUNTRY});
        
        for (String country: dataSource.getDistinctCountries()) {
            result.addRow(new Object[] {country});
        }
        return result;
    }

    private List<String> asSplitList(String sortOrder) {
        return Arrays.asList(sortOrder.split("[ ,]"));
    }
    
    private Cursor getPersonList(String[] projection, String sortOrder, int limit) {
        final MatrixCursor result = new MatrixCursor(projection);

        for (Map<String, Object> person: dataSource.findPersons(null, asSplitList(sortOrder), limit)) {
            result.addRow(createRowData(person, projection));
            if (result.getCount() >= limit) {
                break;
            }
        }
        return result;
    }

    private Cursor getSinglePerson(long id, String[] projection) {
        final MatrixCursor result = new MatrixCursor(projection);
        result.addRow(createRowData(dataSource.findSinglePerson(id), projection));
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
        
        for (Map<String, Object> person: dataSource.findPersons(searchText, Arrays.asList(COL_LASTNAME, COL_FIRSTNAME), limit)) {
            final MapSyntheticValueExtractor extractor = new MapSyntheticValueExtractor(person);
            result.addRow(new Object[] {
                    person.get(COL_OID),
                    extractor.getAsString("${" + COL_FIRSTNAME + "} ${" + COL_LASTNAME + "}"),
                    extractor.get("${" + COL_ADDR_STREET + "} ${" + COL_ADDR_NO + "}, ${" + COL_ADDR_CITY + "}")});
        }
        return result;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(uriMatcher.match(uri)) {
        case ID_URI_PERSON_LIST: 
            return doInsertSinglePerson(values);
        default: 
            throw new IllegalArgumentException(uri.toString());
        }
    }
    
    private Uri doInsertSinglePerson(ContentValues values) {
        final long oid = dataSource.insertSinglePerson(values);
        broadcastPersonListChanged();
        return ContentUris.withAppendedId(URI_PERSON_SINGLE, oid);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
        case ID_URI_PERSON_SINGLE: 
            return doUpdateSinglePerson(ContentUris.parseId(uri), values);
        default:
            throw new IllegalArgumentException ("unrecognized URI " + uri);   
        }
    }

    private int doUpdateSinglePerson(long personId, ContentValues values) {
        final int result = dataSource.updateSinglePerson(personId, values) ? 1 : 0;
        if (result != 0) {
            broadcastPersonListChanged();
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(uriMatcher.match(uri)) {
        case ID_URI_PERSON_SINGLE:
            return doDeleteSinglePerson(ContentUris.parseId(uri));
        default: 
            throw new IllegalArgumentException(uri.toString());
        }
    }

    private int doDeleteSinglePerson(long oid) {
        final int result = dataSource.deleteSinglePerson(oid) ? 1 : 0;
        if (result != 0) {
            broadcastPersonListChanged();
        }
        return result;
    }
}
