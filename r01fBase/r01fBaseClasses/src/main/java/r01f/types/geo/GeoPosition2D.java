package r01f.types.geo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.enums.EnumWithCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Objects;
import r01f.util.types.Strings;

/**
 * Latitude & longitude
 * <code>
 * 		GeoPosition2D geo = GeoPosition2D.at(lat,lon)
 * 										 .encodedUsing(ISO);
 * </code>
 */
@Immutable
@MarshallType(as="geoPosition2D")
@Accessors(prefix="_")
public class GeoPosition2D
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 3126318415213511386L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="standard",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoPositionStandard _standard;

	@MarshallField(as="x",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _x;

	@MarshallField(as="y",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _y;
/////////////////////////////////////////////////////////////////////////////////////////
//  STANDARDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static enum GeoPositionStandard 
			implements EnumWithCode<String,GeoPositionStandard> {	
		// https://epsg.io/
		GOOGLE("EPSG:4326"),		// WGS84 - World Geodetic System 1984, used in GPS 
		ETRS89("EPSG:25830"),		// ETRS89 / UTM zone 30N > Europe between 6°W and 0°W: Faroe Islands offshore; Ireland (accuracy: 1.0) 
		ED50("EPSG:4230");			// ED50 Europe
		
		@Getter private final Class<String> _codeType = String.class;
		@Getter private final String _code;
		
		public String getCrs() {
			return _code;
		}
		public static GeoPositionStandard fromCode(final String code) {
			return EnumWithCode.fromCode(code,GeoPositionStandard.class)
							   .orElseThrow(() -> new IllegalArgumentException("no " + GeoPositionStandard.class + " element with code=" + code));
		}
		public static GeoPositionStandard fromCrs(final String crs) {
			return GeoPositionStandard.fromCode(crs);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2D() {
		// default no-args constructor
	}
	public GeoPosition2D(final GeoPositionStandard standard) {
		_standard = standard != null ? standard : GeoPositionStandard.GOOGLE;
	}
	public GeoPosition2D(final GeoPositionStandard standard,
						 final double x,final double y) {
		this(standard);
		_x = x;
		_y = y;
	}
	public GeoPosition2D(final double x,final double y) {
		this(GeoPositionStandard.GOOGLE,
			 x,y);
	}
	public static GeoPosition2D at(final double x,final double y) {
		return new GeoPosition2D(x,y);
	}
	public static GeoPosition2D at(final double x,final double y,
								   final GeoPositionStandard std) {
		return new GeoPosition2D(std,
								 x,y);
	}
	public static GeoPosition2D valueOf(final String pos) {
		return GeoPosition2D.fromString(pos);
	}
	public static final String COORDINATE_AXIS_VALUE_PATTERN = "(?:[0-9]*[.])?[0-9]+";  // matches 123 / 123.456 and .456;

	public static final String GEOPOSITON2D_STANDARD_PATTERN_NO_CAPTURE = "EPSG\\:[0-9]+"; 	// matches the standard EPSG:4326 (google) / EPSG:25830 (ETRS89) / EPSG:4230 (ED50)
	
	public static final String GEOPOSITION2D_PATERN_CAPTURE = "(" +  GEOPOSITON2D_STANDARD_PATTERN_NO_CAPTURE + ")?" + 
															  "/?" + 
															  "(" + COORDINATE_AXIS_VALUE_PATTERN + "),(" + COORDINATE_AXIS_VALUE_PATTERN + ")";	
	// pattern: x,y (std)
	public static final Pattern FROM_STRING_PATTERN = Pattern.compile(GEOPOSITION2D_PATERN_CAPTURE);
	public static GeoPosition2D fromString(final String pos) {
		if (Strings.isNullOrEmpty(pos)) return null;
		
		Matcher m = FROM_STRING_PATTERN.matcher(pos);
		return m.find() ? new GeoPosition2D(Strings.isNOTNullOrEmpty(m.group(1)) ? GeoPositionStandard.valueOf(m.group(3)) 
																	    		 : null, // defaults to google,
											Double.parseDouble(m.group(2)),Double.parseDouble(m.group(3)))
						: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2D encodedUsing(final GeoPositionStandard std) {
		_standard = std;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return Strings.customized("{}/{},{}",
								  _standard.getCrs(),
								  _x,_y);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASH CODE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isX0AndY0() {
		return _x == 0 && _y == 0;
	}
	public boolean isNOTX0AndY0() {
		return !this.isX0AndY0();
	}
	public boolean is(final GeoPosition2D other) {
		return this.equals(other);
	}
	public boolean isNOT(final GeoPosition2D other) {
		return !this.is(other);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_standard,
								_x,_y);
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (this.getClass() != obj.getClass()) return false;
		GeoPosition2D other = (GeoPosition2D)obj;
		if (_standard != other._standard) return false;
		if (Double.doubleToLongBits(_x) != Double.doubleToLongBits(other._x)) return false;
		if (Double.doubleToLongBits(_y) != Double.doubleToLongBits(other._y)) return false;
		return true;
	}
}
