package r01f.types;

import java.io.Serializable;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;

/**
 * Interface for types that can be represented as a {@link String}
 */
@MarshallPolymorphicTypeInfo(includeTypeInfo = MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE)	// this is important!! (see MarshallerPolymorphicTypes.java)
public interface CanBeRepresentedAsString
		 extends Serializable {
	public String asString();
}
