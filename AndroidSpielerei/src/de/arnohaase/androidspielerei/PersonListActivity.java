package de.arnohaase.androidspielerei;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import de.arnohaase.androidspielerei.provider.PersonProvider;


public class PersonListActivity extends ListActivity { // implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean requiresRefresh=false;

    private final BroadcastReceiver personListChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requiresRefresh = true;
        }
    };

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurSearchText;

    final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor> () {
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = PersonProvider.URI_PERSON_SEARCH;
            if (! TextUtils.isEmpty(mCurSearchText)) {
                uri = Uri.withAppendedPath(uri, mCurSearchText);
            }
            
            return new CursorLoader(PersonListActivity.this, uri, null, null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }    
    };
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
//        setEmptyText("No phone numbers");

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2 },
                new int[] { android.R.id.text1, android.R.id.text2 }, 0);
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
//        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one or start a new one.
        getLoaderManager().initLoader(0, null, loaderCallbacks);

        final IntentFilter personListChangedFilter = new IntentFilter();
        personListChangedFilter.addAction(PersonProvider.INTENT_ACTION_PERSON_LIST_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(personListChangedReceiver, personListChangedFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(personListChangedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        doRefreshIfRequired();
    }

    private void doRefreshIfRequired() {
        if (! requiresRefresh) {
            return;
        }

        getLoaderManager().restartLoader(0, null, loaderCallbacks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personlist, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                final String newSearchText = !TextUtils.isEmpty(newText) ? newText : null;
                
                // only search if the text changed
                if (mCurSearchText == null && newSearchText == null) {
                    return true;
                }
                if (mCurSearchText != null && mCurSearchText.equals(newSearchText)) {
                    return true;
                }
                
                mCurSearchText = newSearchText;
                getLoaderManager().restartLoader(0, null, loaderCallbacks);
                return true;
            }
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_person_new:
            final Intent intent = new Intent();
            intent.setClass(this, PersonNewActivity.class);
            startActivity(intent);
            return true;
        case R.id.menu_personlist_refresh:
            requiresRefresh = true;
            doRefreshIfRequired();
            return true;
        default: return true;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Intent intent = new Intent();
        intent.setClass(this, PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.KEY_EXTRA_ID, id); 
        startActivity(intent);
    }
}

