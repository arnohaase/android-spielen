package de.arnohaase.androidspielerei;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import de.arnohaase.androidspielerei.provider.PersonProvider;


public class PersonNewActivity extends AbstractPersonDetailActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_details);
        initWidgets();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personnew, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_person_save:
            final ContentValues values = guiToData();

            new AsyncTask<ContentValues, Object, Uri>() {
                @Override
                protected Uri doInBackground(ContentValues... params) {
                    return getContentResolver().insert(PersonProvider.URI_PERSON_LIST, values);
                }
                
                protected void onPostExecute(Uri result) {
                    if (result != null) {
                        finish();
                    }
                    Toast.makeText(PersonNewActivity.this, getMessage(result), Toast.LENGTH_SHORT).show();
                }
                
                private String getMessage(Uri result) {
                    if (result != null) {
                        return "saved person " + values.getAsString(COL_FIRSTNAME) + " " + values.getAsString(COL_LASTNAME) + ".";
                    }
                    else {
                        return "failed to save person " + values.getAsString(COL_FIRSTNAME) + " " + values.getAsString(COL_LASTNAME) + ".";
                    }
                }
            }.execute(values);
        }
        return true;
    }
}
