package de.arnohaase.androidspielerei;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import de.arnohaase.androidspielerei.person.Person;
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

        setContentView(R.layout.activity_person_details);
        initWidgets();
        
        dataToGui(data);
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
    
    private void dataToGui(Map<String, Object> personData) {
        final Person person = new Person(personData);
        
        findTextView(R.id.firstname).setText(person.getFirstname());
        findTextView(R.id.lastname).setText(person.getLastname());
        findSpinner(R.id.sex).setSelection(sexAdapter.getPosition(person.getSex()));
        
        findTextView(R.id.street).setText(person.getAddress().getStreet());
        findTextView(R.id.streetnumber).setText(person.getAddress().getNo());
        findTextView(R.id.zip).setText(person.getAddress().getZip());
        findTextView(R.id.city).setText(person.getAddress().getCity());
        findTextView(R.id.country).setText(person.getAddress().getCountry());
    }
}
