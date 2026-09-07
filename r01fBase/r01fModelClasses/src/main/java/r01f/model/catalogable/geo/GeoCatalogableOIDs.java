package r01f.model.catalogable.geo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDs;
import r01f.guids.SuppliesOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePickOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePicksOID;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class GeoCatalogableOIDs {	
/////////////////////////////////////////////////////////////////////////////////////////
//	PICKS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * OID for a label selection in a structure
	 */
	@Immutable
	@MarshallType(as="geoPickOid")
	public record CatalogableGeoPickOID(@Getter String id)
	   implements CatalogablePickOID,
	   			  SuppliesOID {
		public static CatalogableGeoPickOID valueOf(final String s) {
			return CatalogableGeoPickOID.forId(s);
		}
		public static CatalogableGeoPickOID fromString(final String s) {
			return CatalogableGeoPickOID.forId(s);
		}
		public static CatalogableGeoPickOID forId(final String id) {
			return new CatalogableGeoPickOID(id);
		}
		public static CatalogableGeoPickOID supply() {
			return CatalogableGeoPickOID.forId(OIDs.supplyOid());
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
	@Immutable
	@MarshallType(as="geoPicksOid")
	public record CatalogableGeoPicksOID(@Getter String id)
	   implements CatalogablePicksOID,
	   			  SuppliesOID {
		
		public static CatalogableGeoPicksOID valueOf(final String s) {
			return CatalogableGeoPicksOID.forId(s);
		}
		public static CatalogableGeoPicksOID fromString(final String s) {
			return CatalogableGeoPicksOID.forId(s);
		}
		public static CatalogableGeoPicksOID forId(final String id) {
			return new CatalogableGeoPicksOID(id);
		}
		public static CatalogableGeoPicksOID supply() {
			return CatalogableGeoPicksOID.forId(OIDs.supplyOid());
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
