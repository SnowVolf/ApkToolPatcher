package ru.svolf.melissa.compat;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

/**
 *  on 16.09.2016.
 */
public class PropertiesCompat extends Properties {
    public Set<String> stringPropertyNamesSupport() {
        return stringPropertyNames();
    }

    private synchronized void enumerateStringPropertiesSupport(Hashtable<String, String> h) {
        if (defaults != null) {
            ((PropertiesCompat) defaults).enumerateStringPropertiesSupport(h);
        }
        for (Enumeration e = keys(); e.hasMoreElements(); ) {
            Object k = e.nextElement();
            Object v = get(k);
            if (k instanceof String && v instanceof String) {
                h.put((String) k, (String) v);
            }
        }
    }
}
