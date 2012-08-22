package de.arnohaase.androidspielerei;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.arnohaase.androidspielerei.person.Person;
import de.arnohaase.androidspielerei.person.PersonAccessor;
import de.arnohaase.androidspielerei.person.Sex;
import de.arnohaase.androidspielerei.util.AsyncOperationFinishedListener;
import de.arnohaase.androidspielerei.util.ExecutorHelper;


public class PersonDetailActivity extends Activity {
    public static final String KEY_EXTRA_DATA = "data"; 
    
    private ArrayAdapter<Sex> sexAdapter;
    private Map<String, Object> data;
 
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sexAdapter = new ArrayAdapter<Sex>(this, android.R.layout.simple_spinner_dropdown_item);

        data = (Map<String, Object>) getIntent().getExtras().get(KEY_EXTRA_DATA);

        setContentView(R.layout.activity_person_details);
        initWidgets();
        
        dataToGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.persondetails, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Person person = new Person(data);
        
        switch(item.getItemId()) {
        case R.id.menu_person_save:
            guiToData();
            new PersonAccessor(ExecutorHelper.createMainThreadExecutor(this)).savePerson(data, new AsyncOperationFinishedListener<Boolean>() {
                public void onSuccess(Boolean result) {
                    //TODO i18n
                    Toast.makeText(PersonDetailActivity.this, "Person saved: " + person.getFirstname() + " " + person.getLastname(), Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(PersonDetailActivity.this).sendBroadcast(new Intent(PersonListActivity.INTENT_ACTION_PERSON_LIST_CHANGED));
                }

                public void onFailure(Exception reason) {
                    Log.e("...", "save failure", reason);
                    //TODO i18n
                    Toast.makeText(PersonDetailActivity.this, "Failed to save person: " + person.getFirstname() + " " + person.getLastname(), Toast.LENGTH_SHORT).show();
                }

                public void onCancelled() {
                }
            });
            
            break;
        case R.id.menu_person_delete:
            new PersonAccessor(ExecutorHelper.createMainThreadExecutor(this)).deletePerson(person.getOid(), new AsyncOperationFinishedListener<Boolean>() {
                public void onSuccess(Boolean result) {
                    Toast.makeText(PersonDetailActivity.this, "Person deleted: " + person.getFirstname() + " " + person.getLastname(), Toast.LENGTH_SHORT).show();
                    //TODO action bar notification instead
                    
                    LocalBroadcastManager.getInstance(PersonDetailActivity.this).sendBroadcast(new Intent(PersonListActivity.INTENT_ACTION_PERSON_LIST_CHANGED));
                    finish();
                }
                
                public void onFailure(Exception reason) {
                    //TODO i18n for toasts
                    Toast.makeText(PersonDetailActivity.this, "Failed to delete person: " + person.getFirstname() + " " + person.getLastname(), Toast.LENGTH_SHORT).show();
                }
                
                public void onCancelled() {
                }
            });
            
            break;
        }
        return true;
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
    
    private void guiToData() {
        final Person person = new Person(data);
        
        person.setFirstname(findTextView(R.id.firstname).getText());
        person.setLastname(findTextView(R.id.lastname).getText());
        person.setSex((Sex) findSpinner(R.id.sex).getSelectedItem());

        person.getAddress().setStreet(findTextView(R.id.street).getText());
        person.getAddress().setNo(findTextView(R.id.streetnumber).getText());
        person.getAddress().setZip(findTextView(R.id.zip).getText());
        person.getAddress().setCity(findTextView(R.id.city).getText());
        person.getAddress().setCountry(findTextView(R.id.country).getText());
    }
    
    private void dataToGui() {
        final Person person = new Person(data);
        
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
