package r01f.types.geo.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.Path;
import r01f.types.geo.GeoIDs.GeoID;
import r01f.util.types.Strings;

@Accessors(prefix="_")
     class GeoCacheKey
implements Serializable {

	private static final long serialVersionUID = -4512937449884457597L;

	////////// Fields
	@Getter private final Path _idPath;

	////////// Constructor
	protected GeoCacheKey(final Collection<GeoID> ids) {
		this(Path.from(ids.stream()
						  .map(GeoID::asString)
						  .collect(Collectors.joining("/"))));
	}
	GeoCacheKey(final Path idPath) {
		_idPath = idPath;
	}

	////////// ToString
	@Override
	public String toString() {
		return Strings.customized("{}",
								  this.getIdPath().asRelativeString());
	}
	////////// EQUALS & HASHCODE
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof GeoCacheKey)) return false;

		GeoCacheKey other = (GeoCacheKey)obj;
		return Objects.equals(this.getIdPath(),other.getIdPath());
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.getIdPath());
	}
}