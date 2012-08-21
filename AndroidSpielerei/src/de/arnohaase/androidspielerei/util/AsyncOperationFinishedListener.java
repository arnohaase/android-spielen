package de.arnohaase.androidspielerei.util;


/**
 * This callback interface allows implementation of service methods that support both a synchronous
 *  and an asynchronous call model
 * 
 * @author arno
 */
public interface AsyncOperationFinishedListener<T> {
    void onSuccess(T result);
    void onFailure(Exception reason);
    void onCancelled();
}
