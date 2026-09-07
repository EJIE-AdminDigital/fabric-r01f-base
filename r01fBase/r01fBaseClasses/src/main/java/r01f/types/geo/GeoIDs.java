package r01f.types.geo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.util.types.Strings;
/**
 * Geo catalog model object's oids
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
 *   
 *   
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
 * </pre>
 * They're modeled after a long code that encapsulates the geo element (country, territory, street, etc code)
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class GeoIDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	base
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoID
			 extends OIDTyped<String> {
		public default long getCode() {
			return Long.parseLong(this.getId());
		}
		public default String asStringPaddedWithZeros(final int numZeros) {
			return Strings.leftPad(this.toString(),numZeros,'0');
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  oids
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Country
	 */
	@Immutable
	@MarshallType(as="geoCountryId")
	public record GeoCountryID(@Getter String id)
	   implements GeoID {
		public GeoCountryID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoCountryID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoCountryID forId(final String id) {
			return new GeoCountryID(id);
		}
		public static GeoCountryID forId(final long id) {
			return new GeoCountryID(id);
		}
		public static GeoCountryID valueOf(final String str) {
			return new GeoCountryID(Long.parseLong(str));
		}
		public static GeoCountryID fromString(final String str) {
			return new GeoCountryID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Territory: historic territory
	 */
	@Immutable
	@MarshallType(as="geoTerritoryId")
	public record GeoTerritoryID(@Getter String id)
	   implements GeoID {
		public GeoTerritoryID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoTerritoryID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoTerritoryID forId(final String id) {
			return new GeoTerritoryID(id);
		}
		public static GeoTerritoryID forId(final long id) {
			return new GeoTerritoryID(id);
		}
		public static GeoTerritoryID valueOf(final String str) {
			return new GeoTerritoryID(Long.parseLong(str));
		}
		public static GeoTerritoryID fromString(final String str) {
			return new GeoTerritoryID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * State: historic territory
	 */
	@Immutable
	@MarshallType(as="geoStateId")
	public record GeoStateID(@Getter String id)
	   implements GeoID {
		public GeoStateID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoStateID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoStateID forId(final String id) {
			return new GeoStateID(id);
		}
		public static GeoStateID forId(final long id) {
			return new GeoStateID(id);
		}
		public static GeoStateID valueOf(final String str) {
			return new GeoStateID(Long.parseLong(str));
		}
		public static GeoStateID fromString(final String str) {
			return new GeoStateID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * County: provincia
	 */
	@Immutable
	@MarshallType(as="geoCountyId")
	public record GeoCountyID(@Getter String id)
	   implements GeoID {
		public GeoCountyID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoCountyID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoCountyID forId(final String id) {
			return new GeoCountyID(id);
		}
		public static GeoCountyID forId(final long id) {
			return new GeoCountyID(id);
		}
		public static GeoCountyID valueOf(final String str) {
			return new GeoCountyID(Long.parseLong(str));
		}
		public static GeoCountyID fromString(final String str) {
			return new GeoCountyID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Region: comarca
	 */
	@Immutable
	@MarshallType(as="geoRegionId")
	public record GeoRegionID(@Getter String id)
	   implements GeoID {
		public GeoRegionID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoRegionID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoRegionID forId(final String id) {
			return new GeoRegionID(id);
		}
		public static GeoRegionID forId(final long id) {
			return new GeoRegionID(id);
		}
		public static GeoRegionID valueOf(final String str) {
			return new GeoRegionID(Long.parseLong(str));
		}
		public static GeoRegionID fromString(final String str) {
			return new GeoRegionID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Municipality
	 */
	@Immutable
	@MarshallType(as="geoMunicipalityId")
	public record GeoMunicipalityID(@Getter String id)
	   implements GeoID {
		public GeoMunicipalityID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoMunicipalityID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoMunicipalityID forId(final String id) {
			return new GeoMunicipalityID(id);
		}
		public static GeoMunicipalityID forId(final long id) {
			return new GeoMunicipalityID(id);
		}
		public static GeoMunicipalityID valueOf(final String str) {
			return new GeoMunicipalityID(Long.parseLong(str));
		}
		public static GeoMunicipalityID fromString(final String str) {
			return new GeoMunicipalityID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Locality
	 */
	@Immutable
	@MarshallType(as="geoLocalityId")
	public record GeoLocalityID(@Getter String id)
	   implements GeoID {
		public GeoLocalityID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoLocalityID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoLocalityID forId(final String id) {
			return new GeoLocalityID(id);
		}
		public static GeoLocalityID forId(final long id) {
			return new GeoLocalityID(id);
		}
		public static GeoLocalityID valueOf(final String str) {
			return new GeoLocalityID(Long.parseLong(str));
		}
		public static GeoLocalityID fromString(final String str) {
			return new GeoLocalityID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * DistrictCode
	 */
	@Immutable
	@MarshallType(as="geoDistrictCode")
	public record GeoDistrictCode(@Getter String id)
	   implements GeoID {
		public GeoDistrictCode(final long oid) {
			this(Long.toString(oid));
		}
		public GeoDistrictCode(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoDistrictCode forId(final String id) {
			return new GeoDistrictCode(id);
		}
		public static GeoDistrictCode forId(final long id) {
			return new GeoDistrictCode(id);
		}
		public static GeoDistrictCode valueOf(final String str) {
			return new GeoDistrictCode(Long.parseLong(str));
		}
		public static GeoDistrictCode fromString(final String str) {
			return new GeoDistrictCode(Long.parseLong(str));
		}		
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * District
	 */
	@Immutable
	@MarshallType(as="geoDistrictId")
	public record GeoDistrictID(@Getter String id,
								@MarshallIgnoredField
								Memoized<GeoCountyID> _memoCounty,
								@MarshallIgnoredField
								Memoized<GeoMunicipalityID> _memoMunicipality,
								@MarshallIgnoredField
								Memoized<Long> _memoNeighborhood)
	   implements GeoID {
		
		public GeoDistrictID(final String id) {
			this(id,
				 // county
				 Memoized.using(() -> {
									Matcher m = PATTERN.matcher(id);
									if (!m.find()) throw new IllegalArgumentException("District code " + id + " does NOT match " + PATTERN);
									return GeoCountyID.valueOf(m.group(1));
								}),
				 // municiplity
				 Memoized.using(() -> {
									Matcher m = PATTERN.matcher(id);
									if (!m.find()) throw new IllegalArgumentException("District code " + id + " does NOT match " + PATTERN);
									return GeoMunicipalityID.valueOf(m.group(2));
								}),
				 // neighborhood
				 Memoized.using(() -> {
									Matcher m = PATTERN.matcher(id);
									if (!m.find()) throw new IllegalArgumentException("District code " + id + " does NOT match " + PATTERN);
									return Long.parseLong(m.group(3));
								}));
		}
		
		public static GeoDistrictID forId(final String str) {
			return new GeoDistrictID(str);
		}
		public static GeoDistrictID valueOf(final String str) {
			return new GeoDistrictID(str);
		}
		public static GeoDistrictID from(final GeoCountyID countyId,
								         final GeoMunicipalityID municipalityId,
								         final long neigborhoodId) {
			return GeoDistrictID.forId(Strings.customized("{}{}{}{}",	// 4{2}{3}{8}
									  					  "4",
									  					  countyId.asString(),
									  					  municipalityId.asString(),
									  					  Strings.leftPad(Long.toString(neigborhoodId),8,'0')));
		}
		private static final transient Pattern PATTERN = Pattern.compile("2" +
																		 "([0-9]{2})" +		// county
																		 "([0-9]{3})" +		// municipality
																		 "([0-9]{8})");		// district
		public GeoCountyID getCountyId() {
			return _memoCounty.get();
		}
		public GeoMunicipalityID getMunicipalityId() {
			return _memoMunicipality.get();
		}
		public long getNeigborhoodId() {
			return _memoNeighborhood.get();
		}
		@Override
		public long getCode() {
			return this.getNeigborhoodId();
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(8);
		}
		@Override
		public String toString() {
			return this.asString();
		}
		////////// --- EQUALS & HASHCODE: use just the id field
		@Override
		public boolean equals(final Object other) {
			if (other == null) return false;
			if (other == this) return true;
			if (!this.getClass().isInstance(other)) return false;
			
			GeoDistrictID otherGeoDistrictId = (GeoDistrictID)other;
			return this.getId().equals(otherGeoDistrictId.getId());
		}
		@Override
		public int hashCode() {
			return this.getId().hashCode();
		}
	}
	/**
	 * District
	 */
	@Immutable
	@MarshallType(as="geoNeighborhoodId")
	public record GeoNeighborhoodID(@Getter String id,
								    @MarshallIgnoredField
								    Memoized<GeoCountyID> _memoCounty,
								    @MarshallIgnoredField
								    Memoized<GeoMunicipalityID> _memoMunicipality,
								    @MarshallIgnoredField
								    Memoized<Long> _memoNeighborhood)
	   implements GeoID {
		
		public GeoNeighborhoodID(final String id) {
			this(id,
				 // county
				 Memoized.using(() -> {
									Matcher m = PATTERN.matcher(id);
									if (!m.find()) throw new IllegalArgumentException("Neighborhood code " + id + " does NOT match " + PATTERN);
									return GeoCountyID.valueOf(m.group(1));
								}),
				 // municiplity
				 Memoized.using(() -> {
									Matcher m = PATTERN.matcher(id);
									if (!m.find()) throw new IllegalArgumentException("Neighborhood code " + id + " does NOT match " + PATTERN);
									return GeoMunicipalityID.valueOf(m.group(2));
								}),
				 // neighborhood
				 Memoized.using(() -> {
									Matcher m = PATTERN.matcher(id);
									if (!m.find()) throw new IllegalArgumentException("Neighborhood code " + id + " does NOT match " + PATTERN);
									return Long.parseLong(m.group(3));
								}));
		}
		
		public static GeoNeighborhoodID forId(final String str) {
			return new GeoNeighborhoodID(str);
		}
		public static GeoNeighborhoodID valueOf(final String str) {
			return new GeoNeighborhoodID(str);
		}
		public static GeoNeighborhoodID from(final GeoCountyID countyId,
								  		 	 final GeoMunicipalityID municipalityId,
								  		 	 final long neigborhoodId) {
			return GeoNeighborhoodID.forId(Strings.customized("{}{}{}{}",	// 4{2}{3}{8}
											  				  "4",
											  				  countyId.asString(),
											  				  municipalityId.asString(),
											  				  Strings.leftPad(Long.toString(neigborhoodId),8,'0')));
		}
		private static final transient Pattern PATTERN = Pattern.compile("4" +
																		 "([0-9]{2})" +		// county
																		 "([0-9]{3})" +		// municipality
																		 "([0-9]{8})");		// neighborhood
		public GeoCountyID getCountyId() {
			return _memoCounty.get();
		}
		public GeoMunicipalityID getMunicipalityId() {
			return _memoMunicipality.get();
		}
		public long getNeigborhoodId() {
			return _memoNeighborhood.get();
		}
		@Override
		public long getCode() {
			return this.getNeigborhoodId();
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(8);
		}
		@Override
		public String toString() {
			return this.asString();
		}
		////////// --- EQUALS & HASHCODE: use just the id field
		@Override
		public boolean equals(final Object other) {
			if (other == null) return false;
			if (other == this) return true;
			if (!this.getClass().isInstance(other)) return false;
			
			GeoNeighborhoodID otherN = (GeoNeighborhoodID)other;
			return this.getId().equals(otherN.getId());
		}
		@Override
		public int hashCode() {
			return this.getId().hashCode();
		}
	}
	/**
	 * Neighborhood
	 */
	@Immutable
	@MarshallType(as="geoNeighborhoodCode")
	public record GeoNeighborhoodCode(@Getter String id)
	   implements GeoID {
		public GeoNeighborhoodCode(final long oid) {
			this(Long.toString(oid));
		}
		public GeoNeighborhoodCode(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoNeighborhoodCode forId(final String id) {
			return new GeoNeighborhoodCode(id);
		}
		public static GeoNeighborhoodCode forId(final long id) {
			return new GeoNeighborhoodCode(id);
		}
		public static GeoNeighborhoodCode valueOf(final String str) {
			return new GeoNeighborhoodCode(Long.parseLong(str));
		}
		public static GeoNeighborhoodCode fromString(final String str) {
			return new GeoNeighborhoodCode(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Street Code
	 */
	@Immutable
	@MarshallType(as="geoStreetCode")
	public record GeoStreetCode(@Getter String id)
	   implements GeoID {
		public GeoStreetCode(final long oid) {
			this(Long.toString(oid));
		}
		public GeoStreetCode(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoStreetCode forId(final String id) {
			return new GeoStreetCode(id);
		}
		public static GeoStreetCode forId(final long id) {
			return new GeoStreetCode(id);
		}
		public static GeoStreetCode valueOf(final String str) {
			return new GeoStreetCode(Long.parseLong(str));
		}
		public static GeoStreetCode fromString(final String str) {
			return new GeoStreetCode(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Street
	 */
	@Immutable
	@MarshallType(as="geoStreetId")
	public record GeoStreetID(@Getter String id)
	   implements GeoID {
		public GeoStreetID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoStreetID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoStreetID forId(final long id) {
			return new GeoStreetID(id);
		}
		public static GeoStreetID valueOf(final String str) {
			return new GeoStreetID(Long.parseLong(str));
		}
		public static GeoStreetID fromString(final String str) {
			return new GeoStreetID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Street type
	 */
	@Immutable
	@MarshallType(as="geoStreetTypeId")
	public record GeoStreetTypeID(@Getter String id)
	   implements OIDTyped<String> {
		public static GeoStreetTypeID from(final String id) {
			return new GeoStreetTypeID(id);
		}
		public static GeoStreetTypeID forId(final String id) {
			return new GeoStreetTypeID(id);
		}
		public static GeoStreetTypeID valueOf(final String str) {
			return new GeoStreetTypeID(str);
		}
		public static GeoStreetTypeID fromString(final String id) {
			return new GeoStreetTypeID(id);
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
	/**
	 * Portal
	 */
	@Immutable
	@MarshallType(as="geoPortalId")
	public record GeoPortalID(@Getter String id)
	   implements GeoID {
		public GeoPortalID(final long oid) {
			this(Long.toString(oid));
		}
		public GeoPortalID(final Long oid) {
			this(Long.toString(oid));
		}
		public static GeoPortalID forId(final long id) {
			return new GeoPortalID(id);
		}
		public static GeoPortalID valueOf(final String str) {
			return new GeoPortalID(Long.parseLong(str));
		}
		public static GeoPortalID fromString(final String str) {
			return new GeoPortalID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
		@Override
		public String toString() {
			return this.asString();
		}
	}
	/**
	 * Zip (postal code)
	 */
	@Immutable
	@MarshallType(as="geoZipCode")
	public record GeoZipCode(@Getter String id)
	   implements OIDTyped<String> {
		public static GeoZipCode from(final String str) {
			return new GeoZipCode(str);
		}
		public static GeoZipCode forId(final String id) {
			return new GeoZipCode(id);
		}
		public static GeoZipCode valueOf(final String str) {
			return new GeoZipCode(str);
		}
		public static GeoZipCode fromString(final String str) {
			return new GeoZipCode(str);
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
