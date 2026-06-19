package r01f.types.geo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

/**
 * Filter about a geographical point
 * 
 * Extends {@link GeoPositionBase} with some textual data
 * 
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
 *		- Textual info
 * </pre>
 * Usage:
 * <pre class='brush:java'>
 *		GeoPositionFilter pos = GeoPositionFilter.createNew()
 *							 					 .withPosition(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE))	
 *							 					 .withCountry(GeoCountryID.forId(34)) 
 *	 						 					 .positionedAt(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE)));
 * </pre>
 */
@MarshallType(as="geoPositionFilter")
@Accessors(prefix="_")
public class GeoPositionFilter
     extends GeoPositionBase {

	private static final long serialVersionUID = 1793628866355724523L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Distance from point meters
	 */
	@MarshallField(as="distanceFromPointMeters")
	@Getter @Setter private int _distanceFromPointMeters;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPositionFilter() {
		// default no-args constructor
	}
	public GeoPositionFilter(final GeoPositionBase pos) {
		super(pos);
	}
	public GeoPositionFilter(final GeoPositionFilter other) {
		super(other);
		_distanceFromPointMeters = other.getDistanceFromPointMeters();
	}
	public static GeoPositionFilterBuilder createNew() {
		GeoPositionFilter outPos = new GeoPositionFilter();
		return new GeoPositionFilterBuilder(outPos);
	}
	public static GeoPositionFilter from(final GeoPositionBase pos) {
		return new GeoPositionFilter(pos);
	}
	public static GeoPositionFilter valueOf(final String str) {
		return GeoPositionFilter.fromString(str);
	}
	public static GeoPositionFilter from(final String str) {
		return GeoPositionFilter.fromString(str);
	}
	public static GeoPositionFilter fromString(final String str) {
		GeoPositionFilter pos = null;
		int distance = 0;
		if (str.contains("@")) {
			String[] parts = str.split("@");
			String baseStr = parts[0];
			String distanceStr = parts[1];
			
			pos = GeoPositionBase.from(baseStr,
									   GeoPositionFilter::new);
			distance = Numbers.isInteger(distanceStr) ? Integer.parseInt(distanceStr) : 0;
		} else {
			pos = GeoPositionBase.from(str,
									   GeoPositionFilter::new);
		}
		if (distance > 0) pos.setDistanceFromPointMeters(distance);
		return pos;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public static class GeoPositionFilterBuilder
				extends GeoPositionBaseBuilder<GeoPositionFilter> {
		
		private GeoPositionFilterBuilder(final GeoPositionFilter geoPos) {
			super(geoPos);
		}
		
		public GeoPositionFilterBuilder withDistanceFromPointMeters(final int meters) {
			_geoPos.setDistanceFromPointMeters(meters);
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		String superStr = super.asString();
		return _distanceFromPointMeters > 0 ? Strings.customized("{}@{}",
																 superStr,_distanceFromPointMeters)
											: superStr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the filter is filtering for any value
	 * @return
	 */
	public boolean isFiltering() {
		return this.getPosition() != null
			|| this.getZipCode() != null
			|| this.getTerritoryId() != null || this.getCountryId() != null || this.getStateId() != null 
			|| this.getCountyId() != null || this.getRegionId() != null 
			|| this.getMunicipalityId() != null || this.getLocalityId() != null || this.getDistrictId() != null
			|| this.getNeighborhoodId() != null || this.getStreetId() != null || this.getPortalId() != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODED
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		boolean superEq = super.equals(o);
		if (!superEq) return false;
		if (!(o instanceof GeoPositionFilter)) return false;
		
		GeoPositionFilter other = (GeoPositionFilter)o;
		boolean distanceEqs = this.getDistanceFromPointMeters() == other.getDistanceFromPointMeters();
		return distanceEqs;
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
