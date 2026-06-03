package r01f.types.geo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Data about a geographical point
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
 *		GeoPosition pos = GeoPosition.createNew()
 *							 .withPosition(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE))	
 *							 .withCountry(GeoCountryID.forId(34)) 
 *	 						 .positionedAt(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE)));
 * </pre>
 */
@MarshallType(as="geoPosition")
@Accessors(prefix="_")
public class GeoPosition
     extends GeoPositionBase {

	private static final long serialVersionUID = 8722622151577217898L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Stairway
	 */
	@MarshallField(as="stairWay")
	@Getter @Setter private String _stairWay;
	/**
	 * Floor
	 */
	@MarshallField(as="floor")
	@Getter @Setter private String _floor;
	/**
	 * door
	 */
	@MarshallField(as="door")
	@Getter @Setter private String _door;
	/**
	 * A textual (lang-independent) representation of the address
	 */
	@MarshallField(as="address",escape=true)
	@Getter @Setter private String _addressText;
	/**
	 * Textual directions (language dependent)
	 */
	@MarshallField(as="directions")
	@Getter @Setter private LanguageTexts _directions;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition() {
		// default no-args constructor
	}
	public GeoPosition(final GeoPositionBase pos) {
		super(pos);
	}
	public GeoPosition(final GeoPosition other) {
		super(other);
		_stairWay = other.getStairWay();
		_floor = other.getFloor();
		_door = other.getDoor();
		_addressText = other.getAddressText();
		_directions = other.getDirections();
	}
	public static GeoPositionBuilder createNew() {
		GeoPosition outPos = new GeoPosition();
		return new GeoPositionBuilder(outPos);
	}
	public static GeoPosition from(final GeoPositionBase pos) {
		return new GeoPosition(pos);
	}
	public static GeoPosition valueOf(final String str) {
		return GeoPosition.fromString(str);
	}
	public static GeoPosition from(final String str) {
		return GeoPosition.fromString(str);
	}
	public static GeoPosition fromString(final String str) {
		return GeoPositionBase.from(str,
									GeoPosition::new);
	}
	public static GeoPosition from(final GeoPositionDetailed geoPos,final Language lang) {
		if (geoPos == null) return new GeoPosition();
		GeoPosition outGeo = new GeoPosition();
		if (geoPos.getTerritory() != null) outGeo.setTerritory(geoPos.getTerritory().asLocationItemIn(lang));
		if (geoPos.getCountry() != null) outGeo.setCountry(geoPos.getCountry().asLocationItemIn(lang));
		if (geoPos.getState() != null) outGeo.setState(geoPos.getState().asLocationItemIn(lang));
		if (geoPos.getCounty() != null) outGeo.setCounty(geoPos.getCounty().asLocationItemIn(lang));
		if (geoPos.getRegion() != null) outGeo.setRegion(geoPos.getRegion().asLocationItemIn(lang));
		if (geoPos.getMunicipality() != null) outGeo.setMunicipality(geoPos.getMunicipality().asLocationItemIn(lang));
		if (geoPos.getLocality() != null) outGeo.setLocality(geoPos.getLocality().asLocationItemIn(lang));
		if (geoPos.getDistrict() != null) outGeo.setDistrict(geoPos.getDistrict().asLocationItemIn(lang));
		if (geoPos.getNeighborhood() != null) outGeo.setNeighborhood(geoPos.getNeighborhood().asLocationItemIn(lang));
		if (geoPos.getStreet() != null) outGeo.setStreet(geoPos.getStreet().asLocationItemIn(lang));
		if (geoPos.getPortal() != null) outGeo.setPortal(geoPos.getPortal().asLocationItemIn(lang));
		if (geoPos.getZipCode() != null) outGeo.setZipCode(geoPos.getZipCode());
		if (geoPos.getStairWay() != null) outGeo.setStairWay(geoPos.getStairWay());
		if (geoPos.getFloor() != null) outGeo.setFloor(geoPos.getFloor());
		if (geoPos.getAddressText() != null) outGeo.setAddressText(geoPos.getAddressText());
		if (geoPos.getDirections() != null) outGeo.setDirections(geoPos.getDirections());
		return outGeo;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public static class GeoPositionBuilder
				extends GeoPositionBaseBuilder<GeoPosition> {
		
		private GeoPositionBuilder(final GeoPosition geoPos) {
			super(geoPos);
		}
		
		public GeoPositionBuilder withStairWay(final String stairWay) {
			_geoPos.setStairWay(stairWay);
			return this;
		}
		public GeoPositionBuilder withFloor(final String floor) {
			_geoPos.setFloor(floor);
			return this;
		}
		public GeoPositionBuilder withDoor(final String door) {
			_geoPos.setDoor(door);
			return this;
		}
		public GeoPositionBuilder withAddressText(final String addressText) {
			_geoPos.setAddressText(addressText);
			return this;
		}
		public GeoPositionBuilder withDirections(final LanguageTexts directions) {
			_geoPos.setDirections(directions);
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODED
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		boolean superEq = super.equals(o);
		if (!superEq) return false;
		if (!(o instanceof GeoPosition)) return false;
		
		GeoPosition other = (GeoPosition)o;
		boolean stairWayEqs = this.getStairWay() != null && other.getStairWay() != null ? this.getStairWay().equals(other.getStairWay())
																						: this.getStairWay() != null ? false
																													 : true;	// both null
		boolean floorEqs = this.getFloor() != null && other.getFloor() != null ? this.getFloor().equals(other.getFloor())
																			   : this.getFloor() != null ? false
																					   					 : true; 	// both null
		boolean doorEqs = this.getDoor() != null && other.getDoor() != null ? this.getDoor().equals(other.getDoor())
																			: this.getDoor() != null ? false
																									 : true;	// both null
		boolean addrEqs = this.getAddressText() != null && other.getAddressText() != null ? this.getAddressText().equals(other.getAddressText())
																						  : this.getAddressText() != null ? false
																								  						  : true;	// both null
		boolean dirsEqs = this.getDirections() != null && other.getDirections() != null ? this.getDirections().equals(other.getDirections())
																						: this.getDirections() != null ? false
																													   : true;	// both null
		return stairWayEqs && floorEqs && doorEqs && addrEqs && dirsEqs;
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
