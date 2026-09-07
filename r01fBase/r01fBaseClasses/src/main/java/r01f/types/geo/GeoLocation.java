package r01f.types.geo;

import java.io.Serializable;

import r01f.debug.Debuggable;
import r01f.facets.HasLangDependentName;
import r01f.locale.Language;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToDistrict;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToLocality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToNeighborhood;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToStreet;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoIDs.GeoID;

/**
 * Level    |Technical Term (ISO/Google) |Common Names (Examples)
 * ---------+----------------------------+-----------------------
 * Level 0  |Country                     |Country
 * 			|							 |Nation
 * 			|							 |Sovereign State
 * ---------+----------------------------+-----------------------
 * Level 1  |Administrative Area Level 1 |State
 * 			|							 |Province
 * 			|							 |Region
 * 			|							 |Territory
 * 			|							 |Land
 * ---------+----------------------------+-----------------------
 * Level 2  |Administrative Area Level 2 |County
 * 		    |							 |Department
 * 			|							 |District 
 * 			|							 |Prefecture
 * ---------+----------------------------+-----------------------
 * Level 3  |Administrative Area Level 3 |Municipality
 * 			|							 |City
 * 			|							 |Town
 * 			|							 |Commune
 * 			|							 |Borough
 * ---------+----------------------------+-----------------------
 * Sub-Local|Sublocality / Neighborhood  |Ward
 * 			|							 |Quarter
 * 			|							 |Neighborhood
 * 			|							 |Suburb
 */
public interface GeoLocation<GID extends GeoID> 
		 extends HasLangDependentName,
		 		 Debuggable,
				 Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public GID getId();
	public void setId(final GID id);
	
	public String getOfficialName();
	public void setOfficialName(final String name);
	
	@Override
	public String getNameIn(final Language lang);
	
	public GeoPosition2D getPosition2D();
	public void setPosition2D(final GeoPosition2D pos);
/////////////////////////////////////////////////////////////////////////////////////////
// Territory											Europe
//   |_Country											Spain										
//   	 |_State										Euskadi
//   		 |_County									Bizkaia
//   		 	|_Region								Gran Bilbao / valles alaveses
//   				|_Municipality						Bilbao
//  					|_Locality						Bilbao	
//   						|_District					01	
//   							|_Neighborhood 			Abando
//   								|_Street			General Concha
//   									|_portal		12
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isBelongsToTerritory();
	public GeoLocationBelongsToTerritory asBelongsToTerritory();
	
	public boolean isBelongsToCountry();
	public GeoLocationBelongsToCountry asBelongsToCountry();
	
	public boolean isBelongsToState();
	public GeoLocationBelongsToState asBelongsToState();
	
	public boolean isBelongsToCounty();
	public GeoLocationBelongsToCounty asBelongsToCounty();
	
	public boolean isBelongsToRegion();
	public GeoLocationBelongsToRegion asBelongsToRegion();
	
	public boolean isBelongsToMunicipality();
	public GeoLocationBelongsToMunicipality asBelongsToMunicipality();
	
	public boolean isBelongsToLocality();
	public GeoLocationBelongsToLocality asBelongsToLocality();
	
	public boolean isBelongsToDistrict();
	public GeoLocationBelongsToDistrict asBelongsToDistrict();
	
	public boolean isBelongsToNeighborhood();
	public GeoLocationBelongsToNeighborhood asBelongsToNeighborhood();
	
	public boolean isBelongsToStreet();
	public GeoLocationBelongsToStreet asBelongsToStreet();
}
