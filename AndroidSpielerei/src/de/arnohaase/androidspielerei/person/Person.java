package de.arnohaase.androidspielerei.person;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;


/**
 * This is an ultra-lightweight 'entity' class. Since reflection is not a best practice on Android (--> older versions
 *  of Dalvik VM and low-end hardware), this is at least a type-safe wrapper around a java.util.Map that is the actual
 *  master for data. It is optimized for minimal resource impact.<br>
 *  
 * This kind of class is a good candidate for code generation.
 * 
 * @author arno
 */
public final class Person implements PersonConstants {
    private Map<String, Object> data;
    
    public Person() {}
    public Person(Map<String, Object> data) {
        setData(data);
    }
    public Person(Cursor cursor) {
        setData(new HashMap<String, Object>());
        for (int i=0; i<cursor.getColumnCount(); i++) {
            if (COL_OID.equals(cursor.getColumnName(i))) {
                data.put(COL_OID, cursor.getLong(i));
            }
            else {
                data.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
    }
    
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Integer getOid() {
        return (Integer) data.get(COL_OID);
    }
    
    public CharSequence getFirstname() {
        return (CharSequence) data.get(COL_FIRSTNAME);
    }
    public void setFirstname(CharSequence firstname) {
        data.put(COL_FIRSTNAME, firstname);
    }
    
    public CharSequence getLastname() {
        return (CharSequence) data.get(COL_LASTNAME);
    }
    public void setLastname(CharSequence lastname) {
        data.put(COL_LASTNAME, lastname);
    }
    
    public Sex getSex() {
        return Sex.valueOf((String) data.get(COL_SEX));
    }
    public void setSex(Sex sex) {
        data.put(COL_SEX, sex.name());
    }
    public CharSequence getStreet() {
        return (CharSequence) data.get(COL_ADDR_STREET);
    }
    public void setStreet(CharSequence street) {
        data.put(COL_ADDR_STREET, street);
    }

    public CharSequence getNo() {
        return (CharSequence) data.get(COL_ADDR_NO);
    }
    public void setNo(CharSequence no) {
        data.put(COL_ADDR_NO, no);
    }
    
    public CharSequence getZip() {
        return (CharSequence) data.get(COL_ADDR_ZIP);
    }
    public void setZip(CharSequence zip) {
        data.put(COL_ADDR_ZIP, zip);
    }
    
    public CharSequence getCity() {
        return (CharSequence) data.get(COL_ADDR_CITY);
    }
    public void setCity(CharSequence city) {
        data.put(COL_ADDR_CITY, city);
    }
    
    public CharSequence getCountry() {
        return (CharSequence) data.get(COL_ADDR_COUNTRY);
    }
    public void setCountry(CharSequence country) {
        data.put(COL_ADDR_COUNTRY, country);
    }
}
