package de.arnohaase.androidspielerei.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * This is a specialized Future implementation (base on <code>java.util.concurrent.FutureTask</code> that provides some special behavior for the Android platform
 * 
 * @author arno
 */
public abstract class AndroidFutureTask<T> implements RunnableFuture<T>, Callable<T> {
    private final Executor resultCallbackExecutor;
    private final AsyncOperationFinishedListener<T> finishedCallback;
    private final FutureTask<T> inner;

    public AndroidFutureTask(Executor resultCallbackExecutor, AsyncOperationFinishedListener<T> finshedCallback) {
        this.resultCallbackExecutor = resultCallbackExecutor;
        this.finishedCallback = finshedCallback;
        
        inner = new FutureTask<T> (this) {
            @Override
            protected void done() {
                AndroidFutureTask.this.done();
            }  
        };
    }

    /**
     * convenience method that starts this task in a newly created thread
     * TODO this is the way the samples do it - is this really a good way?
     */
    public AndroidFutureTask<T> startInNewThread() {
        new Thread(this).start();
        return this;
    }

    void done() {
        //TODO how to perform these callbacks more elegantly in the main thread? AsyncTask appears to be able to do that, but how?!
        if (finishedCallback == null) {
            return;
        }

        resultCallbackExecutor.execute(new Runnable() {
            public void run() {
                if (isCancelled()) {
                    finishedCallback.onCancelled();
                }
                else {
                    try {
                        finishedCallback.onSuccess(get());
                    } catch (ExecutionException e) {
                        finishedCallback.onFailure(e);
                    } catch (InterruptedException e) {
                        finishedCallback.onFailure(e);
                    }
                }
            }
        });
    }

    public abstract T call() throws Exception;

    public void run() {
        inner.run();
    }
    
    public boolean cancel(boolean mayInterruptIfRunning) {
        return inner.cancel(mayInterruptIfRunning);
    }

    public T get() throws InterruptedException, ExecutionException {
        return inner.get();
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return inner.get(timeout, unit);
    }

    public boolean isCancelled() {
        return inner.isCancelled();
    }

    public boolean isDone() {
        return inner.isDone();
    }
}

