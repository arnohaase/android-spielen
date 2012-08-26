package de.arnohaase.androidspielerei.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import de.arnohaase.androidspielerei.person.Person;
import de.arnohaase.androidspielerei.person.PersonConstants;
import de.arnohaase.androidspielerei.person.Sex;
import de.arnohaase.androidspielerei.util.AndroidFutureTask;
import de.arnohaase.androidspielerei.util.AsyncOperationFinishedListener;


/**
 * This class serves as a 'single point of contact' for all data access to person related data
 *  (not only queries but all of CRUD).<br>
 *  
 * It encapsulates the underlying storage (in-memory dummy, other app, remote on a server, ...).
 * 
 * @author arno
 */
public class PersonAccessor {
    private final Executor resultCallbackExecutor;

    private static final Map<Long, Map<String, Object>> allPersons = new ConcurrentHashMap<Long, Map<String,Object>>();
    static {
        for (int idx=0; idx<20; idx++) {
            final boolean male = idx % 2 == 0; 

            final Map<String, Object> result = new HashMap<String, Object>();
            result.put("oid", (long) idx);
            
            final Person p = new Person(result);
            p.setFirstname(male ? "Arno " + idx : "Testa" + idx);
            p.setLastname(male ? "Haase" : "Testarossa");
            p.setSex(male ? Sex.m : Sex.f);
            p.setStreet("Sesame Street");
            p.setNo("" + idx);
            p.setZip("12345");
            p.setCity("Dodge City");
            p.setCountry("Germany");

            allPersons.put(Long.valueOf(idx), result);
        }
    }
    
    
    /**
     * @param resultCallbackExecutor the executor that is used to perfom result callbacks. For convenience, use one of
     *  the implementations from <code>ExecutorHelper</code>
     */
    public PersonAccessor(Executor resultCallbackExecutor) {
        this.resultCallbackExecutor = resultCallbackExecutor;
    }
    
    //TODO factor out common code --> AbstractAccessor (?)
    
    public Future<List<Map<String, Object>>> findAllPersons(final AsyncOperationFinishedListener<List<Map<String, Object>>> finishedCallback) {
        return new AndroidFutureTask<List<Map<String,Object>>>(resultCallbackExecutor, finishedCallback) {
            @Override
            public List<Map<String, Object>> call() throws Exception {
                return doFindAllPersons();
            }
        }.startInNewThread();
    }

    public Future<Boolean> savePerson(Map<String, Object> data, final AsyncOperationFinishedListener<Boolean> finishedCallback) {
        // copy the data to be independent of concurrent changes on the caller side
        final Map<String, Object> dataSnapshot = new HashMap<String, Object>(data);
        
        return new AndroidFutureTask<Boolean>(resultCallbackExecutor, finishedCallback) {
            @Override
            public Boolean call() throws Exception {
                return doSavePerson(dataSnapshot);
            }
        }.startInNewThread();
    }
    
    private boolean doSavePerson(Map<String, Object> data) {
        simulateDelay();
        
        final Long oid = (Long) data.get(PersonConstants.COL_OID);
        allPersons.put(oid, data);
        
        return true;
    }
    
    /**
     * @return success
     */
    public Future<Boolean> deletePerson(final long oid, final AsyncOperationFinishedListener<Boolean> finishedCallback) {
        return new AndroidFutureTask<Boolean>(resultCallbackExecutor, finishedCallback) {
            @Override
            public Boolean call() throws Exception {
                return doDeletePerson(oid);
            }
        }.startInNewThread();
    }
    
    public Future<List<String>> getCountryAutoCompletions(final CharSequence partialText) {
        final String prefix = partialText.toString().toLowerCase();
        
        return new AndroidFutureTask<List<String>>(resultCallbackExecutor, null) {
            @Override
            public List<String> call() throws Exception {
                final SortedSet<String> candidates = new TreeSet<String>(Arrays.asList("Germany", "Georgia", "Gibraltar", "Great Britian", "France"));
                
                for (Map<String, Object> person: doFindAllPersons()) {
                    final CharSequence country = new Person(person).getCountry();
                    if (country != null) {
                        candidates.add(String.valueOf(country));
                    }
                }
        
                final List<String> result = new ArrayList<String>();
                for (String candidate: candidates) {
                    if (candidate.toLowerCase().startsWith(prefix)) {
                        result.add(candidate);
                    }
                }

                return result;
            }
        }.startInNewThread();
    }
    
    private void simulateDelay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    boolean doDeletePerson(long oid) {
        simulateDelay();
        
        allPersons.remove(oid);
        
        return true;
    }
    
    List<Map<String, Object>> doFindAllPersons() {
        // AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android Probieren");
        // JSON

        simulateDelay();

        final List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        
        for (Map<String, Object> orig: allPersons.values()) {
            result.add(new HashMap<String, Object>(orig));
        }
        
        return result;
    }
}
