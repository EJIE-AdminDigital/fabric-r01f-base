package r01f.model;

import java.util.EnumSet;

import r01f.enums.EnumExtended;
import r01f.facets.Summarizable;
import r01f.oids.PersistableObjectOID;

/**
 * All {@link Summarizable} object types ENUM must implement this interface
 * @param <E>
 */
public interface SummarizableObjectType<E extends Enum<E> & SummarizableObjectType<E>>
		 extends EnumExtended<E> {
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the model object oid type
	 * @return
	 */
	public Class<? extends PersistableObjectOID> getOidType();
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends PersistableObjectOID,
				   E extends Enum<E> & SummarizableObjectType<E>> boolean isSummarizableObjectType(final Class<O> oidType,
																								   final Class<E> enumType) {
		boolean out = false;
		for (E o : EnumSet.allOf(enumType)) {
			if (o.getOidType() == oidType) {
				out = true;
				break;
			}
		}
		return out;
	}
	public static <O extends PersistableObjectOID,
				   E extends Enum<E> & SummarizableObjectType<E>> E fromOidType(final Class<O> oidType,
						   												 		final Class<E> enumType) {
		E out = null;
		for (E o : EnumSet.allOf(enumType)) {
			if (o.getOidType() == oidType) {
				out = o;
				break;
			}
		}
		if (out == null) throw new IllegalArgumentException(oidType + " is NOT an oid type for a summarizable object!");
		return out;
	}
}
