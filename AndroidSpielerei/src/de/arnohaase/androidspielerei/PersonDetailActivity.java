package de.arnohaase.androidspielerei;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.arnohaase.androidspielerei.person.Person;
import de.arnohaase.androidspielerei.person.PersonConstants;
import de.arnohaase.androidspielerei.person.Sex;
import de.arnohaase.androidspielerei.provider.PersonProvider;


public class PersonDetailActivity extends Activity implements PersonConstants {
    public static final String KEY_EXTRA_ID = "oid"; 
    
    private ArrayAdapter<Sex> sexAdapter;
    
    private static final String[] PROJECTION = new String[] {
        COL_OID, COL_FIRSTNAME, COL_LASTNAME, COL_SEX, COL_ADDR_STREET, COL_ADDR_NO, COL_ADDR_ZIP, COL_ADDR_CITY, COL_ADDR_COUNTRY
    };
    
    private final LoaderCallbacks<Cursor> personLoadedCallbacks = new LoaderCallbacks<Cursor>() {
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(PersonDetailActivity.this, ContentUris.withAppendedId(PersonProvider.URI_PERSON_SINGLE, getPersonId()), PROJECTION, null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            //TODO progress indicator
            setContentView(R.layout.activity_person_details); //TODO do this only once
            initWidgets(); //TODO do this only once
            
            cursor.moveToNext(); //TODO error handling if there was no result
            
            final Person person = new Person(cursor);
                
            findTextView(R.id.firstname).setText(person.getFirstname());
            findTextView(R.id.lastname).setText(person.getLastname());
            findSpinner(R.id.sex).setSelection(sexAdapter.getPosition(person.getSex()));

            findTextView(R.id.street).setText(person.getStreet());
            findTextView(R.id.streetnumber).setText(person.getNo());
            findTextView(R.id.zip).setText(person.getZip());
            findTextView(R.id.city).setText(person.getCity());
            findTextView(R.id.country).setText(person.getCountry());
        }

        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sexAdapter = new ArrayAdapter<Sex>(this, android.R.layout.simple_spinner_dropdown_item);
        getLoaderManager().initLoader(0, null, personLoadedCallbacks);
    }

    private long getPersonId() {
        return getIntent().getExtras().getLong(KEY_EXTRA_ID);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.persondetails, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_person_save:
            final ContentValues values = guiToData();

            new AsyncTask<ContentValues, Object, Integer>() {
                @Override
                protected Integer doInBackground(ContentValues... params) {
                    final int result = getContentResolver().update(ContentUris.withAppendedId(PersonProvider.URI_PERSON_SINGLE, getPersonId()), values, null, null);
                    
                    //TODO move this broadcast into PersonProvider
                    LocalBroadcastManager.getInstance(PersonDetailActivity.this).sendBroadcast(new Intent(PersonListActivity.INTENT_ACTION_PERSON_LIST_CHANGED));
                    finish();
                    return result;
                }
                
                protected void onPostExecute(Integer result) {
                    Toast.makeText(PersonDetailActivity.this, getMessage(result), Toast.LENGTH_SHORT).show();
                }
                
                private String getMessage(int result) {
                    switch(result) {
                    case 1:  return "saved person " + values.getAsString(COL_FIRSTNAME) + " " + values.getAsString(COL_LASTNAME) + ".";
                    default: return "failed to save person " + values.getAsString(COL_FIRSTNAME) + " " + values.getAsString(COL_LASTNAME) + ".";
                    }
                }
            }.execute(values);
            
            break;
        case R.id.menu_person_delete:
            //TODO implement 'delete'
            
//            new PersonAccessor(ExecutorHelper.createMainThreadExecutor(this)).deletePerson((Long) data.get(COL_OID), new AsyncOperationFinishedListener<Boolean>() {
//                public void onSuccess(Boolean result) {
//                    Toast.makeText(PersonDetailActivity.this, "Person deleted: " + data.get(COL_FIRSTNAME) + " " + data.get(COL_LASTNAME), Toast.LENGTH_SHORT).show();
//                    //TODO action bar notification instead
//                    
//                    LocalBroadcastManager.getInstance(PersonDetailActivity.this).sendBroadcast(new Intent(PersonListActivity.INTENT_ACTION_PERSON_LIST_CHANGED));
//                    finish();
//                }
//                
//                public void onFailure(Exception reason) {
//                    //TODO i18n for toasts
//                    Toast.makeText(PersonDetailActivity.this, "Failed to delete person: " + data.get(COL_FIRSTNAME) + " " + data.get(COL_LASTNAME), Toast.LENGTH_SHORT).show();
//                }
//                
//                public void onCancelled() {
//                }
//            });
//            
            break;
        }
        return true;
    }
    
    private void initWidgets() {
        final Spinner spinner = (Spinner) findViewById(R.id.sex);
        sexAdapter.add(Sex.m);
        sexAdapter.add(Sex.f);
        spinner.setAdapter(sexAdapter);

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.country);
        textView.setAdapter(new PersonCountryAutocompleteAdapter(this));
    }
    
    private TextView findTextView(int id) {
        return (TextView) findViewById(id);
    }
    
    private Spinner findSpinner(int id) {
        return (Spinner) findViewById(id);
    }
    
    private ContentValues guiToData() {
        final ContentValues result = new ContentValues();

        result.put(COL_FIRSTNAME, String.valueOf(findTextView(R.id.firstname).getText()));
        result.put(COL_LASTNAME, String.valueOf(findTextView(R.id.lastname).getText()));
        result.put(COL_SEX, ((Sex) (findSpinner(R.id.sex).getSelectedItem())).name());

        result.put(COL_ADDR_STREET, String.valueOf(findTextView(R.id.street).getText()));
        result.put(COL_ADDR_NO, String.valueOf(findTextView(R.id.streetnumber).getText()));
        result.put(COL_ADDR_ZIP,  String.valueOf(findTextView(R.id.zip).getText()));
        result.put(COL_ADDR_CITY, String.valueOf(findTextView(R.id.city).getText()));
        result.put(COL_ADDR_CITY, String.valueOf(findTextView(R.id.country).getText()));
        
        return result;
    }
}
