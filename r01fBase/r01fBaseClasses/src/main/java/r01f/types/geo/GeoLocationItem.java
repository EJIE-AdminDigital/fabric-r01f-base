package r01f.types.geo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.facets.HasName;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoIDs.GeoID;
import r01f.util.types.Objects;
import r01f.util.types.Strings;

@MarshallType(as="geoLocationItem")
@Accessors(prefix="_")
public class GeoLocationItem<GID extends GeoID>
  implements Serializable,
  			 HasName,
  			 Debuggable {

	private static final long serialVersionUID = 7882266717178676037L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * geo location oid
	 */
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GID _id;
	/**
	 * name
	 */
	@MarshallField(as="name",escape=true,
				   whenXml=@MarshallFieldAsXml(asParentElementValue = true))
	@Getter @Setter private String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoLocationItem() {
		// default no-args constructor
	}
	public GeoLocationItem(final GID id,final String name) {
		_id = id;
		_name = name;
	}
	public static <I extends GeoID> GeoLocationItem<I> normalized(final I id) {
		return new GeoLocationItem<>(id,null);
	}
	public static <I extends GeoID> GeoLocationItem<I> notNormalized(final String name) {
		return new GeoLocationItem<>(null,name);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NORMALIZED
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isNormalized() {
		return _id != null;
	}
	public boolean isNOTNormalized() {
		return !this.isNormalized();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final GeoLocationItem<GID> other) {
		return this.equals(other);
	}
	public boolean isNOT(final GeoLocationItem<GID> other) {
		return !this.is(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoLocationItem<GID> withName(final String name) {
		_name = name;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof GeoLocationItem)) return false;

		GeoLocationItem<?> other = (GeoLocationItem<?>)o;
		boolean idEq = this.getId() != null ? this.getId().is(other.getId())
											: other.getId() != null ? false
																	: true;	// both ids null
		boolean nameEq = this.getName() != null ? this.getName().equals(other.getName())
												: other.getName() != null ? false
																		  : true;	// both names null
		return this.isNormalized() && other.isNormalized() ? idEq
														   : this.isNOTNormalized() && other.isNOTNormalized() ? nameEq
																   											   : false;		// cannot be comparable
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_id,_name);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} > {}",
								  _id,_name);
	}

}
