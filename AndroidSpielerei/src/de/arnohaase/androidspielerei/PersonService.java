package de.arnohaase.androidspielerei;

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
		super("Name Lookup Service");
	}

	public static final String EXTRAS_KEY_MESSENGER = "MESSENGER";

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.w(PersonService.class.getName(), "onHandleIntent");
		final Bundle extras = intent.getExtras();
		if (extras != null) {
			final Messenger messenger = (Messenger) extras.get(EXTRAS_KEY_MESSENGER);
			final Message msg = Message.obtain();
			msg.arg1 = Activity.RESULT_OK;
			msg.obj = "Arno Haase";
			
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				Log.w("exception sending message", e);
			}
		}
	}
}
