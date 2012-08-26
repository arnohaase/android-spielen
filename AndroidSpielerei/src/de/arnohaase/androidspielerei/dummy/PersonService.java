package de.arnohaase.androidspielerei.dummy;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public class PersonService extends IntentService {
	public PersonService() {
		super("Person Lookup Service");
	}

	public static final String EXTRAS_KEY_MESSENGER = "MESSENGER";

	@Override
	protected void onHandleIntent(Intent intent) {
		final Bundle extras = intent.getExtras();
		if (extras != null) {
			final Messenger messenger = (Messenger) extras.get(EXTRAS_KEY_MESSENGER);
			final Message msg = Message.obtain();
			msg.arg1 = Activity.RESULT_OK;
			msg.obj = createPersonsJson();
			
//			AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android Probieren");
			
			try {
			    // simulate delay, e.g. for database or server access
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                Log.w(PersonService.class.getName(), e1);
            }
			
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				Log.w("exception sending message", e);
			}
		}
	}
	
	private String createPersonsJson() {
		final StringBuilder result = new StringBuilder("[");
		
		for (int i=0; i<20; i++) {
			if (i>0) {
				result.append(",");
			}
			result.append (createPersonJson(i));
		}
		
		result.append("]");
		return result.toString();
	}
	
	private String createPersonJson(int idx) {
	    if (idx % 2 == 0) {
	        return "{oid=" + idx + ", firstname:'Arno " + idx + "', lastname:'Haase', sex:'m', address:{street: 'Sesame Street', no: '" + idx + "', zip: '12345', city: 'Dodge City', country: 'Germany'}}";
	    }
	    else {
	        return "{oid=" + idx + ", firstname:'Testa " + idx + "', lastname:'Testarossa', sex:'f', address:{street: 'Sesame Street', no: '" + idx + "', zip: '12345', city: 'Dodge City', country: 'Germany'}}";
	    }
	}
}
