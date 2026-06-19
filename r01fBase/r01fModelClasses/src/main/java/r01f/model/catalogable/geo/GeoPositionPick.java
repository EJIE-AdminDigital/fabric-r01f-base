package r01f.model.catalogable.geo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasOID;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPickOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoIDs.GeoCountryID;
import r01f.types.geo.GeoIDs.GeoCountyID;
import r01f.types.geo.GeoIDs.GeoDistrictID;
import r01f.types.geo.GeoIDs.GeoLocalityID;
import r01f.types.geo.GeoIDs.GeoMunicipalityID;
import r01f.types.geo.GeoIDs.GeoNeighborhoodID;
import r01f.types.geo.GeoIDs.GeoRegionID;
import r01f.types.geo.GeoIDs.GeoStateID;
import r01f.types.geo.GeoIDs.GeoStreetID;
import r01f.types.geo.GeoIDs.GeoTerritoryID;
import r01f.types.geo.GeoIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoPositionBase;


/**
 * index data about a geographical point > just ids, NO texts
 * <pre>
 *		- x,y
 * Territory											Europe
 *   |_Country											Spain										
 *   	 |_State										Euskadi
 *   		 |_County									Bizkaia
 *   		 	|_Region								Gran Bilbao / valles alaveses
 *   				|_Municipality						Bilbao
 *  					|_Locality						Bilbao	
 *   						|_District					01	
 *   							|_Neighborhood 			Abando
 *   								|_Street			General Concha
 *   									|_portal		12
 * </pre>
 * Usage:
 * <pre class='brush:java'>
 *		GeoPositionPick pos = GeoPositionPick.createNew()
 *							 	.withPosition(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE))	
 *							 	.withCountry(GeoCountry.create(GeoCountryID.forId(34));
 * </pre>
 */
@MarshallType(as="geoPositionPick")
@Accessors(prefix="_")
public class GeoPositionPick
	 extends GeoPositionBase
  implements HasOID<CatalogableGeoPickOID> {
	
	private static final long serialVersionUID = 2167095531559768127L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * pick oid
	 */
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private CatalogableGeoPickOID _oid;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPositionPick() {
		// default no-args constructor
	}
	public GeoPositionPick(final GeoTerritoryID territory,final GeoCountryID country,final GeoStateID state,final GeoCountyID county,final GeoRegionID region,
					       final GeoMunicipalityID municipality,final GeoLocalityID locality,final GeoDistrictID district,final GeoNeighborhoodID neighborhood,final GeoStreetID street,
					       final GeoZipCode zipCode,
					       final GeoPosition2D position) {
		super(territory,country,state,county,region,municipality,locality,district,neighborhood,street,
			  zipCode,
			  position);
	}
	public GeoPositionPick(final GeoPositionBase geo) {
		super(geo);
	}
	public static GeoPositionPickBuilder createNew() {
		GeoPositionPick outPos = new GeoPositionPick();
		return new GeoPositionPickBuilder(outPos);
	}
	public static GeoPositionPick from(final GeoPositionBase geo) {
		return new GeoPositionPick(geo);
	}
	public static GeoPositionPick valueOf(final String str) {
		return GeoPositionPick.fromString(str);
	}
	public static GeoPositionPick from(final String str) {
		return GeoPositionPick.fromString(str);
	}
	public static GeoPositionPick fromString(final String str) {
		return GeoPositionBase.from(str,
									GeoPositionPick::new);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	public static class GeoPositionPickBuilder
				extends GeoPositionBaseBuilder<GeoPositionPick> {
		
		private GeoPositionPickBuilder(final GeoPositionPick geoPos) {
			super(geoPos);
		}
		public GeoPositionPickBuilder withOid(final CatalogableGeoPickOID oid) {
			_geoPos.setOid(oid);
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition asGeoPosition() {
		return new GeoPosition(this);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		boolean superEqs = super.equals(o);
		if (!superEqs) return false;
		if (!(o instanceof GeoPositionPick)) return false;
		
		GeoPositionPick other = (GeoPositionPick)o;
		boolean oidEqs = this.getOid() != null & other.getOid() != null ? this.getOid().is(other.getOid())
																	    : this.getOid() != null ? false
																	    						: true;	// both null
		return oidEqs;
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
