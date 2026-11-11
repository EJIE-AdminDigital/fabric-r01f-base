package r01f.model.catalogable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OID;

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
	public record CatalogableObjectTypeID(@Getter String id)
	   implements OID {
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
		@Override
		public String asString() {
			return this.getId();
		}
	}
}
