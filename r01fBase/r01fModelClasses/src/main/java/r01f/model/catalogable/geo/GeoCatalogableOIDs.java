package r01f.model.catalogable.geo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDs;
import r01f.guids.SuppliesOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePickOIDBase;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePicksOIDBase;
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
	@NoArgsConstructor
	public static final class CatalogableGeoPickOID
					  extends CatalogablePickOIDBase
				   implements SuppliesOID {
		private static final long serialVersionUID = -2727859776371512718L;
		public CatalogableGeoPickOID(final String oid) {
			super(oid);
		}
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
	}
	@Immutable
	@MarshallType(as="geoPicksOid")
	@NoArgsConstructor
	public static final class CatalogableGeoPicksOID
			   		  extends CatalogablePicksOIDBase
				   implements SuppliesOID {
		
		private static final long serialVersionUID = -2727859776371512718L;
		
		public CatalogableGeoPicksOID(final String oid) {
			super(oid);
		}
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
	}
}
