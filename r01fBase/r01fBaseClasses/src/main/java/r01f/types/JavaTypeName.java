package r01f.types;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a java type name
 */
public record JavaTypeName(String typeName) 
  implements CanBeRepresentedAsString,
			 Serializable {
	private static final long serialVersionUID = 7316410643001225993L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaTypeName {
		Objects.requireNonNull(typeName);
	}
	public JavaTypeName(final Class<?> type) {
		this(type.getName());
	}
	public static JavaTypeName of(final String name) {
		return new JavaTypeName(name);
	}
	public static JavaTypeName of(final Class<?> type) {
		return new JavaTypeName(type);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public String getTypeName() {
		return this.typeName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.typeName;
	}
	@Override
	public String toString() {
		return this.asString();
	}
}
