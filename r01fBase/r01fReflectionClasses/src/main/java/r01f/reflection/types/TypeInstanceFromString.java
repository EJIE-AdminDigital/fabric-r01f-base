package r01f.reflection.types;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import r01f.oids.OID;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.datetime.DateTimeMarshaller;


public class TypeInstanceFromString {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates an instance of a type from it {@link String} value
     * @param valueStr
     * @param dataType
     * @return
     */
    @SuppressWarnings({ "unchecked","rawtypes" })
    public static <T> T instanceFrom(final String valueStr,
                                     final Class<T> dataType) {
        if (Strings.isNullOrEmpty(valueStr)) {
            return dataType != Boolean.class ? null
                                             : (T)Boolean.valueOf(false);
        }

        T value = null;
        ////////// Numeric
        if (dataType == Boolean.class) {
            value = (T)Boolean.valueOf(valueStr);
        }
        else if (dataType == Integer.class) {
            value = (T)Integer.valueOf(valueStr);
        }
        else if (dataType == Long.class) {
            value = (T)Long.valueOf(valueStr);
        }
        else if (dataType == Double.class) {
            value = (T)Double.valueOf(valueStr);
        }
        else if (dataType == Float.class) {
            value = (T)Float.valueOf(valueStr);
        }
        ////////// String
        else if (dataType == String.class) {
            value = (T)valueStr;
        }
        else if (dataType == Character.class) {
            value = (T)Character.valueOf(valueStr.charAt(0));
        }
        else if (dataType != null && ReflectionUtils.isSubClassOf(dataType,CanBeRepresentedAsString.class)) {
            value = (T)ReflectionUtils.createInstanceFromString(dataType,valueStr);
        }
        ////////// DateTime
        else if (dataType == Date.class) {
        	Instant instant = TypeInstanceFromString.instanceFrom(valueStr,
        														  Instant.class);
            value = instant != null ? (T)Date.from(instant) : null;
        }
        else if (dataType == Instant.class) {
        	value = (T)DateTimeMarshaller.forUnMarshallingFrom(valueStr)
        							  	 .asInstantAsummingISO8601OrEPOCHMillis()
        							  	 .orElse(null);
        }
        else if (dataType == LocalDate.class) {
        	value = (T)DateTimeMarshaller.forUnMarshallingFrom(valueStr)
        								 .asLocalDateAsummingISO8601Format()
        								 .orElse(null);
        }
        else if (dataType == LocalDateTime.class) {
        	value = (T)DateTimeMarshaller.forUnMarshallingFrom(valueStr)
        								 .asLocalDateTimeAsummingISO8601Format()
        								 .orElse(null);
        }
        else if (dataType == LocalTime.class) {
        	value = (T)DateTimeMarshaller.forUnMarshallingFrom(valueStr)
        								 .asLocalTimeAsummingISO8601Format()
        								 .orElse(null);
        }
        else if (dataType == OffsetDateTime.class) {
        	value = (T)DateTimeMarshaller.forUnMarshallingFrom(valueStr)
        								 .asOffsetDateTimeAsummingISO8601Format()
        								 .orElse(null);
        }
        else if (dataType == ZonedDateTime.class) {
        	value = (T)DateTimeMarshaller.forUnMarshallingFrom(valueStr)
        								 .asZonedDateTimeAsummingISO8601Format()
        								 .orElse(null);
        }
        ////////// Enum
        else if (dataType != null && ReflectionUtils.isSubClassOf(dataType,Enum.class)) {
            value = (T)Enum.valueOf((Class<? extends Enum>)dataType,valueStr);
        }
        ////////// OID
        else if (dataType != null && ReflectionUtils.isSubClassOf(dataType,OID.class)) {
            value = (T)ReflectionUtils.invokeStaticMethod(dataType,
                                                          OID.STATIC_FACTORY_METHOD_NAME,new Class<?>[] {String.class},new Object[] {valueStr});
        } else {
            throw new IllegalArgumentException("unsupported datatype: " + dataType.getName());
        }
        return value;
    }
    public static <T> T instanceFrom(final String valueStr,
                                     final Class<T> dataType,
                                     final Function<String,T> ifFail) {
        T value = TypeInstanceFromString.instanceFrom(valueStr,
                                                       dataType);
        if (value == null && ifFail != null) value = ifFail.apply(valueStr);
        return value;
    }
    @SuppressWarnings({ "unchecked","rawtypes" })
    public static <T> T[] arrayFrom(final String text,
                                    final Class<T> dataType) {
        String[] arrayItems = text.split(",");
        T[] spectrum = null;
        ////////// Numeric
        if (dataType == Integer.class) {
            spectrum = (T[])new Integer[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Integer.valueOf(arrayItems[i]);
        }
        else if (dataType == Long.class) {
            spectrum = (T[])new Long[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Long.valueOf(arrayItems[i]);
        }
        else if (dataType == Double.class) {
            spectrum = (T[])new Double[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Double.valueOf(arrayItems[i]);
        }
        else if (dataType == Float.class) {
            spectrum = (T[])new Float[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Float.valueOf(arrayItems[i]);
        }
        ////////// String
        else if (dataType == String.class) {
            spectrum = (T[])new String[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)arrayItems[i];

        }
        else if (dataType == Character.class) {
            spectrum = (T[])new Character[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Character.valueOf(arrayItems[i].charAt(0));
        }
        ////////// DateTime
        else if (dataType == Date.class) {
            spectrum = (T[])new Date[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asInstantAsummingISO8601OrEPOCHMillis()
            																		   .map(Date::from)
            																		   .orElse(null);
        }
        else if (dataType == Instant.class) {
            spectrum = (T[])new Instant[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asInstantAsummingISO8601OrEPOCHMillis()
            																		   .orElse(null);
        }
        else if (dataType == LocalDate.class) {
            spectrum = (T[])new LocalDate[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asLocalDateAsummingISO8601Format()
            																		   .orElse(null);
        }
        else if (dataType == LocalDateTime.class) {
            spectrum = (T[])new LocalDateTime[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asLocalDateTimeAsummingISO8601Format()
            																		   .orElse(null);
        }
        else if (dataType == LocalTime.class) {
            spectrum = (T[])new LocalDateTime[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asLocalTimeAsummingISO8601Format()
            																		   .orElse(null);
        }
        else if (dataType == OffsetDateTime.class) {
            spectrum = (T[])new OffsetDateTime[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asOffsetDateTimeAsummingISO8601Format()
            																		   .orElse(null);
        }
        else if (dataType == ZonedDateTime.class) {
            spectrum = (T[])new ZonedDateTime[arrayItems.length];
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)DateTimeMarshaller.forUnMarshallingFrom(arrayItems[i])
            																		   .asZonedDateTimeAsummingISO8601Format()
            																		   .orElse(null);
        }
        ////////// OID
        else if (ReflectionUtils.isSubClassOf(dataType,OID.class)) {
            spectrum = (T[])Array.newInstance(dataType,arrayItems.length);
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)ReflectionUtils.invokeStaticMethod(dataType,
                                                                                                        OID.STATIC_FACTORY_METHOD_NAME,new Class<?>[] {String.class},new Object[] {arrayItems[i]});
        }
        ////////// Enum
        else if (ReflectionUtils.isSubClassOf(dataType,Enum.class)) {
            spectrum = (T[])Array.newInstance(dataType,arrayItems.length);
            for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Enum.valueOf((Class<? extends Enum>)dataType,arrayItems[i]);
        } else {
            throw new IllegalArgumentException("unsupported datatype: " + dataType.getName());
        }
        return spectrum;
    }
    @SuppressWarnings({ "rawtypes" })
    public static <T> Collection<T> collectionFrom(final String text,
                                                    final Class<T> dataType,
                                                    final Class<? extends Collection> colType) {
        // Get an array of values
        T[] values = TypeInstanceFromString.arrayFrom(text,
                                                      dataType);
        // Get the collection type
        Collection<T> outCol = null;
        if (colType == List.class) {
            outCol = ReflectionUtils.createInstanceOf(ArrayList.class);
        } else if (colType == Set.class) {
            outCol = ReflectionUtils.createInstanceOf(HashSet.class);
        } else {
            throw new IllegalArgumentException("Not a valid Collection type");
        }
        // Add all items
        if (CollectionUtils.hasData(values)) {
            for (T val : values) outCol.add(val);
        }
        return outCol;
    }
}
