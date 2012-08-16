package de.arnohaase.androidspielerei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import de.arnohaase.androidspielerei.util.MapWithSynthetics;


public class PersonListActivity extends ListActivity {
    private static final Map<String, MapWithSynthetics.Expression<String, Object>> synthetics = new HashMap<String, MapWithSynthetics.Expression<String, Object>>();
    
    static {
        synthetics.put("name", new MapWithSynthetics.Expression<String, Object>() {
            public Object getValue(Map<String, Object> raw) {
                return raw.get("firstname") + " " + raw.get("lastname");
            }
        });
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		final List<Map<String, Object>> raw = createPersons();
		final List<Map<String, Object>> withSynthetics = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> row: raw) {
		    withSynthetics.add(new MapWithSynthetics<String, Object>(row, synthetics));
		}
		
		final ListAdapter adapter = new SimpleAdapter(this, withSynthetics, android.R.layout.two_line_list_item, new String[] {"name", "street"}, new int[] {android.R.id.text1, android.R.id.text2});
//		final ListAdapter adapter = new JsonListAdapter(this, createPersons());
		
		setListAdapter(adapter);
	}
	
	private List<Map<String, Object>> createPersons() {
		final List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		for (int i=0; i<20; i++) {
			result.add(createPerson(i));
		}
		
		return result;
	}

	private Map<String, Object> createPerson(int idx) {
		final Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("oid", idx);
		result.put("firstname", "first " + idx);
		result.put("lastname", "last " + idx);
		
		result.put("street", "Sesame Street");
		result.put("no", "" + idx);
		result.put("city", "Dodge City");
		
		return result;
	}
}
 