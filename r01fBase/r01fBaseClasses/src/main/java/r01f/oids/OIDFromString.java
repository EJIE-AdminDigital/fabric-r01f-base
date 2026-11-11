package r01f.oids;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import r01f.exceptions.Throwables;
import r01f.patterns.FactoryFrom;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OIDFromString {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates an OID from it's serialized as {@link String} value
	 * @param oidType
	 * @param oidAsString
	 * @return
	 */
	public static <O extends OID> O createOIDFromString(final String oidAsString,
														final FactoryFrom<String,O> oidFactory) {
		return oidFactory.from(oidAsString);
	}
	/**
	 * Creates an OID factory from an String that uses reflection to create the OID instance
	 * @param <O>
	 * @param oidType
	 * @return
	 */
	public static <O extends OID> FactoryFrom<String,O> createOIDFactoryFromStringFor(final Class<O> oidType) {
		return oidAsString -> {
					O outOid = null;
					try {
						outOid = _createInstanceFromString(oidType,oidAsString);
					} catch (Throwable th) {
						th.printStackTrace(System.out);
					}
					if (outOid == null) outOid = _invokeStaticMethod(oidType,
																   	 "forId",
																   	 new Class<?>[] {String.class},
																   	 new Object[] {oidAsString});
					return outOid;
			   };
	}
	/**
	 * Creates an OID from it's serialized as {@link String} value
	 * @param oidType
	 * @param oidAsString
	 * @return
	 */
	public static <O extends OID> O createOIDFromString(final Class<O> oidType,
														final String oidAsString) {
		return OIDFromString.createOIDFactoryFromStringFor(oidType)
				   			.from(oidAsString);
	}
	/**
	 * Creates a versionable OID from it's serialized as {@link String}s value
	 * @param oidType
	 * @param versionIndependentOidAsString
	 * @param versionOidAsString
	 * @return
	 */
	public static <O extends OID> O createVersionableOIDFromString(final Class<O> oidType,
																   final String versionIndependentOidAsString,
																   final String versionOidAsString) {
		O outOid = _invokeStaticMethod(oidType,
									   "forIds",
									   new Class<?>[] {String.class,String.class},
									   new Object[] {versionIndependentOidAsString,versionOidAsString});
		return outOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COPIED FROM ReflectionUtils :-(
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a type's instance from a {@link String}
	 * It uses a secuential approach:
	 * <ul>
	 * 		<li>First it tries to use a single String param constructor</li>
	 * 		<li>Second it tries to use a static valueOf(String) method</li>
	 * </ul>
	 * @param type
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> T _createInstanceFromString(final Class<?> type,
										 		   final String value) {
		Object newObj = null;
		// [1] - Try to use the single String param constructor (if it exists)
		try {
			Constructor<?> constructor = type.getDeclaredConstructor(new Class<?>[] {String.class}); 	// Constructor from string
			if (constructor != null) newObj = constructor.newInstance(new Object[] {value});
		} catch (Throwable th) {
			th.printStackTrace(System.out);
		}
		// [2] - Try to use a static valueOf method
		if (newObj == null) {
			try {
				Method valueOfMethod = _staticMethod(type,
											 		 "valueOf",
											 		 new Class<?>[] {String.class});
				if (valueOfMethod != null)  {
					newObj = _invokeStaticMethod(type,
												 valueOfMethod,
												 new Object[] {value});
				} else {
					throw new UnsupportedOperationException("cannot create a " + type + " from a String because neither the single String param constructor exists, nor the static valueOf(String) method");
				}
			} catch (Throwable th) {
				th.printStackTrace(System.out);
			}
		}
		return (T)newObj;
	}
	/**
	 * Returns a STATIC method at a type
	 * @param type 
	 * @param methodName method name
	 * @param argsTypes method args types
	 */
	private static Method _staticMethod(final Class<?> type,
									  	final String methodName,final Class<?>... paramTypes) {
		Method outMethod = null;
		for (Class<?> t = type; t != Object.class; t = t.getSuperclass()) {
			if (t == null) break;
			try  {
				Method m = t.getDeclaredMethod(methodName,paramTypes != null ? paramTypes
																			 : new Class[] {});
				if (m != null) {
					outMethod = m;
					break;
				}
			} catch (NoSuchMethodException nsmEx) {
				/* Ignore */
			}
		}
		return outMethod;
	}
	/**
	 * Invokes an static method in a type
	 * @param type
	 * @param methodName
	 * @param argsTypes
	 * @param argsValues
	 * @return the method-returned object
	 */
	private static <T> T _invokeStaticMethod(final Class<?> type,
										     final String methodName,final Class<?>[] argsTypes,final Object[] argsValues) {
		Method method = _methodMatchingParamTypes(type,methodName,argsTypes);
		return _invokeStaticMethod(type,method,argsValues);
	}
	/**
	 * Invokes an static method in a type
	 * @param
	 * @param method
	 * @param argsValues
	 * @return the method-returned object
	 */
	@SneakyThrows
	@SuppressWarnings("unchecked")
	private static <T> T _invokeStaticMethod(final Class<?> type,
										   	 final Method method,final Object... argsValues) {
		if (type == null || method == null) throw new IllegalArgumentException(Throwables.message("Cannot invoke {} method on null object instance",
																								  (method != null ? method : "null"),
																								  (type != null ? type : "null")));
	   return (T)MethodUtils.invokeStaticMethod(type,
			   									method.getName(),
			   									argsValues,method.getParameterTypes());
	}
	/**
	 * Finds a method looking in all object's hierarchy
	 * PROBLEM:	class.getMethods() returns just PUBLIC methods
	 *			class.getDeclaredMethods() returns PUBLIC & PRIVATE methods of a given type
	 *									   but DOES NOT return method on super-types
	 * @param type 
	 * @param methodName 
	 * @param paramTypes 
	 * @return the method or null if it was NOT found
	 */
	private static Method _methodMatchingParamTypes(final Class<?> type,
									  			  	final String methodName,final Class<?>... paramTypes) {
		Method outMethod = null;
		for (Class<?> t = type; t != Object.class; t = t.getSuperclass()) {
			if (t == null) break;
			try  {
				Method m = t.getDeclaredMethod(methodName,paramTypes != null ? paramTypes
																			 : new Class[] {});
				if (m != null) {
					outMethod = m;
					break;
				}
			} catch (NoSuchMethodException nsmEx) {
				/* Ignore */
			}
		}
		return outMethod;
	}
}
