package r01f.types.geo;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Factory;
import r01f.patterns.FactoryFrom;
import r01f.patterns.IsBuilder;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.XY;
import r01f.types.geo.GeoIDs.GeoCountryID;
import r01f.types.geo.GeoIDs.GeoCountyID;
import r01f.types.geo.GeoIDs.GeoDistrictID;
import r01f.types.geo.GeoIDs.GeoID;
import r01f.types.geo.GeoIDs.GeoLocalityID;
import r01f.types.geo.GeoIDs.GeoMunicipalityID;
import r01f.types.geo.GeoIDs.GeoNeighborhoodID;
import r01f.types.geo.GeoIDs.GeoPortalID;
import r01f.types.geo.GeoIDs.GeoRegionID;
import r01f.types.geo.GeoIDs.GeoStateID;
import r01f.types.geo.GeoIDs.GeoStreetID;
import r01f.types.geo.GeoIDs.GeoTerritoryID;
import r01f.types.geo.GeoIDs.GeoZipCode;
import r01f.types.geo.GeoPosition2D.GeoPositionStandard;
import r01f.util.types.Objects;
import r01f.util.types.Strings;

/**
 * Data about a geographical point
 * 
 * IMPORTANT!!
 * This object just contains IDs (no text data). 
 * See {@link GeoPosition}
 * 
 * <pre>
 *		- x,y
 * Territory											Europe
 *   |_country											Spain										
 *   	 |_state										Euskadi
 *   		 |_county									Bizkaia
 *   		 	|_region								Gran Bilbao / valles alaveses
 *   				|_municipality						Bilbao
 *  					|_locality						Bilbao	
 *   						|_district					01	
 *   							|_neighborhood 			Abando
 *   								|_street			General Concha
 *   									|_portal		12
 *		- Textual info
 * </pre>
 * Usage:
 * <pre class='brush:java'>
 *		GeoPosition pos = GeoPosition.create()
 *							 .withPosition(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE))	
 *							 .withCountry(GeoCountryID.forId(34)) 
 *							 .build();
 * </pre>
 */
@MarshallType(as="geoPositionBase")
@Accessors(prefix="_")
public class GeoPositionBase
  implements CanBeRepresentedAsString,
  			 Comparable<GeoPositionBase> {

	private static final long serialVersionUID = -8514721085110934114L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * X,Y position
	 */
	@MarshallField(as="position2D")
	@Getter @Setter private GeoPosition2D _position;
/////////////////////////////////////////////////////////////////////////////////////////
//	MORE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Territory (groups more than a single state)
	 */
	@MarshallField(as="territory")
	@Getter @Setter private GeoLocationItem<GeoTerritoryID> _territory;
	/**
	 * Country
	 */
	@MarshallField(as="country")
	@Getter @Setter private GeoLocationItem<GeoCountryID> _country;
	/**
	 * State or province
	 */
	@MarshallField(as="state")
	@Getter @Setter private GeoLocationItem<GeoStateID> _state;
	/**
	 * Country
	 */
	@MarshallField(as="county")
	@Getter @Setter private GeoLocationItem<GeoCountyID> _county;
	/**
	 * Locality / region (groups some municipalities)
	 */
	@MarshallField(as="region")
	@Getter @Setter private GeoLocationItem<GeoRegionID> _region;
	/**
	 * Municipality
	 */
	@MarshallField(as="municipality")
	@Getter @Setter private GeoLocationItem<GeoMunicipalityID> _municipality;
	/**
	 * Locality
	 */
	@MarshallField(as="locality")
	@Getter @Setter private GeoLocationItem<GeoLocalityID> _locality;
	/**
	 * District
	 */
	@MarshallField(as="district")
	@Getter @Setter private GeoLocationItem<GeoDistrictID> _district;
	/**
	 * Neighborhood
	 */
	@MarshallField(as="neighborhood")
	@Getter @Setter private GeoLocationItem<GeoNeighborhoodID> _neighborhood;
	/**
	 * Portal
	 */
	@MarshallField(as="street")
	@Getter @Setter private GeoLocationItem<GeoStreetID> _street;
	/**
	 * Street
	 */
	@MarshallField(as="portal")
	@Getter @Setter private GeoLocationItem<GeoPortalID> _portal;
	/**
	 * Zip code
	 */
	@MarshallField(as="zipCode")
	@Getter @Setter private GeoZipCode _zipCode;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPositionBase() {
		// default no-args constructor
	}
	public GeoPositionBase(final GeoTerritoryID territory,final GeoCountryID country,final GeoStateID state,final GeoCountyID county,final GeoRegionID region,
					       final GeoMunicipalityID municipality,final GeoLocalityID locality,final GeoDistrictID district,final GeoNeighborhoodID neighborhood,final GeoStreetID street,
					       final GeoZipCode zipCode,
					       final GeoPosition2D position) {
		_territory = GeoLocationItem.normalized(territory);
		_country = GeoLocationItem.normalized(country);
		_state = GeoLocationItem.normalized(state);
		_county = GeoLocationItem.normalized(county);
		_region = GeoLocationItem.normalized(region);
		_municipality = GeoLocationItem.normalized(municipality);
		_locality = GeoLocationItem.normalized(locality);
		_district = GeoLocationItem.normalized(district);
		_neighborhood = GeoLocationItem.normalized(neighborhood);
		_street = GeoLocationItem.normalized(street);
		_zipCode = zipCode;
		_position = position;
	}
	public GeoPositionBase(final GeoPositionBase other) {
		if (other != null) {		
			this.setTerritory(other.getTerritory());
			this.setCountry(other.getCountry());
			this.setState(other.getState());
			this.setCounty(other.getCounty());
			this.setRegion(other.getRegion());
			this.setMunicipality(other.getMunicipality());
			this.setLocality(other.getLocality());
			this.setDistrict(other.getDistrict());
			this.setNeighborhood(other.getNeighborhood());
			this.setStreet(other.getStreet());
			this.setPortal(other.getPortal());
			this.setZipCode(other.getZipCode());
			this.setPosition(other.getPosition());
		}
	}
	public static GeoPositionBaseBuilder<GeoPositionBase> create() {
		GeoPositionBase outPos = new GeoPositionBase();
		return new GeoPositionBaseBuilder<>(outPos);
	}
	public static GeoPositionBase from(final GeoPositionBase other) {
		return new GeoPositionBase(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IDs
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("null")
	private static <I extends GeoID,G extends GeoLocationItem<I>> G _setId(final G loc,final I id,
																		   final FactoryFrom<I,G> locFactory) {
		G outLoc = null;
		if (id == null && loc != null && loc.getName() == null) {
			outLoc = null;
		} else if (loc == null) {
			outLoc = locFactory.from(id);
		} else {
			outLoc = loc;
			outLoc.setId(id);
		}
		return outLoc;
	}
	@SuppressWarnings({ "null" })
	private static <I extends GeoID,G extends GeoLocationItem<I>> G _setName(final G loc,final String name,
																			 final FactoryFrom<String,G> locFactory) {
		G outLoc = null;
		if (name == null && loc != null && loc.getId() == null) {
			outLoc = null;
		} else if (loc == null) {
			outLoc = locFactory.from(name);
		} else {
			outLoc = loc;
			outLoc.setName(name);
		}
		return outLoc;
	}
	
	
	////////// Territory
	public GeoTerritoryID getTerritoryId() {
		return _territory != null ? _territory.getId() : null;
	}
	public void setTerritoryId(final GeoTerritoryID id) {
		_territory = _setId(_territory,id,GeoLocationItem::normalized);
	}
	public String getTerritoryName() {
		return _territory != null ? _territory.getName() : null;
	}
	public void setTerritoryName(final String name) {
		_territory = _setName(_territory,name,GeoLocationItem::notNormalized);
	}
	//////// Country
	public GeoCountryID getCountryId() {
		return _country != null ? _country.getId() : null;
	}
	public void setCountryId(final GeoCountryID id) {
		_country = _setId(_country,id,GeoLocationItem::normalized);
	}
	public String getCountryName() {
		return _country != null ? _country.getName() : null;
	}
	public void setCountryName(final String name) {
		_country = _setName(_country,name,GeoLocationItem::notNormalized);;
	}
	//////// State
	public GeoStateID getStateId() {
		return _state != null ? _state.getId() : null;
	}
	public void setStateId(final GeoStateID id) {
		_state = _setId(_state,id,GeoLocationItem::normalized);
	}
	public String getStateName() {
		return _state != null ? _state.getName() : null;
	}
	public void setStateName(final String name) {
		_state = _setName(_state,name,GeoLocationItem::notNormalized);
	}
	//////// County
	public GeoCountyID getCountyId() {
		return _county != null ? _county.getId() : null;
	}
	public void setCountyId(final GeoCountyID id) {
		_county = _setId(_county,id,GeoLocationItem::normalized);
	}
	public String getCountyName() {
		return _county != null ? _county.getName() : null;
	}
	public void setCountyName(final String name) {
		_county = _setName(_county,name,GeoLocationItem::notNormalized);
	}
	//////// Region
	public GeoRegionID getRegionId() {
		return _region != null ? _region.getId() : null;
	}
	public void setRegionId(final GeoRegionID id) {
		_region = _setId(_region,id,GeoLocationItem::normalized);
	}
	public String getRegionName() {
		return _region != null ? _region.getName() : null;
	}
	public void setRegionName(final String name) {
		_region = _setName(_region,name,GeoLocationItem::notNormalized);
	}
	//////// Municipality
	public GeoMunicipalityID getMunicipalityId() {
		return _municipality != null ? _municipality.getId() : null;
	}
	public void setMunicipalityId(final GeoMunicipalityID id) {
		_municipality = _setId(_municipality,id,GeoLocationItem::normalized);
	}
	public String getMunicipalityName() {
		return _municipality != null ? _municipality.getName() : null;
	}
	public void setMunicipalityName(final String name) {
		_municipality = _setName(_municipality,name,GeoLocationItem::notNormalized);
	}
	//////// Locality
	public GeoLocalityID getLocalityId() {
		return _locality != null ? _locality.getId() : null;
	}
	public void setLocalityId(final GeoLocalityID id) {
		_locality = _setId(_locality,id,GeoLocationItem::normalized);
	}
	public String getLocalityName() {
		return _locality != null ? _locality.getName() : null;
	}
	public void setLocalityName(final String name) {
		_locality = _setName(_locality,name,GeoLocationItem::notNormalized);
	}
	//////// District
	public GeoDistrictID getDistrictId() {
		return _district != null ? _district.getId() : null;
	}
	public void setDistrictId(final GeoDistrictID id) {
		_district = _setId(_district,id,GeoLocationItem::normalized);
	}
	public String getDistrictName() {
		return _district != null ? _district.getName() : null;
	}
	public void setDistrictName(final String name) {
		_district = _setName(_district,name,GeoLocationItem::notNormalized);
	}
	//////// Neighborhood
	public GeoNeighborhoodID getNeighborhoodId() {
		return _neighborhood != null ? _neighborhood.getId() : null;
	}
	public void setNeighborhoodId(final GeoNeighborhoodID id) {
		_neighborhood = _setId(_neighborhood,id,GeoLocationItem::normalized);
	}
	public String getNeighborhoodName() {
		return _neighborhood != null ? _neighborhood.getName() : null;
	}
	public void setNeighborhoodName(final String name) {
		_neighborhood = _setName(_neighborhood,name,GeoLocationItem::notNormalized);
	}
	//////// Street
	public GeoStreetID getStreetId() {
		return _street != null ? _street.getId() : null;
	}
	public void setStreetId(final GeoStreetID id) {
		_street = _setId(_street,id,GeoLocationItem::normalized);
	}
	public String getStreetName() {
		return _street != null ? _street.getName() : null;
	}
	public void setStreetName(final String name) {
		_street = _setName(_street,name,GeoLocationItem::notNormalized);
	}
	//////// Portal
	public GeoPortalID getPortalId() {
		return _portal != null ? _portal.getId() : null;
	}
	public void setPortalId(final GeoPortalID id) {
		_portal = _setId(_portal,id,GeoLocationItem::normalized);
	}
	public String getPortalName() {
		return _portal != null ? _portal.getName() : null;
	}
	public void setPortalName(final String name) {
		_portal = _setName(_portal,name,GeoLocationItem::notNormalized);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	// geo position pattern: ETRS:4236/12.45,234.12/12
	private static final String GEOPOSTION2D_PATTERN = "(" + GeoPosition2D.GEOPOSITON2D_STANDARD_PATTERN_NO_CAPTURE + ")?" +		// matches the standard EPSG:4326 (google) / EPSG:25830 (ETRS89) / EPSG:4230 (ED50)
													   "/?" +
													   "(" + GeoPosition2D.COORDINATE_AXIS_VALUE_PATTERN + "," + GeoPosition2D.COORDINATE_AXIS_VALUE_PATTERN + ")?";
	private static final Pattern FROM_STRING_PATTERN = Pattern.compile(GEOPOSTION2D_PATTERN + "_?" +	// [1] standard / [2] point x,y
																	  "([^_]+)?" + "_?" + 		// [3] zip code
																	  "([^_]+)?" + "_?" +		// [A] territory
																	  "([^_]+)?" + "_?" +		// [B] country
																	  "([^_]+)?" + "_?" +		// [C] state
																	  "([^_]+)?" + "_?" +		// [D] county
																	  "([^_]+)?" + "_?" +		// [E] region
																	  "([^_]+)?" + "_?" +		// [F] municipality
																	  "([^_]+)?" + "_?" +		// [G] locality
																	  "([^_]+)?" + "_?" + 		// [H] district
																	  "([^_]+)?" + "_?" + 		// [I] neighborhood
																	  "([^_]+)?" + "_?" + 		// [J] street
																	  "([^_]+)?" + "_?"); 		// [K] portal
	public static GeoPositionBase from(final String str) {
		return GeoPositionBase.from(str,
									GeoPositionBase::new);	// factory
	}
	public static <G extends GeoPositionBase> G from(final String str,
									   				 final Factory<G> geoFactory) {
		G out = null;
		
		Matcher m = FROM_STRING_PATTERN.matcher(str);
		if (!m.find()) {
			throw new IllegalArgumentException(str + " is NOT a valid [geo position pick]: it DOES NOT match " + FROM_STRING_PATTERN.toString());
		}
		// string representation
		String geoPos2DStdStr = m.group(1);
		String geoPos2DXYStr = m.group(2);
		
		String zipCodeStr = m.group(3);

		int offset = 3;
		String territoryStr = m.group(offset + 1);
		String countryStr = m.group(offset + 2);
		String stateStr = m.group(offset + 3);
		String countyStr = m.group(offset + 4);
		String regionStr = m.group(offset + 5);
		String municipalityStr = m.group(offset + 6);
		String localityStr = m.group(offset + 7);
		String districtStr = m.group(offset + 8);
		String neighborhoodStr = m.group(offset + 9);
		String streetStr = m.group(offset + 10);
		String portalStr = m.group(offset + 11);
		
		out = geoFactory.create();

		GeoPositionStandard geoPosStd = Strings.isNOTNullOrEmpty(geoPos2DStdStr) ? GeoPositionStandard.fromCode(geoPos2DStdStr) : null;
		XY coords = Strings.isNOTNullOrEmpty(geoPos2DXYStr) ? XY.fromString(geoPos2DXYStr) : null;
		out.setPosition(geoPosStd != null && coords != null ? new GeoPosition2D(geoPosStd,
																				coords.getX(),coords.getY())
					  										: null);
		
		out.setZipCode(Strings.isNOTNullOrEmpty(zipCodeStr) ? GeoZipCode.forId(zipCodeStr) : null);
		
		out.setTerritoryId(Strings.isNOTNullOrEmpty(territoryStr) ? GeoTerritoryID.forId(territoryStr) : null);
		out.setCountryId(Strings.isNOTNullOrEmpty(countryStr) ? GeoCountryID.forId(countryStr) : null);
		out.setStateId(Strings.isNOTNullOrEmpty(stateStr) ? GeoStateID.forId(stateStr) : null);
		out.setCountyId(Strings.isNOTNullOrEmpty(countyStr) ? GeoCountyID.forId(countyStr) : null);
		out.setRegionId(Strings.isNOTNullOrEmpty(regionStr) ? GeoRegionID.forId(regionStr) : null);
		out.setMunicipalityId(Strings.isNOTNullOrEmpty(municipalityStr) ? GeoMunicipalityID.forId(municipalityStr) : null);
		out.setLocalityId(Strings.isNOTNullOrEmpty(localityStr) ? GeoLocalityID.forId(localityStr) : null);
		out.setDistrictId(Strings.isNOTNullOrEmpty(districtStr) ? GeoDistrictID.forId(districtStr) : null);
		out.setNeighborhoodId(Strings.isNOTNullOrEmpty(neighborhoodStr) ? GeoNeighborhoodID.forId(neighborhoodStr) : null);
		out.setStreetId(Strings.isNOTNullOrEmpty(streetStr) ? GeoStreetID.forId(Long.parseLong(streetStr)) : null);
		out.setPortalId(Strings.isNOTNullOrEmpty(portalStr) ? GeoPortalID.forId(Long.parseLong(portalStr)) : null);
		return out;
	}
	@Override
	public String asString() {
		// "EPSG:std/x,y_zip-code_{}_{}_{}_{}_{}_{}_{}_{}_{}_{}_{}",
		//  	1     2     3     A  B  C  D  E  F  G  H  I  J  K  
		//  this.getPosition().asString(),	// [1]/[2]  position x,y
		//	this.getZipCode()				// [3] zip code
		//  this.getTerritoryId(),			// [A] territory
		//  this.getCountryId(),			// [B] country
		//	this.getStateId(),				// [C] state
		//	this.getCountyId(),				// [D] county
		//	this.getRegionId(),				// [E] region
		//	this.getMunicipalityId(),		// [F] municipality
		//	this.getLocalityId(),			// [G] locality
		//	this.getDistrictId(),			// [H] district
		//	this.getNeighborhoodId(),		// [I] neighborhood
		//	this.getStreetId(),				// [J] street
		//	this.getPortalId());			// [K] portal
		
		String out = null;
		// [1] - items
		if (this.getPortalId() != null) {
			out = Strings.customized("{}_{}_{}_{}_{}_{}_{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E  F  G  H  I  J  K
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()),			// [E]  region
								  	 _geoIdToStringOrEmpty(this.getMunicipalityId()),	// [F]  municipality
								  	 _geoIdToStringOrEmpty(this.getLocalityId()),		// [G]  locality
								  	 _geoIdToStringOrEmpty(this.getDistrictId()),		// [H]  district
								  	 _geoIdToStringOrEmpty(this.getNeighborhoodId()),	// [I] neighborhood
								  	 _geoIdToStringOrEmpty(this.getStreetId()),			// [J] street
								  	 _geoIdToStringOrEmpty(this.getPortalId()));		// [K] portal
		} else if (this.getStreetId() != null && _allNull(this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}_{}_{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E  F  G  H  I  J 
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()),			// [E]  region
								  	 _geoIdToStringOrEmpty(this.getMunicipalityId()),	// [F]  municipality
								  	 _geoIdToStringOrEmpty(this.getLocalityId()),		// [G]  locality
								  	 _geoIdToStringOrEmpty(this.getDistrictId()),		// [H]  district
								  	 _geoIdToStringOrEmpty(this.getNeighborhoodId()),	// [I] neighborhood
								  	 _geoIdToStringOrEmpty(this.getStreetId()));		// [J] street
		} else if (this.getNeighborhoodId() != null && _allNull(this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}_{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E  F  G  H  I  
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()),			// [E]  region
								  	 _geoIdToStringOrEmpty(this.getMunicipalityId()),	// [F]  municipality
								  	 _geoIdToStringOrEmpty(this.getLocalityId()),		// [G]  locality
								  	 _geoIdToStringOrEmpty(this.getDistrictId()),		// [H]  district
								  	 _geoIdToStringOrEmpty(this.getNeighborhoodId()));	// [I] neighborhood
		} else if (this.getDistrictId() != null && _allNull(this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E  F  G  H   
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()),			// [E]  region
								  	 _geoIdToStringOrEmpty(this.getMunicipalityId()),	// [F]  municipality
								  	 _geoIdToStringOrEmpty(this.getLocalityId()),		// [G]  locality
								  	 _geoIdToStringOrEmpty(this.getDistrictId()));		// [H]  district
		} else if (this.getLocalityId() != null && _allNull(this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E  F  G  
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()),			// [E]  region
								  	 _geoIdToStringOrEmpty(this.getMunicipalityId()),	// [F]  municipality
								  	 _geoIdToStringOrEmpty(this.getLocalityId()));		// [G]  locality
		} else if (this.getMunicipalityId() != null && _allNull(this.getLocalityId(),this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E  F   
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()),			// [E]  region
								  	 _geoIdToStringOrEmpty(this.getMunicipalityId()));	// [F]  municipality
		} else if (this.getRegionId() != null && _allNull(this.getMunicipalityId(),this.getLocalityId(),this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}_{}",
							 	//    A  B  C  D  E    
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()),			// [D]  county
								  	 _geoIdToStringOrEmpty(this.getRegionId()));		// [E]  region
		} else if (this.getCountyId() != null && _allNull(this.getRegionId(),this.getMunicipalityId(),this.getLocalityId(),this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}_{}",
							 	//    A  B  C  D   
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()),			// [C]  state
								  	 _geoIdToStringOrEmpty(this.getCountyId()));		// [D]  county
		} else if (this.getStateId() != null && _allNull(this.getCountyId(),this.getRegionId(),this.getMunicipalityId(),this.getLocalityId(),this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}_{}",
							 	//    A  B  C    
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()),		// [B]  country
								  	 _geoIdToStringOrEmpty(this.getStateId()));			// [C]  state
		} else if (this.getCountryId() != null && _allNull(this.getStateId(),this.getCountyId(),this.getRegionId(),this.getMunicipalityId(),this.getLocalityId(),this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}_{}",
							 	//    A  B  
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()),		// [A]  territory
								  	 _geoIdToStringOrEmpty(this.getCountryId()));		// [B]  country
		} else if (this.getTerritoryId() != null && _allNull(this.getCountryId(),this.getStateId(),this.getCountyId(),this.getRegionId(),this.getMunicipalityId(),this.getLocalityId(),this.getDistrictId(),this.getNeighborhoodId(),this.getStreetId(),this.getPortalId())) {
			out = Strings.customized("{}",
							 	//    A    
								  	 _geoIdToStringOrEmpty(this.getTerritoryId()));		// [A]  territory
		} else {
			out = "";
		}
		// [2] - position
		String posAndZip = this.getPosition() != null
						&& this.getZipCode() != null ? Strings.customized("{}_{}",
																		  this.getPosition().asString(),this.getZipCode().asString())
													 : this.getPosition() != null ? Strings.customized("{}_",this.getPosition().asString())
															 					  : this.getZipCode() != null ? Strings.customized("_{}",this.getZipCode().asString())
															 							  					  : "_";	// both null
		return posAndZip != null
			&& Strings.isNOTNullOrEmpty(out) ? Strings.customized("{}_{}",
															   	  posAndZip,out)
										  	 : posAndZip != null ? posAndZip
												  				 : out;
	}
	private static boolean _allNull(final GeoID... geoIds) {
		boolean outAllNull = true;
		for (GeoID geoId : geoIds) {
			if (geoId != null) {
				outAllNull = false;
				break;
			}
		}
		return outAllNull;
	}
	private static String _geoIdToStringOrEmpty(final GeoID geoId) {
		return geoId == null ? "" : geoId.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static class GeoPositionBaseBuilder<SELF_TYPE extends GeoPositionBase>
	  		 implements IsBuilder {
		
		protected final SELF_TYPE _geoPos;
		
		public GeoPositionBaseBuilder<SELF_TYPE> withPosition(final GeoPosition2D pos) {
			_geoPos.setPosition(pos);
			return this;
		}
		////////// Territory
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedTerritory(final GeoTerritoryID territory) {
			_geoPos.setTerritory(GeoLocationItem.normalized(territory));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedTerritory(final String territory) {
			_geoPos.setTerritory(GeoLocationItem.notNormalized(territory));
			return this;
		}
		////////// Country
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedCountry(final GeoCountryID country) {
			_geoPos.setCountry(GeoLocationItem.normalized(country));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedCountry(final String country) {
			_geoPos.setCountry(GeoLocationItem.notNormalized(country));
			return this;
		}
		////////// State
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedState(final GeoStateID state) {
			_geoPos.setState(GeoLocationItem.normalized(state));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedState(final String state) {
			_geoPos.setState(GeoLocationItem.notNormalized(state));
			return this;
		}
		////////// County
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedCounty(final GeoCountyID county) {
			_geoPos.setCounty(GeoLocationItem.normalized(county));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedCounty(final String county) {
			_geoPos.setCounty(GeoLocationItem.notNormalized(county));
			return this;
		}
		////////// Region
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedRegion(final GeoRegionID region) {
			_geoPos.setRegion(GeoLocationItem.normalized(region));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedRegion(final String region) {
			_geoPos.setRegion(GeoLocationItem.notNormalized(region));
			return this;
		}
		////////// Municipality
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedMunicipality(final GeoMunicipalityID mun) {
			_geoPos.setMunicipality(GeoLocationItem.normalized(mun));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedMunicipality(final String mun) {
			_geoPos.setMunicipality(GeoLocationItem.notNormalized(mun));
			return this;
		}
		////////// Locality
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedLocality(final GeoLocalityID locality) {
			_geoPos.setLocality(GeoLocationItem.normalized(locality));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedLocality(final String locality) {
			_geoPos.setLocality(GeoLocationItem.notNormalized(locality));
			return this;
		}
		////////// District
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedDistrict(final GeoDistrictID dist) {
			_geoPos.setDistrict(GeoLocationItem.normalized(dist));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedDistrict(final String dist) {
			_geoPos.setDistrict(GeoLocationItem.notNormalized(dist));
			return this;
		}
		////////// Neighborhood
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedNeighborhood(final GeoNeighborhoodID nh) {
			_geoPos.setNeighborhood(GeoLocationItem.normalized(nh));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedNeighborhood(final String nh) {
			_geoPos.setNeighborhood(GeoLocationItem.notNormalized(nh));
			return this;
		}
		////////// Street
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedStreet(final GeoStreetID street) {
			_geoPos.setStreet(GeoLocationItem.normalized(street));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedStreet(final String street) {
			_geoPos.setStreet(GeoLocationItem.notNormalized(street));
			return this;
		}
		////////// Portal
		public GeoPositionBaseBuilder<SELF_TYPE> withNormalizedPortal(final GeoPortalID portal) {
			_geoPos.setPortal(GeoLocationItem.normalized(portal));
			return this;
		}
		public GeoPositionBaseBuilder<SELF_TYPE> withNOTNormalizedPortal(final String portal) {
			_geoPos.setPortal(GeoLocationItem.notNormalized(portal));
			return this;
		}
		////////// zipcode
		public GeoPositionBaseBuilder<SELF_TYPE> withZipCode(final GeoZipCode zipCode) {
			_geoPos.setZipCode(zipCode);
			return this;
		}
		public SELF_TYPE build() {
			return _geoPos;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof GeoPositionBase)) return false;
		GeoPositionBase other = (GeoPositionBase)o;
		
		boolean posEqs = this.getPosition() != null && other.getPosition() != null ? this.getPosition().equals(other.getPosition())
																				   : other.getPosition() != null ? false
																						  						 : true;	// both null
		boolean territoryEqs = _geoLocItemsEqs(this.getTerritory(),other.getTerritory());
		boolean countryEqs = _geoLocItemsEqs(this.getCountry(),other.getCountry());
		boolean stateEqs = _geoLocItemsEqs(this.getState(),other.getState());
		boolean countyEqs = _geoLocItemsEqs(this.getCounty(),other.getCounty());
		boolean regionEqs = _geoLocItemsEqs(this.getRegion(),other.getRegion());
		boolean municipalityEqs = _geoLocItemsEqs(this.getMunicipality(),other.getMunicipality());
		boolean localityEqs = _geoLocItemsEqs(this.getLocality(),other.getLocality());
		boolean districtEqs = _geoLocItemsEqs(this.getDistrict(),other.getDistrict());
		boolean neighborhoodEqs = _geoLocItemsEqs(this.getNeighborhood(),other.getNeighborhood());
		boolean streetEqs = _geoLocItemsEqs(this.getStreet(),other.getStreet());
		boolean zipCodeEqs = _equalsZipCode(this.getZipCode(),other.getZipCode());
		return posEqs
			&& territoryEqs && countryEqs && stateEqs && countyEqs && regionEqs && municipalityEqs && localityEqs && districtEqs && neighborhoodEqs && streetEqs
			&& zipCodeEqs;
		
	}
	private static <I extends GeoID> boolean _geoLocItemsEqs(final GeoLocationItem<I> i1,final GeoLocationItem<I> i2) {
		return i1 != null && i2 != null ? i1.equals(i2)
										: i1 != null ? false
													 : true;	// both null
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_position,
								_territory,_country,_state,_county,_region,_municipality,_locality,_district,_neighborhood,_street,
								_zipCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPARATOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Comparator<GeoPositionBase> LOOSE_COMPARATOR = (p1,p2) -> {
																			
																			boolean eq = _equals(p1.getTerritory(),p2.getTerritory())
																					  && _equals(p1.getCountry(),p2.getCountry())
																					  && _equals(p1.getState(),p2.getState())
																					  && _equals(p1.getCounty(),p2.getCounty())
																					  && _equals(p1.getRegion(),p2.getRegion())
																					  && _equals(p1.getMunicipality(),p2.getMunicipality())
																					  && _equals(p1.getLocality(),p2.getLocality())
																					  && _equals(p1.getDistrict(),p2.getDistrict())
																					  && _equals(p1.getNeighborhood(),p2.getNeighborhood())
																					  && _equals(p1.getStreet(),p2.getStreet())
																					  && _equalsZipCode(p1.getZipCode(), p2.getZipCode());	
																			return eq ? 0 : -1;
																		};
	@Override
	public int compareTo(final GeoPositionBase other) {
		return LOOSE_COMPARATOR.compare(this,other);
	}
	private static <I extends GeoID> boolean _equals(final GeoLocationItem<I> p1,final GeoLocationItem<I> p2) {
		boolean bothNOTNull = _bothNOTNull(p1,p2);
		boolean justONENull = _justOneNull(p1,p2);
		boolean eq = bothNOTNull && p1.is(p2);
		
		return bothNOTNull ? eq
						   : justONENull ? false
								   		 : true;	// both null
	}
	private static boolean _equalsZipCode(final GeoZipCode p1,final GeoZipCode p2) {
		return p1 != null ? p1.is(p2)
					  	  : p2 != null ? false
					  							 				   : true;	// both null
	}
	private static <I extends GeoID> boolean _bothNOTNull(final GeoLocationItem<I> p1,final GeoLocationItem<I> p2) {
		return p1 != null && p2 != null;
	}
	private static <I extends GeoID> boolean _justOneNull(final GeoLocationItem<I> p1,final GeoLocationItem<I> p2) {
		return (p1 != null && p2 == null)
				||
			   (p1 == null && p2 != null);
	}	
}
