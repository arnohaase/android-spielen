package de.arnohaase.androidspielerei;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	View v;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	Log.w(MainActivity.class.getName(), "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private final Handler handler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
    	}
    };
    
    public void onClick(View view) {
    	Log.w(MainActivity.class.getName(), "onClick");
    	final Intent intent = new Intent(this, PersonService.class);
    	intent.putExtra(PersonService.EXTRAS_KEY_MESSENGER, new Messenger(handler));
    	Log.w("...", "starting " + startService(intent));
    }
    
    public void onClickPersonList(View view) {
    	startActivity(new Intent(this, PersonListActivity.class));
    }
}
