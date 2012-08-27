package de.arnohaase.androidspielerei;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.arnohaase.androidspielerei.person.Person;
import de.arnohaase.androidspielerei.provider.PersonProvider;


public class PersonDetailActivity extends AbstractPersonDetailActivity {
    public static final String KEY_EXTRA_ID = "oid"; 
    
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
            //TODO enable save action only if the data was actually changed
            final ContentValues values = guiToData();

            new AsyncTask<ContentValues, Object, Integer>() {
                @Override
                protected Integer doInBackground(ContentValues... params) {
                    return getContentResolver().update(ContentUris.withAppendedId(PersonProvider.URI_PERSON_SINGLE, getPersonId()), values, null, null);
                }
                
                protected void onPostExecute(Integer result) {
                    if (result > 0) {
                        finish();
                    }
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
            //TODO add confirmation dialog
            new AsyncTask<Long, Object, Integer>() {
                @Override
                protected Integer doInBackground(Long... oids) {
                    return getContentResolver().delete(ContentUris.withAppendedId(PersonProvider.URI_PERSON_SINGLE, getPersonId()), null, null);
                }
                
                protected void onPostExecute(Integer result) {
                    if (result > 0) {
                        finish();
                    }
                    Toast.makeText(PersonDetailActivity.this, getMessage(result), Toast.LENGTH_SHORT).show();
                }
                
                private String getMessage(int result) {
                    switch(result) {
                    case 1:  return "deleted person.";
                    default: return "failed to delete person.";
                    }
                }
            }.execute(getPersonId());

            break;
        }
        return true;
    }
    
    private TextView findTextView(int id) {
        return (TextView) findViewById(id);
    }
    
    private Spinner findSpinner(int id) {
        return (Spinner) findViewById(id);
    }
}
