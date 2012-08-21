package de.arnohaase.androidspielerei.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

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

    public Future<List<String>> getCountryAutoCompletions(final CharSequence partialText) {
        final String prefix = partialText.toString().toLowerCase();
        
        return new AndroidFutureTask<List<String>>(resultCallbackExecutor, null) {
            @Override
            public List<String> call() throws Exception {
                final SortedSet<String> candidates = new TreeSet<String>(Arrays.asList("Germany", "Georgia", "Gibraltar", "Great Britian", "France"));
                
                for (Map<String, Object> person: doFindAllPersons()) {
                    candidates.add(String.valueOf(person.get(Address.KEY_COUNTRY)));
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
    
    protected List<Map<String, Object>> doFindAllPersons() {
        // AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android Probieren");
        // JSON

        simulateDelay();
        
        final List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        
        for (int i=0; i<20; i++) {
            result.add(createPerson(i));
        }
        
        return result;
        
    }
    
    private Map<String, Object> createPerson(int idx) {
        final boolean male = idx % 2 == 0; 

        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("oid", idx);
        
        final Person p = new Person(result);
        p.setFirstname(male ? "Arno " + idx : "Testa" + idx);
        p.setLastname(male ? "Haase" : "Testarossa");
        p.setSex(male ? Sex.m : Sex.f);
        p.getAddress().setStreet("Sesame Street");
        p.getAddress().setNo("" + idx);
        p.getAddress().setZip("12345");
        p.getAddress().setCity("Dodge City");
        p.getAddress().setCountry("Germany");
        
        return result; 
    }
}
