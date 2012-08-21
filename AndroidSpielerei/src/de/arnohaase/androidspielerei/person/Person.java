package de.arnohaase.androidspielerei.person;

import java.util.HashMap;
import java.util.Map;


/**
 * This is an ultra-lightweight 'entity' class. Since reflection is not a best practice on Android (--> older versions
 *  of Dalvik VM and low-end hardware), this is at least a type-safe wrapper around a java.util.Map that is the actual
 *  master for data. It is optimized for minimal resource impact.<br>
 *  
 * This kind of class is a good candidate for code generation.
 * 
 * @author arno
 */
public final class Person {
    public static final String KEY_FIRSTNAME="firstname";
    public static final String KEY_LASTNAME="lastname";
    public static final String KEY_SEX="sex";

    public static final String KEY_ADDRESS="address";
    
    private Map<String, Object> data;
    
    private final Address address = new Address();

    public Person() {}
    public Person(Map<String, Object> data) {
        setData(data);
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public void setData(Map<String, Object> data) {
        this.data = data;
        if (! data.containsKey(KEY_ADDRESS)) {
            data.put(KEY_ADDRESS, new HashMap<String, Object>());
        }
        address.setData((Map<String, Object>) data.get(KEY_ADDRESS));
    }

    public String getFirstname() {
        return (String) data.get(KEY_FIRSTNAME);
    }
    public void setFirstname(String firstname) {
        data.put(KEY_FIRSTNAME, firstname);
    }
    
    public String getLastname() {
        return (String) data.get(KEY_LASTNAME);
    }
    public void setLastname(String lastname) {
        data.put(KEY_LASTNAME, lastname);
    }
    
    public Sex getSex() {
        return Sex.valueOf((String) data.get(KEY_SEX));
    }
    public void setSex(Sex sex) {
        data.put(KEY_SEX, sex.name());
    }
    
    public Address getAddress() {
        return address;
    }
}
