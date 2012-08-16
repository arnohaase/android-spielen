package de.arnohaase.androidspielerei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.arnohaase.androidspielerei.util.JsonDecoder;
import de.arnohaase.androidspielerei.util.MapWithSynthetics;


public class PersonListActivity extends ListActivity {
    private static final Map<String, MapWithSynthetics.Expression<String, Object>> synthetics = new HashMap<String, MapWithSynthetics.Expression<String, Object>>();
    
    static {
        synthetics.put("name", new MapWithSynthetics.Expression<String, Object>() {
            public Object getValue(Map<String, Object> raw) {
                return raw.get("firstname") + " " + raw.get("lastname");
            }
        });
        synthetics.put("adrString", new MapWithSynthetics.Expression<String, Object>() {
            public Object getValue(Map<String, Object> raw) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> adress = (Map<String, Object>) raw.get("adress");
                
                return adress.get("street") + " " + adress.get("no") + ", " + adress.get("city");
            }
        });
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		final Intent intent = new Intent(this, PersonService.class);
		intent.putExtra(PersonService.EXTRAS_KEY_MESSENGER, new Messenger(handler));
        Log.i("...", "starting " + startService(intent));
	}
	
    private final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                final String rawJson = msg.obj.toString();
                
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> raw = (List<Map<String, Object>>) new JsonDecoder().decode(rawJson);
                final List<Map<String, Object>> withSynthetics = new ArrayList<Map<String,Object>>();
                for (Map<String, Object> row: raw) {
                    withSynthetics.add(new MapWithSynthetics<String, Object>(row, synthetics));
                }

                final ListAdapter adapter = 
                        new SimpleAdapter(PersonListActivity.this, withSynthetics, android.R.layout.two_line_list_item, new String[] {"name", "adrString"}, new int[] {android.R.id.text1, android.R.id.text2});

                setListAdapter(adapter);
            }
            catch(JSONException exc) {
                Toast.makeText(PersonListActivity.this, "invalid response: " + exc.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
 