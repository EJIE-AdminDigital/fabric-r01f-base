package r01f.marshalling.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.OID;
import r01f.types.Range;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JacksonSerializers {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonSerialize(using = OIDJacksonSerializer.class)
	public static abstract class OIDJacksonAnnotationsMixin {
		@JsonUnwrapped abstract Object getId();
		@JsonIgnore abstract Object getRaw(); // we don't need it!
	}
	// Serialize oids as id:value
	// Jackson by default serializes OIDs like:
	// {
	// 		oid {
	// 			id : "xxxx"
	//	  }
	// }
	// ... but it's more elegant to serialize as:
	// {
	// 		oid : "xxx"
	// }
	@NoArgsConstructor
	public static class OIDJacksonSerializer
				extends ValueSerializer<OID> {
		@Override
		public void serialize(final OID value,
							  final JsonGenerator jgen,final SerializationContext ctx) throws JacksonException {
			jgen.writeString(value.asString());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonSerialize(using = RangeJacksonSerializer.class)
	public static abstract class RangeJacksonAnnotationsMixin {
		// nothing
	}
	@NoArgsConstructor
	public static class RangeJacksonSerializer
				extends ValueSerializer<Range<?>> {
		@Override
		public void serialize(final Range<?> value,
							  final JsonGenerator jgen,final SerializationContext ctx) throws JacksonException {
			jgen.writeStartObject();
			if (value.hasLowerBound()) jgen.writeStringProperty("start",value.getLowerBound().toString());
			if (value.hasUpperBound()) jgen.writeStringProperty("end",value.getUpperBound().toString());
			jgen.writeEndObject();
		}
	}
}
