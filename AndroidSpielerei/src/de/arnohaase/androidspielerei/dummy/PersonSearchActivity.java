package de.arnohaase.androidspielerei.dummy;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import de.arnohaase.androidspielerei.provider.PersonProvider;


public class PersonSearchActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.search);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          String query = intent.getStringExtra(SearchManager.QUERY);
          doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        final ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, 
                getContentResolver().query(Uri.withAppendedPath(PersonProvider.URI_PERSON_SEARCH, query), null, null, null, null), 
                new String[] {"shortname", "longname"}, 
                new int[] {android.R.id.text1, android.R.id.text2}, 0); 
        setListAdapter(adapter);
    }
}
