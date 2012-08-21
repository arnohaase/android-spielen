package de.arnohaase.androidspielerei;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
    private static final Map<String, MapWithSynthetics.Expression<String, Object>> synthetics = new HashMap<String, MapWithSynthetics.Expression<String, Object>>();
    
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

        new PersonAccessor(ExecutorHelper.createMainThreadExecutor(this)).findAllPersons(personListLoadedListener);
        
//		final Intent intent = new Intent(this, PersonService.class);
//		intent.putExtra(PersonService.EXTRAS_KEY_MESSENGER, new Messenger(handler));
//        Log.i(getClass().getName(), "starting " + startService(intent));
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
        }

        public void onFailure(final Exception reason) {
            Log.e(getClass().getName(), "failed to load person list: " + reason.getMessage());
            Toast.makeText(PersonListActivity.this, "failed to load person list", Toast.LENGTH_LONG).show(); //TODO i18n --> from resource
        }

        public void onCancelled() {
            Toast.makeText(PersonListActivity.this, "canceled loading person list", Toast.LENGTH_LONG).show(); //TODO i18n --> from resource
        }
    };
	
//    private final Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            try {
//                final ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//                root.removeView(getListView().getEmptyView());
//                getListView().setEmptyView(null);
//                
//                final String rawJson = msg.obj.toString();
//                
//                @SuppressWarnings("unchecked")
//                final List<Map<String, Object>> raw = (List<Map<String, Object>>) new JsonDecoder().decode(rawJson);
//                final List<Map<String, Object>> withSynthetics = new ArrayList<Map<String,Object>>();
//                for (Map<String, Object> row: raw) {
//                    withSynthetics.add(new MapWithSynthetics<String, Object>(row, synthetics));
//                }
//
//                final ListAdapter adapter = 
//                        new SimpleAdapter(PersonListActivity.this, withSynthetics, android.R.layout.two_line_list_item, new String[] {"name", "adrString"}, new int[] {android.R.id.text1, android.R.id.text2});
//
//                setListAdapter(adapter);
//            }
//            catch(JSONException exc) {
//                Toast.makeText(PersonListActivity.this, "invalid response: " + exc.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    };
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(getClass().getName(), "clicked " + l.getItemAtPosition(position));
        final Intent intent = new Intent();
        intent.setClass(this, PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.KEY_EXTRA_DATA, (Serializable) l.getItemAtPosition(position));
        startActivity(intent);
    }
}
 