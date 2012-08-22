package de.arnohaase.androidspielerei.person;

import java.util.Map;


public final class Address {
    public static final String KEY_STREET = "street";
    public static final String KEY_NO = "no";
    public static final String KEY_ZIP = "zip";
    public static final String KEY_CITY = "city";
    public static final String KEY_COUNTRY = "country";
    
    private Map<String, Object> data;

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public CharSequence getStreet() {
        return (CharSequence) data.get(KEY_STREET);
    }
    public void setStreet(CharSequence street) {
        data.put(KEY_STREET, street);
    }

    public CharSequence getNo() {
        return (CharSequence) data.get(KEY_NO);
    }
    public void setNo(CharSequence no) {
        data.put(KEY_NO, no);
    }
    
    public CharSequence getZip() {
        return (CharSequence) data.get(KEY_ZIP);
    }
    public void setZip(CharSequence zip) {
        data.put(KEY_ZIP, zip);
    }
    
    public CharSequence getCity() {
        return (CharSequence) data.get(KEY_CITY);
    }
    public void setCity(CharSequence city) {
        data.put(KEY_CITY, city);
    }
    
    public CharSequence getCountry() {
        return (CharSequence) data.get(KEY_COUNTRY);
    }
    public void setCountry(CharSequence country) {
        data.put(KEY_COUNTRY, country);
    }
}



