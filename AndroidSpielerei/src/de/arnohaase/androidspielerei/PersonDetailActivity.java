package de.arnohaase.androidspielerei;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import de.arnohaase.androidspielerei.person.Sex;


public class PersonDetailActivity extends Activity {
    public static final String KEY_EXTRA_DATA = "data"; 
    
    private ArrayAdapter<Sex> sexAdapter;
 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sexAdapter = new ArrayAdapter<Sex>(this, android.R.layout.simple_spinner_dropdown_item);

        @SuppressWarnings("unchecked")
        final Map<String, Object> data = (Map<String, Object>) getIntent().getExtras().get(KEY_EXTRA_DATA);

        Log.i("...", data.toString());
        
        setContentView(R.layout.activity_person_details);
        initWidgets();
        
        dataToGui(data);
    }

    private void initWidgets() {
        final Spinner spinner = (Spinner) findViewById(R.id.sex);
        
        sexAdapter.add(Sex.m);
        sexAdapter.add(Sex.f);
        spinner.setAdapter(sexAdapter);
    }
    
    private void dataToGui(Map<String, Object> person) {
        ((TextView) findViewById(R.id.firstname)).setText(String.valueOf(person.get("firstname")));
        ((TextView) findViewById(R.id.lastname)).setText(String.valueOf(person.get("lastname")));
        ((Spinner) findViewById(R.id.sex)).setSelection(sexAdapter.getPosition(Sex.valueOf((String) person.get("sex"))));
    }
}

/*

08-17 19:49:26.040: I/...(5230): {
lastname=Haase, 
sex=m, 
adress={zip=12345, no=8, country=Germany, city=Dodge City, street=Sesame Street}, 
firstname=Arno 8, 
oid=8, 

name=Arno 8 Haase, 
adrString=Sesame Street 8, Dodge City
}


*/