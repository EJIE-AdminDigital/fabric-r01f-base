package r01f.model.catalogable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.SuppliesOID;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Catalog oids
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CatalogableIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  picks
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * OID for a geo selection
	 */
	@Immutable
	@MarshallType(as="catalogableObjectTypeId")
	@NoArgsConstructor
	public static final class CatalogableObjectTypeID
					  extends OIDBaseMutable<String>
			       implements SuppliesOID {
		private static final long serialVersionUID = -2727859776371512718L;
		public CatalogableObjectTypeID(final String oid) {
			super(oid);
		}
		public static CatalogableObjectTypeID valueOf(final String s) {
			return CatalogableObjectTypeID.forId(s);
		}
		public static CatalogableObjectTypeID fromString(final String s) {
			return CatalogableObjectTypeID.forId(s);
		}
		public static CatalogableObjectTypeID forId(final String id) {
			return new CatalogableObjectTypeID(id);
		}
		public static <E extends Enum<E>> CatalogableObjectTypeID from(final E enumVal) {
			return CatalogableObjectTypeID.forId(enumVal.name());
		}
	}
}
