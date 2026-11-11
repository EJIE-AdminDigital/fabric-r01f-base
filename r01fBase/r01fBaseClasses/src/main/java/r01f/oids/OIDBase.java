package r01f.oids;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.util.types.Strings;

/**
 * Models an oid by encapsulating an id that can be either a String, an int, a long, etc
 * The OID could be based on an iImmutable OID if it extends {@link OIDBaseImmutable}
 * or on an mutable OID if it extends {@link OIDBaseMutable}
 *
 * {@link OIDBaseMutable} should be used as OID base type if the OID must have a zero-argument
 * constructor. This is the case of objects that must be serialized using GWT serialization
 * mechanism: if a no-arg constructor is missing a compilation error is raised
 *
 * @param <T> the type of the id
 */
@Deprecated	// use records instead of this class
@Immutable
@NoArgsConstructor
public abstract class OIDBase<T extends Comparable<? super T>>
		   implements OIDTyped<T> {

	private static final long serialVersionUID = 3253497735245445342L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZABLE / CLONEABLE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Deprecated
	@Override 
	protected Object clone() throws CloneNotSupportedException {
		if (this.getId() == null) throw new IllegalStateException(String.format("The oid of type %s has NO state",this.getClass()));
		
		Object id = ObjectUtils.clone(this.getId());
		return _createOIDFromString(this.getClass(),
									id.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof OID) {
			OIDTyped<?> oidTyped = (OIDTyped<?>)obj;
			return oidTyped.getRaw().equals(this.getRaw());
		}
		if (obj instanceof String
		 || obj instanceof Number)
			return obj.toString().equals(this.getId().toString());
		return super.equals(obj);
	}
	@Deprecated
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	String conversion
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Override
	public String toString() {
		return this.getId() != null ? this.getId().toString()
									: null;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private static <O extends OID> O _createOIDFromString(final Class<O> oidType,
												   		  final String oidAsString) {
		if (Strings.isNullOrEmpty(oidAsString)) throw new IllegalArgumentException("an oid cannot have a null id");

		O outOid = null;
		try {
			outOid = (O)MethodUtils.invokeStaticMethod(oidType,
				   									   "forId",
				   									   new Object[] {oidAsString},new Class<?>[] {String.class});
		} catch (NoSuchMethodException nsmEx) {
			throw new IllegalArgumentException(String.format("Type %s is NOT a valid OID: it does NOT have a forId(String) static builder method",
															 oidType));
		} catch (Throwable th) {
			th.printStackTrace();
			throw new IllegalStateException(String.format("Could NOT create an OID instance of type %s: %s",oidType,th.getMessage()),
											th);
		}
		return outOid;
	}
}
