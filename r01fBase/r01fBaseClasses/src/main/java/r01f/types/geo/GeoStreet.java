package r01f.types.geo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToDistrict;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToLocality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoIDs.GeoCountryID;
import r01f.types.geo.GeoIDs.GeoCountyID;
import r01f.types.geo.GeoIDs.GeoDistrictID;
import r01f.types.geo.GeoIDs.GeoLocalityID;
import r01f.types.geo.GeoIDs.GeoMunicipalityID;
import r01f.types.geo.GeoIDs.GeoRegionID;
import r01f.types.geo.GeoIDs.GeoStateID;
import r01f.types.geo.GeoIDs.GeoStreetCode;
import r01f.types.geo.GeoIDs.GeoStreetID;
import r01f.types.geo.GeoIDs.GeoStreetTypeID;
import r01f.types.geo.GeoIDs.GeoTerritoryID;
import r01f.util.types.Strings;

/**
 * Street
 * <pre>
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
 * <pre class='brush:java'>
 *		GeoStreet street = new GeoStreet(GeoStreetID.forId(34),
 *									     LanguageTexts.of(Language.SPANISH,"General Concha")
 *												      .addForLang(Language.ENGLISH,"General Concha"),
 *										 GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
   * O tambien:
 * <pre class='brush:java'>
 * 		GeoStreet street = GeoStreet.create(GeoStreetID.forId(34))
 * 								    .withNameInLang(Language.SPANISH,"Bilbao")
 * 								    .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *											 		 		   .setLocation(lat,lon);
 * </pre>
 */
@MarshallType(as="geoStreet")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoStreet
     extends GeoLocationBase<GeoStreetID,GeoStreet>
  implements GeoLocationBelongsToTerritory,
			 GeoLocationBelongsToCountry,
			 GeoLocationBelongsToState,
			 GeoLocationBelongsToCounty,
			 GeoLocationBelongsToRegion,
			 GeoLocationBelongsToMunicipality,	
	 		 GeoLocationBelongsToLocality,
	 		 GeoLocationBelongsToDistrict{

	private static final long serialVersionUID = 7121715785159226136L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField(as="territoryId",
					whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoTerritoryID _territoryId;
	
	@MarshallField(as="countryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountryID _countryId;
	
	@MarshallField(as="stateId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoStateID _stateId;
	
	@MarshallField(as="countyId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountyID _countyId;
	
	@MarshallField(as="regionId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoRegionID _regionId;
	
	@MarshallField(as="municipalityId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoMunicipalityID _municipalityId;
	
	@MarshallField(as="localityId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoLocalityID _localityId;
	
	@MarshallField(as="districtId",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoDistrictID _districtId;
	
	@MarshallField(as="streetTypeId")
	@Getter @Setter private GeoStreetTypeID _streetTypeId;
	
	@MarshallField(as="streetType")
	@Getter @Setter private String _streetType;
	
	@MarshallField(as="streetCode",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoStreetCode _streetCode;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoStreet(final GeoStreetID oid) {
		super(oid);
	}
	public GeoStreet(final GeoStreetID oid,
					 final String officialName,final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoStreet(final GeoStreetID oid,
					 final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoStreet(final GeoStreetID oid,
					 final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoStreet(final GeoStreetID oid,
					 final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
	public static GeoStreet create() {
		return new GeoStreet();
	}
	public static GeoStreet create(final GeoStreetID geoID) {
		GeoStreet outGeo = new GeoStreet();
		outGeo.setId(geoID);
		return outGeo;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getNameIn(final Language lang) {
		String name = super.getNameIn(lang);
		String prefix = _streetTypeId != null ? _streetTypeId.asString()
											  : _streetType != null ? _streetType
													  				: null;
		return prefix != null ? Strings.customized("{} ({})",name,prefix)
							  : name;
	}
}
