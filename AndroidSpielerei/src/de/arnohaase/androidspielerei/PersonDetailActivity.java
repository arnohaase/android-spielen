package de.arnohaase.androidspielerei;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class PersonDetailActivity extends Activity {
    public static final String KEY_EXTRA_DATA = "data"; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @SuppressWarnings("unchecked")
        final Map<String, Object> data = (Map<String, Object>) getIntent().getExtras().get(KEY_EXTRA_DATA);

        Log.i("...", data.toString());
    }
}
