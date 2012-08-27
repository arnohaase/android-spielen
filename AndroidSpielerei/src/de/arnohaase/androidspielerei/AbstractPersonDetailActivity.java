package de.arnohaase.androidspielerei;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import de.arnohaase.androidspielerei.person.PersonConstants;
import de.arnohaase.androidspielerei.person.Sex;


abstract class AbstractPersonDetailActivity extends Activity implements PersonConstants {
    protected ArrayAdapter<Sex> sexAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sexAdapter = new ArrayAdapter<Sex>(this, android.R.layout.simple_spinner_dropdown_item);
    }

    protected void initWidgets() {
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
    
    protected ContentValues guiToData() {
        final ContentValues result = new ContentValues();

        result.put(COL_FIRSTNAME, String.valueOf(findTextView(R.id.firstname).getText()));
        result.put(COL_LASTNAME, String.valueOf(findTextView(R.id.lastname).getText()));
        result.put(COL_SEX, ((Sex) (findSpinner(R.id.sex).getSelectedItem())).name());

        result.put(COL_ADDR_STREET, String.valueOf(findTextView(R.id.street).getText()));
        result.put(COL_ADDR_NO, String.valueOf(findTextView(R.id.streetnumber).getText()));
        result.put(COL_ADDR_ZIP,  String.valueOf(findTextView(R.id.zip).getText()));
        result.put(COL_ADDR_CITY, String.valueOf(findTextView(R.id.city).getText()));
        result.put(COL_ADDR_COUNTRY, String.valueOf(findTextView(R.id.country).getText()));
        
        Log.i(".....", result.getAsString(COL_SEX));
        
        return result;
    }
}
