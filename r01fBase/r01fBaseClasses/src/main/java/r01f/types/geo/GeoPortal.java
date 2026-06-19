package r01f.types.geo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToLocality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToStreet;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoIDs.GeoCountryID;
import r01f.types.geo.GeoIDs.GeoCountyID;
import r01f.types.geo.GeoIDs.GeoLocalityID;
import r01f.types.geo.GeoIDs.GeoMunicipalityID;
import r01f.types.geo.GeoIDs.GeoPortalID;
import r01f.types.geo.GeoIDs.GeoRegionID;
import r01f.types.geo.GeoIDs.GeoStateID;
import r01f.types.geo.GeoIDs.GeoStreetID;
import r01f.types.geo.GeoIDs.GeoTerritoryID;
import r01f.util.types.Strings;

/**
 * Country data
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
 *		GeoPortal portal = new GeoPortalID(GeoPortalID.forId(34),
 *										   LanguageTexts.of(Language.SPANISH,"Portal 5")
 *													 	.addForLang(Language.ENGLISH,"5th portal"),
 *											GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *													     .setLocation(lat,lon));
 * </pre>
 * or
 * <pre class='brush:java'>
 * 		GeoPortal portal = GeoPortal.create(R01MGeoCountryID.forId(34))
 * 									.withNameInLang(Language.SPANISH,"Portal 5")
 * 									.positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *													 		   .setLocation(lat,lon);
 * </pre>
 */
@MarshallType(as="geoPortal")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoPortal
     extends GeoLocationBase<GeoPortalID,GeoPortal>  
  implements GeoLocationBelongsToTerritory,
			 GeoLocationBelongsToCountry,
			 GeoLocationBelongsToState,
			 GeoLocationBelongsToCounty,
			 GeoLocationBelongsToRegion,
			 GeoLocationBelongsToMunicipality,
			 GeoLocationBelongsToLocality,
			 GeoLocationBelongsToStreet{

	private static final long serialVersionUID = 3510450562804176082L;
	
	
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
	
	@MarshallField(as="streetId",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoStreetID _streetId;
	
	@MarshallField(as="postalCode",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _postalCode;
	
	@MarshallField(as="bis",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _bis;
	
	@MarshallField(as="block",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _block;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPortal(final GeoPortalID oid) {
		super(oid);
	}
	public GeoPortal(final GeoPortalID oid,
					 final String officialName,final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoPortal(final GeoPortalID oid,
					 final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoPortal(final GeoPortalID oid,
					 final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoPortal(final GeoPortalID oid,
					 final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoPortal create() {
		return new GeoPortal();
	}
	public static GeoPortal create(final GeoPortalID geoOid) {
		GeoPortal outGeo = new GeoPortal();
		outGeo.setId(geoOid);
		return outGeo;
	}
	
	public String getFullName() {
		StringBuilder fullName = new StringBuilder(getOfficialName());
		if (Strings.isNOTNullOrEmpty(getBis())) {
			fullName.append("-")
					.append(getBis());
		} else if (Strings.isNOTNullOrEmpty(getBlock())) {
			fullName.append("-")
					.append(getBlock());
		}
		return fullName.toString();
	}
}
