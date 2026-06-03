package r01f.types.geo;

import java.io.Serializable;
import java.util.Comparator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Factory;
import r01f.patterns.IsBuilder;
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
import r01f.util.types.Objects;

/**
 * Data about a geographical point but including all info about all the items
 * instead of just the id & name of the {@link GeoPosition}
 * Use {@link GeoPosition} to store just the ID of the items and this object to store everything about the item
 * 
 * IMPORTANT!!
 * This object just contains IDs (no text data). See {@link GeoPosition}
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
 *		GeoPositionDetailed pos = GeoPosition.create()
 *							 			.withPosition(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE))	
 *							 			.withCountry(GeoCountryID.forId(34)) 
 *										.build();
 * </pre>
 */
@MarshallType(as="geoPosition")
@Accessors(prefix="_")
public class GeoPositionDetailed
  implements Serializable,
  			 Comparable<GeoPositionDetailed> {

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
	@Getter @Setter private GeoTerritory _territory;
	/**
	 * Country
	 */
	@MarshallField(as="country")
	@Getter @Setter private GeoCountry _country;
	/**
	 * State or province
	 */
	@MarshallField(as="state")
	@Getter @Setter private GeoState _state;
	/**
	 * Country
	 */
	@MarshallField(as="county")
	@Getter @Setter private GeoCounty _county;
	/**
	 * Locality / region (groups some municipalities)
	 */
	@MarshallField(as="region")
	@Getter @Setter private GeoRegion _region;
	/**
	 * Municipality
	 */
	@MarshallField(as="municipality")
	@Getter @Setter private GeoMunicipality _municipality;
	/**
	 * Locality
	 */
	@MarshallField(as="locality")
	@Getter @Setter private GeoLocality _locality;
	/**
	 * District
	 */
	@MarshallField(as="district")
	@Getter @Setter private GeoDistrict _district;
	/**
	 * Neighborhood
	 */
	@MarshallField(as="neighborhood")
	@Getter @Setter private GeoNeighborhood _neighborhood;
	/**
	 * Portal
	 */
	@MarshallField(as="street")
	@Getter @Setter private GeoStreet _street;
	/**
	 * Street
	 */
	@MarshallField(as="portal")
	@Getter @Setter private GeoPortal _portal;
	/**
	 * Zip code
	 */
	@MarshallField(as="zipCode")
	@Getter @Setter private GeoZipCode _zipCode;
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
	public GeoPositionDetailed() {
		// default no-args constructor
	}
	public GeoPositionDetailed(final GeoTerritory territory,final GeoCountry country,final GeoState state,final GeoCounty county,final GeoRegion region,
					       	   final GeoMunicipality municipality,final GeoLocality locality,final GeoDistrict district,final GeoNeighborhood neighborhood,final GeoStreet street,
					       	   final GeoZipCode zipCode,
					       	   final GeoPosition2D position) {
		_territory = territory;
		_country = country;
		_state = state;
		_county = county;
		_region = region;
		_municipality = municipality;
		_locality = locality;
		_district = district;
		_neighborhood = neighborhood;
		_street = street;
		_zipCode = zipCode;
		_position = position;
	}
	public GeoPositionDetailed(final GeoPositionDetailed other) {
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
			
			this.setStairWay(other.getStairWay());
			this.setFloor(other.getFloor());
			this.setDoor(other.getDoor());
			this.setAddressText(other.getAddressText());
			this.setDirections(new LanguageTextsMapBacked(other.getDirections()));
		}
	}
	public static GeoPositionDetailedBuilder create() {
		GeoPositionDetailed outPos = new GeoPositionDetailed();
		return new GeoPositionDetailedBuilder(outPos);
	}
	public static GeoPositionDetailed from(final GeoPositionDetailed other) {
		return new GeoPositionDetailed(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition asGeoPositionIn(final Language lang) {
		return GeoPosition.from(this,lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IDs
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("null")
	private static <I extends GeoID,G extends GeoLocationBase<I,G>> G _setId(final G loc,final I id,
																		     final Factory<G> locFactory) {
		G outLoc = null;
		if (id == null && loc != null && loc.getName() == null) {
			outLoc = null;
		} else if (loc == null) {
			outLoc = locFactory.create();
		} else {
			outLoc = loc;
		}
		outLoc.setId(id);
		return outLoc;
	}
	@SuppressWarnings({ "null" })
	private static <I extends GeoID,G extends GeoLocationBase<I,G>> G _setName(final G loc,final String name,final Language lang,
																			   final Factory<G> locFactory) {
		G outLoc = null;
		if (name == null && loc != null && loc.getId() == null) {
			outLoc = null;
		} else if (loc == null) {
			outLoc = locFactory.create();
		} else {
			outLoc = loc;
		}
		outLoc.getName().setIn(lang,name);
		return outLoc;
	}
	
	////////// Territory
	public GeoLocationItem<GeoTerritoryID> getTerritoryItemIn(final Language lang) {
		return _territory != null ? GeoLocationItem.normalized(_territory.getId())
												   .withName(_territory.getNameIn(lang))
								  : null;
	}
	public GeoTerritoryID getTerritoryId() {
		return _territory != null ? _territory.getId() : null;
	}
	public void setTerritoryId(final GeoTerritoryID id) {
		_territory = _setId(_territory,id,
							GeoTerritory::new);
	}
	public String getTerritoryNameIn(final Language lang) {
		return _territory != null ? _territory.getNameIn(lang) : null;
	}
	public void setTerritoryName(final String name,final Language lang) {
		_territory = _setName(_territory,name,lang,
							  GeoTerritory::new);
	}
	//////// Country
	public GeoLocationItem<GeoCountryID> getCountryItemIn(final Language lang) {
		return _country != null ? GeoLocationItem.normalized(_country.getId())
												   .withName(_country.getNameIn(lang))
								  : null;
	}
	public GeoCountryID getCountryId() {
		return _country != null ? _country.getId() : null;
	}
	public void setCountryId(final GeoCountryID id) {
		_country = _setId(_country,id,
						  GeoCountry::new);
	}
	public String getCountryNameIn(final Language lang) {
		return _country != null ? _country.getNameIn(lang) : null;
	}
	public void setCountryName(final String name,final Language lang) {
		_country = _setName(_country,name,lang,
							GeoCountry::new);
	}
	//////// State
	public GeoLocationItem<GeoStateID> getStateItemIn(final Language lang) {
		return _state != null ? GeoLocationItem.normalized(_state.getId())
											   .withName(_state.getNameIn(lang))
							   : null;
	}
	public GeoStateID getStateId() {
		return _state != null ? _state.getId() : null;
	}
	public void setStateId(final GeoStateID id) {
		_state = _setId(_state,id,
						GeoState::new);
	}
	public String getStateNameIn(final Language lang) {
		return _state != null ? _state.getNameIn(lang) : null;
	}
	public void setStateName(final String name,final Language lang) {
		_state = _setName(_state,name,lang,
						  GeoState::new);
	}
	//////// County
	public GeoLocationItem<GeoCountyID> getCountyItemIn(final Language lang) {
		return _county != null ? GeoLocationItem.normalized(_county.getId())
												.withName(_county.getNameIn(lang))
							   : null;
	}
	public GeoCountyID getCountyId() {
		return _county != null ? _county.getId() : null;
	}
	public void setCountyId(final GeoCountyID id) {
		_county = _setId(_county,id,
						 GeoCounty::new);
	}
	public String getCountyNameIn(final Language lang) {
		return _county != null ? _county.getNameIn(lang) : null;
	}
	public void setCountyName(final String name,final Language lang) {
		_county = _setName(_county,name,lang,
						   GeoCounty::new);
	}
	//////// Region
	public GeoLocationItem<GeoRegionID> getRegionItemIn(final Language lang) {
		return _region != null ? GeoLocationItem.normalized(_region.getId())
												.withName(_region.getNameIn(lang))
							   : null;
	}
	public GeoRegionID getRegionId() {
		return _region != null ? _region.getId() : null;
	}
	public void setRegionId(final GeoRegionID id) {
		_region = _setId(_region,id,
						 GeoRegion::new);
	}
	public String getRegionNameIn(final Language lang) {
		return _region != null ? _region.getNameIn(lang) : null;
	}
	public void setRegionName(final String name,final Language lang) {
		_region = _setName(_region,name,lang,
						   GeoRegion::new);
	}
	//////// Municipality
	public GeoLocationItem<GeoMunicipalityID> getMunicipalityItemIn(final Language lang) {
		return _municipality != null ? GeoLocationItem.normalized(_municipality.getId())
												   	  .withName(_municipality.getNameIn(lang))
								     : null;
	}
	public GeoMunicipalityID getMunicipalityId() {
		return _municipality != null ? _municipality.getId() : null;
	}
	public void setMunicipalityId(final GeoMunicipalityID id) {
		_municipality = _setId(_municipality,id,
							   GeoMunicipality::new);
	}
	public String getMunicipalityNameIn(final Language lang) {
		return _municipality != null ? _municipality.getNameIn(lang) : null;
	}
	public void setMunicipalityName(final String name,final Language lang) {
		_municipality = _setName(_municipality,name,lang,
								 GeoMunicipality::new);
	}
	//////// Locality
	public GeoLocationItem<GeoLocalityID> getLocalityItemIn(final Language lang) {
		return _locality != null ? GeoLocationItem.normalized(_locality.getId())
												  .withName(_locality.getNameIn(lang))
								 : null;
	}
	public GeoLocalityID getLocalityId() {
		return _locality != null ? _locality.getId() : null;
	}
	public void setLocalityId(final GeoLocalityID id) {
		_locality = _setId(_locality,id,
						   GeoLocality::new);
	}
	public String getLocalityNameIn(final Language lang) {
		return _locality != null ? _locality.getNameIn(lang) : null;
	}
	public void setLocalityName(final String name,final Language lang) {
		_locality = _setName(_locality,name,lang,
							 GeoLocality::new);
	}
	//////// District
	public GeoLocationItem<GeoDistrictID> getDistrictItemIn(final Language lang) {
		return _district != null ? GeoLocationItem.normalized(_district.getId())
												  .withName(_district.getNameIn(lang))
								 : null;
	}
	public GeoDistrictID getDistrictId() {
		return _district != null ? _district.getId() : null;
	}
	public void setDistrictId(final GeoDistrictID id) {
		_district = _setId(_district,id,
						   GeoDistrict::new);
	}
	public String getDistrictNameIn(final Language lang) {
		return _district != null ? _district.getNameIn(lang) : null;
	}
	public void setDistrictName(final String name,final Language lang) {
		_district = _setName(_district,name,lang,
							 GeoDistrict::new);
	}
	//////// Neighborhood
	public GeoLocationItem<GeoNeighborhoodID> getNeighborhoodItemIn(final Language lang) {
		return _neighborhood != null ? GeoLocationItem.normalized(_neighborhood.getId())
												      .withName(_country.getNameIn(lang))
								     : null;
	}
	public GeoNeighborhoodID getNeighborhoodId() {
		return _neighborhood != null ? _neighborhood.getId() : null;
	}
	public void setNeighborhoodId(final GeoNeighborhoodID id) {
		_neighborhood = _setId(_neighborhood,id,
							   GeoNeighborhood::new);
	}
	public String getNeighborhoodNameIn(final Language lang) {
		return _neighborhood != null ? _neighborhood.getNameIn(lang) : null;
	}
	public void setNeighborhoodName(final String name,final Language lang) {
		_neighborhood = _setName(_neighborhood,name,lang,
								 GeoNeighborhood::new);
	}
	//////// Street
	public GeoLocationItem<GeoStreetID> getStreetItemIn(final Language lang) {
		return _street != null ? GeoLocationItem.normalized(_street.getId())
												.withName(_street.getNameIn(lang))
							   : null;
	}
	public GeoStreetID getStreetId() {
		return _street != null ? _street.getId() : null;
	}
	public void setStreetId(final GeoStreetID id) {
		_street = _setId(_street,id,
						 GeoStreet::new);
	}
	public String getStreetNameIn(final Language lang) {
		return _street != null ? _street.getNameIn(lang) : null;
	}
	public void setStreetName(final String name,final Language lang) {
		_street = _setName(_street,name,lang,
						   GeoStreet::new);
	}
	//////// Portal
	public GeoPortalID getPortalId() {
		return _portal != null ? _portal.getId() : null;
	}
	public void setPortalId(final GeoPortalID id) {
		_portal = _setId(_portal,id,
						 GeoPortal::new);
	}
	public String getPortalNameIn(final Language lang) {
		return _portal != null ? _portal.getNameIn(lang) : null;
	}
	public void setPortalName(final String name,final Language lang) {
		_portal = _setName(_portal,name,lang,
						   GeoPortal::new);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static class GeoPositionDetailedBuilder
	  		 implements IsBuilder {
		
		private final GeoPositionDetailed _geoPos;
		
		public GeoPositionDetailedBuilder withPosition(final GeoPosition2D pos) {
			_geoPos.setPosition(pos);
			return this;
		}
		////////// Territory
		public GeoPositionDetailedBuilder withTerritory(final GeoTerritory territory) {
			_geoPos.setTerritory(territory);
			return this;
		}
		////////// Country
		public GeoPositionDetailedBuilder withCountry(final GeoCountry country) {
			_geoPos.setCountry(country);
			return this;
		}
		////////// State
		public GeoPositionDetailedBuilder withState(final GeoState state) {
			_geoPos.setState(state);
			return this;
		}
		////////// County
		public GeoPositionDetailedBuilder withCounty(final GeoCounty county) {
			_geoPos.setCounty(county);
			return this;
		}
		////////// Region
		public GeoPositionDetailedBuilder withRegion(final GeoRegion region) {
			_geoPos.setRegion(region);
			return this;
		}
		////////// Municipality
		public GeoPositionDetailedBuilder withMunicipality(final GeoMunicipality mun) {
			_geoPos.setMunicipality(mun);
			return this;
		}
		////////// Locality
		public GeoPositionDetailedBuilder withLocality(final GeoLocality locality) {
			_geoPos.setLocality(locality);
			return this;
		}
		////////// District
		public GeoPositionDetailedBuilder withDistrict(final GeoDistrict dist) {
			_geoPos.setDistrict(dist);
			return this;
		}
		////////// Neighborhood
		public GeoPositionDetailedBuilder withNeighborhood(final GeoNeighborhood nh) {
			_geoPos.setNeighborhood(nh);
			return this;
		}
		////////// Street
		public GeoPositionDetailedBuilder withStreet(final GeoStreet street) {
			_geoPos.setStreet(street);
			return this;
		}
		////////// Portal
		public GeoPositionDetailedBuilder withPortal(final GeoPortal portal) {
			_geoPos.setPortal(portal);
			return this;
		}
		////////// zipcode
		public GeoPositionDetailedBuilder withZipCode(final GeoZipCode zipCode) {
			_geoPos.setZipCode(zipCode);
			return this;
		}
		////////// Other
		public GeoPositionDetailedBuilder withStairWay(final String stairWay) {
			_geoPos.setStairWay(stairWay);
			return this;
		}
		public GeoPositionDetailedBuilder withFloor(final String floor) {
			_geoPos.setFloor(floor);
			return this;
		}
		public GeoPositionDetailedBuilder withDoor(final String door) {
			_geoPos.setDoor(door);
			return this;
		}
		public GeoPositionDetailedBuilder withAddressText(final String addressText) {
			_geoPos.setAddressText(addressText);
			return this;
		}
		public GeoPositionDetailedBuilder withDirections(final LanguageTexts directions) {
			_geoPos.setDirections(directions);
			return this;
		}
		////////// Build
		public GeoPositionDetailed build() {
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
		if (!(o instanceof GeoPositionDetailed)) return false;
		GeoPositionDetailed other = (GeoPositionDetailed)o;
		
		boolean posEqs = this.getPosition() != null && other.getPosition() != null ? this.getPosition().equals(other.getPosition())
																				   : other.getPosition() != null ? false
																						  						 : true;	// both null
		boolean territoryEqs = _geoLocationEqs(this.getTerritory(),other.getTerritory());
		boolean countryEqs = _geoLocationEqs(this.getCountry(),other.getCountry());
		boolean stateEqs = _geoLocationEqs(this.getState(),other.getState());
		boolean countyEqs = _geoLocationEqs(this.getCounty(),other.getCounty());
		boolean regionEqs = _geoLocationEqs(this.getRegion(),other.getRegion());
		boolean municipalityEqs = _geoLocationEqs(this.getMunicipality(),other.getMunicipality());
		boolean localityEqs = _geoLocationEqs(this.getLocality(),other.getLocality());
		boolean districtEqs = _geoLocationEqs(this.getDistrict(),other.getDistrict());
		boolean neighborhoodEqs = _geoLocationEqs(this.getNeighborhood(),other.getNeighborhood());
		boolean streetEqs = _geoLocationEqs(this.getStreet(),other.getStreet());
		boolean zipCodeEqs = _equalsZipCode(this.getZipCode(),other.getZipCode());
		boolean stairwayEqs = _equalsString(this.getStairWay(),other.getStairWay());
		boolean floorEqs = _equalsString(this.getFloor(),other.getFloor());
		boolean doorEqs = _equalsString(this.getDoor(),other.getDoor());
		boolean addressEqs = _equalsString(this.getAddressText(),other.getAddressText());
		boolean directionsEqs = _equalsLanguageTexts(this.getDirections(),other.getDirections());
																							  							
		return posEqs
			&& territoryEqs && countryEqs && stateEqs && countyEqs && regionEqs && municipalityEqs && localityEqs && districtEqs && neighborhoodEqs && streetEqs
			&& zipCodeEqs
			&& stairwayEqs && floorEqs && doorEqs && addressEqs && directionsEqs;
		
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
	public static final Comparator<GeoPositionDetailed> LOOSE_COMPARATOR = (p1,p2) -> {
																				boolean eq = _geoLocationEqs(p1.getTerritory(),p2.getTerritory())
																						  && _geoLocationEqs(p1.getCountry(),p2.getCountry())
																						  && _geoLocationEqs(p1.getState(),p2.getState())
																						  && _geoLocationEqs(p1.getCounty(),p2.getCounty())
																						  && _geoLocationEqs(p1.getRegion(),p2.getRegion())
																						  && _geoLocationEqs(p1.getMunicipality(),p2.getMunicipality())
																						  && _geoLocationEqs(p1.getLocality(),p2.getLocality())
																						  && _geoLocationEqs(p1.getDistrict(),p2.getDistrict())
																						  && _geoLocationEqs(p1.getNeighborhood(),p2.getNeighborhood())
																						  && _geoLocationEqs(p1.getStreet(),p2.getStreet())
																						  && _equalsZipCode(p1.getZipCode(),p2.getZipCode())
																						  && _equalsString(p1.getStairWay(),p2.getStairWay())
																						  && _equalsString(p1.getFloor(),p2.getFloor())
																						  && _equalsString(p1.getDoor(),p2.getDoor())
																						  && _equalsString(p1.getAddressText(),p2.getAddressText());
																				return eq ? 0 : -1;
																		};
	@Override
	public int compareTo(final GeoPositionDetailed other) {
		return LOOSE_COMPARATOR.compare(this,other);
	}
	private static <I extends GeoID,G extends GeoLocationBase<I,G>> boolean _geoLocationEqs(final G i1,final G i2) {
		return i1 != null && i2 != null ? _equalsId(i1.getId(),i2.getId())
										: i1 != null ? false
													 : true;	// both null
	}
	private static <I extends GeoID> boolean _equalsId(final I i1,final I i2) {
		return i1 != null && i2 != null ? i1.is(i2)
										: i1 != null ? false
												     : true;	// both null
	}
	private static boolean _equalsZipCode(final GeoZipCode p1,final GeoZipCode p2) {
		return p1 != null ? p1.is(p2)
					  	  : p2 != null ? false
					  							 				   : true;	// both null
	}
	private static boolean _equalsString(final String str1,final String str2) {
		return str1 != null && str2 != null ? str1.equals(str2)
											: str1 != null ? false
														   : true;	// both null
	}
	private static boolean _equalsLanguageTexts(final LanguageTexts l1,final LanguageTexts l2) {
		return l1 != null && l2 != null ? l1.equals(l2)
										: l1 != null ? false
													 : true;	// both null		
	}
}
