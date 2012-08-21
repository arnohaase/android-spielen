package de.arnohaase.androidspielerei.util;

import java.util.concurrent.Executor;

import android.app.Activity;


/**
 * This is a factory for convenience Executors, e.g. for use with 'accessor' objects that
 *  can execute result callbacks in a client-provided executor 
 * 
 * @author arno
 */
public class ExecutorHelper {
    /**
     * This is a plain, no-frills executor that just passes the call through to the command
     */
    public static final Executor NULL_EXECUTOR = new Executor() {
        public void execute(Runnable command) {
            command.run();
        }
    };
    
    
    /**
     * This method creates an executor that executes a callback in the main thread of the
     *  passed-in Activity.
     */
    public static Executor createMainThreadExecutor(Activity activity) {
        return new MainThreadExecutor(activity);
    }
    
    private static class MainThreadExecutor implements Executor {
        private final Activity activity;

        public MainThreadExecutor (Activity activity) {
            this.activity = activity;
        }
        
        public void execute (Runnable command) {
            activity.runOnUiThread(command);
        }
    }
}
