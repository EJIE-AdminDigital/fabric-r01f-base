package r01f.guids;

import java.util.Arrays;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;

/**
 * Models an oid
 */
@MarshallPolymorphicTypeInfo(includeTypeInfo = MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE)	// this is important!! (see MarshallerPolymorphicTypes.java)
public interface OID
		 extends Comparable<OID>,
	 			 Cloneable,
	 			 CanBeRepresentedAsString {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int OID_LENGTH = 100;

	public static final String STATIC_FACTORY_METHOD_NAME = "forId";
	public static final String STATIC_SUPPLIER_METHOD_NAME = "supply";
	public static final String REGEX_NOCAPTURE = "[0-9A-Za-z_-]+";
	public static final String REGEX_CAPTURE = "(" + REGEX_NOCAPTURE + ")";
/////////////////////////////////////////////////////////////////////////////////////////
//	is
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if this oid is the same as the provided one
	 * (its similar to equals method but more 'semantic')
	 * @param other
	 * @return true if the two oids are the same oid
	 */
	public default <O extends OID> boolean is(final O other) {
		return this.equals(other);
	}
	/**
	 * Checks if this oid is NOT equal to the provided one
	 * @param other
	 * @return
	 */
	public default <O extends OID> boolean isNOT(final O other) {
		return !this.is(other);
	}
	/**
	 * Checks if this oid is included in the provided collection
	 * @param oids
	 * @return true if the oid is included in the collection, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public default <O extends OID> boolean isContainedIn(final O... oids) {
		return oids != null ? this.isContainedIn(Arrays.asList(oids))
							: false;
	}
	/**
	 * Checks if this oid is included in the provided collection
	 * @param oids
	 * @return true if the oid is included in the collection, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public default <O extends OID> boolean isIn(final O... oids) {
		return this.isContainedIn(oids);
	} 
	/**
	 * Checks if this oid is NOT included in the provided collection
	 * @param oids
	 * @return true if the oid is NOT included in the collection, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public default <O extends OID> boolean isNOTContainedIn(final O... oids) {
		return !this.isContainedIn(oids);
	}
	/**
	 * Checks if this oid is NOT included in the provided collection
	 * @param oids
	 * @return true if the oid is NOT included in the collection, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public default <O extends OID> boolean isNOTIn(final O... oids) {
		return this.isNOTContainedIn(oids);
	}
	/**
	 * Checks if this oid is included in the provided collection
	 * @param oids
	 * @return true if the oid is included in the collection, false otherwise
	 */
	public default <O extends OID> boolean isContainedIn(final Iterable<O> oids) {
		if (oids == null) {
			return false;
		}

		boolean outIsContained = false;
		for (O oid : oids) {
			if (oid.is(this)) {
				outIsContained = true;
				break;
			}
		}
		return outIsContained;
	}
	/**
	 * Checks if this oid is included in the provided collection
	 * @param oids
	 * @return true if the oid is included in the collection, false otherwise
	 */
	public default <O extends OID> boolean isIn(final Iterable<O> oids) {
		return this.isContainedIn(oids);
	}
	/**
	 * Checks if this oid NOT is included in the provided collection
	 * @param oids
	 * @return true if the oid is NOT included in the collection, false otherwise
	 */
	public default <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids) {
		return !this.isContainedIn(oids);
	}
	/**
	 * Checks if this oid NOT is included in the provided collection
	 * @param oids
	 * @return true if the oid is NOT included in the collection, false otherwise
	 */
	public default <O extends OID> boolean isNOTIn(final Iterable<O> oids) {
		return this.isNOTContainedIn(oids);
	}
	/**
	 * Checks if this oid is the same as the provided one ignoring the case
	 * @param <O>
	 * @param other
	 * @return
	 */
	@SuppressWarnings("null")
	public default <O extends OID> boolean isIgnoringCase(final O other) {
		if (other == null) {
			return false;
		}

		String thisStr = this.asString();
		String otherStr = other.asString();
		if (thisStr == null && otherStr == null) return true;
		if (thisStr != null && otherStr == null) return false;
		if (thisStr == null && otherStr != null) return false;
		if (thisStr.equalsIgnoreCase(otherStr)) return true;
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STRING CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends OID> String asStringOrNull(final O oid) {
		return oid != null ? oid.asString()
						   : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public default int compareTo(final OID other) {
		return OID.compareOIDs(this,other);
	}
	public static int compareOIDs(final OID oid1,final OID oid2) {
		int outCompare = 0;

		if (oid1 instanceof OIDTyped oidTyped1
		 && oid2 instanceof OIDTyped oidTyped2) {
			// ensure ids are being compared
			// might throw if ids are not the same type
			outCompare = _compareIds(oidTyped1.getId(),oidTyped2.getId());
		} else {
			// fallback
			outCompare = oid1.asString().compareTo(oid2.asString());
		}
		return outCompare;
	}
	@SuppressWarnings("unchecked")
	private static <T> int _compareIds(final T id1,final T id2) {
		return id1 instanceof Comparable
			&& id2 instanceof Comparable
				  		? ((Comparable<T>)id1).compareTo(id2)
				  		: id1.toString().compareTo(id2.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the oid is valid
	 */
	public default boolean isValid() {
		return Strings.isNOTNullOrEmpty(this.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the oid typed
	 */
	@SuppressWarnings("unchecked")
	public default <O extends OID> O cast() {
		return (O)this;
	}
}
