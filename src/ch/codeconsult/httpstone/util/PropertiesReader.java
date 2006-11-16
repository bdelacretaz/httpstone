package ch.codeconsult.httpstone.util;

import java.util.Properties;

/** Wrap Properties with "required value" functions
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */
 
public class PropertiesReader {
    private final Properties props;

    public static class MissingPropertyException extends Exception {
        MissingPropertyException(String key) {
            super("Missing configuration property '" + key + "'");
        }
    }

    public static class TypeConversionException extends Exception {
        TypeConversionException(String key,String value,Class desiredType) {
            super("For property '" + key + "', cannot convert value '" + value + "' to " + desiredType.getName());
        }
    }

    public PropertiesReader(Properties p) {
        props = p;
    }

    public boolean hasProperty(String key) {
      return props.containsKey(key);
    }
    
    public String getRequiredString(String key) throws MissingPropertyException {
        final String result = props.getProperty(key);
        if(result==null) throw new MissingPropertyException(key);
        return result.trim();
    }

    public int getRequiredInt(String key) throws MissingPropertyException,TypeConversionException {
        final String value = getRequiredString(key);
        int result = 0;
        try {
            result = Integer.valueOf(value).intValue();
        } catch(Exception e) {
            throw new TypeConversionException(key,value,Integer.class);
        }
        return result;
    }
}
