package r01f.marshalling.properties;



import r01f.patterns.IsBuilder;
import tools.jackson.databind.ObjectMapper;

public class PropertiesToRecordMarshallerBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builder
	 * <b>BEWARE!</b>
	 * It's advisable to inject the JSonMarshaller using GUICE since the created object
	 * is cached as a singleton
	 * @return the {@link PropertiesToRecordMarshaller}
	 */
	public static ObjectMapper createMapper() {
		ObjectMapper object = new ObjectMapper();	
		return object;
	}
	public static PropertiesToRecordMarshaller create() {
		return new PropertiesToRecordMarshaller(PropertiesToRecordMarshallerBuilder.createMapper());
	}
}
