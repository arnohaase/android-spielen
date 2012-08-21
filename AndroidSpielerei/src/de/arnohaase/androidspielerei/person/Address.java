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

    public String getStreet() {
        return (String) data.get(KEY_STREET);
    }
    public void setStreet(String street) {
        data.put(KEY_STREET, street);
    }

    public String getNo() {
        return (String) data.get(KEY_NO);
    }
    public void setNo(String no) {
        data.put(KEY_NO, no);
    }
    
    public String getZip() {
        return (String) data.get(KEY_ZIP);
    }
    public void setZip(String zip) {
        data.put(KEY_ZIP, zip);
    }
    
    public String getCity() {
        return (String) data.get(KEY_CITY);
    }
    public void setCity(String city) {
        data.put(KEY_CITY, city);
    }
    
    public String getCountry() {
        return (String) data.get(KEY_COUNTRY);
    }
    public void setCountry(String country) {
        data.put(KEY_COUNTRY, country);
    }
}



