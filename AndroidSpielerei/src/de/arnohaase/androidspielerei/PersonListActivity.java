package de.arnohaase.androidspielerei;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.arnohaase.androidspielerei.person.Address;
import de.arnohaase.androidspielerei.person.Person;
import de.arnohaase.androidspielerei.person.PersonAccessor;
import de.arnohaase.androidspielerei.util.AsyncOperationFinishedListener;
import de.arnohaase.androidspielerei.util.ExecutorHelper;
import de.arnohaase.androidspielerei.util.MapWithSynthetics;


public class PersonListActivity extends ListActivity {
    public static final String INTENT_ACTION_PERSON_LIST_CHANGED = "PERSON_LIST_CHANGED";
    
    private static final Map<String, MapWithSynthetics.Expression<String, Object>> synthetics = new HashMap<String, MapWithSynthetics.Expression<String, Object>>();
    
    private boolean requiresRefresh=true;

    static {
        synthetics.put("name", new MapWithSynthetics.Expression<String, Object>() {
            public Object getValue(Map<String, Object> raw) {
                return raw.get(Person.KEY_FIRSTNAME) + " " + raw.get(Person.KEY_LASTNAME);
            }
        });
        synthetics.put("adrString", new MapWithSynthetics.Expression<String, Object>() {
            public Object getValue(Map<String, Object> raw) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> address = (Map<String, Object>) raw.get(Person.KEY_ADDRESS);

                return address.get(Address.KEY_STREET) + " " + address.get(Address.KEY_NO) + ", " + address.get(Address.KEY_CITY);
            }
        });
    }

    private final BroadcastReceiver personListChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requiresRefresh = true;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        final ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        getListView().setPadding(5, 5, 5, 5); //TODO set this more elegantly and as 5dp - but how?!

        final IntentFilter personListChangedFilter = new IntentFilter();
        personListChangedFilter.addAction(INTENT_ACTION_PERSON_LIST_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(personListChangedReceiver, personListChangedFilter);
        
//        new PersonAccessor(ExecutorHelper.createMainThreadExecutor(this)).findAllPersons(personListLoadedListener);

        //		final Intent intent = new Intent(this, PersonService.class);
        //		intent.putExtra(PersonService.EXTRAS_KEY_MESSENGER, new Messenger(handler));
        //        Log.i(getClass().getName(), "starting " + startService(intent));
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
        
        new PersonAccessor(ExecutorHelper.createMainThreadExecutor(this)).findAllPersons(personListLoadedListener);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personlist, menu);
        return true;
    }


    private final AsyncOperationFinishedListener<List<Map<String, Object>>> personListLoadedListener = new AsyncOperationFinishedListener<List<Map<String,Object>>>() {
        public void onSuccess (final List<Map<String, Object>> result) {
            final ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            root.removeView(getListView().getEmptyView());
            getListView().setEmptyView(null);

            final List<Map<String, Object>> withSynthetics = new ArrayList<Map<String,Object>>();
            for (Map<String, Object> row: result) {
                withSynthetics.add(new MapWithSynthetics<String, Object>(row, synthetics));
            }

            final ListAdapter adapter = 
                    new SimpleAdapter(PersonListActivity.this, withSynthetics, android.R.layout.two_line_list_item, new String[] {"name", "adrString"}, new int[] {android.R.id.text1, android.R.id.text2});

            setListAdapter(adapter);
            requiresRefresh = false;
        }

        public void onFailure(final Exception reason) {
            Log.e(getClass().getName(), "failed to load person list: " + reason.getMessage());
            Toast.makeText(PersonListActivity.this, "failed to load person list", Toast.LENGTH_LONG).show(); //TODO i18n --> from resource
        }

        public void onCancelled() {
            Toast.makeText(PersonListActivity.this, "canceled loading person list", Toast.LENGTH_LONG).show(); //TODO i18n --> from resource
        }
    };

    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Intent intent = new Intent();
        intent.setClass(this, PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.KEY_EXTRA_DATA, (Serializable) ((MapWithSynthetics<String, Object>) l.getItemAtPosition(position)).getInner());
        startActivity(intent);
    }
}
