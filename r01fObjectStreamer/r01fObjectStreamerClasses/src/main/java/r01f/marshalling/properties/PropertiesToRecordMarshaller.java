package r01f.marshalling.properties;

import java.util.Map;
import java.util.Properties;


import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.patterns.Provider;
import tools.jackson.databind.ObjectMapper;

/**
 * Simple objects Properties to Record marshaller, for little utilities:
 *   f.e properties as Properties to convert to a Record 
 * public record JavaMailSenderProperties( SMTPProperties smtp,
										   AWSProperties aws,
										   RESTProperties rest) {
/////////////////////////////////////////////////////////////////////////////////////////
	
		/////////////////////////////////////////////////////////////////////////////////////////
		// RECORDS
		/////////////////////////////////////////////////////////////////////////////////////////                                                        
			    public record SMTPProperties (Host host, int port) implements  NotifierImplRecordProperties {
					public static SMTPProperties from(final Properties data) {				
						return _marshaller.forType(SMTPProperties.class).propertiesToRecord(data);				
					}}
			    
			    public record AWSProperties  (AWSAccessKey accessKey, AWSAccessSecret accessSecret) implements NotifierImplRecordProperties{	    	
			    	public static AWSProperties from(final Properties data) {	
			    		return _marshaller.forType(AWSProperties.class).propertiesToRecord(data);
				
					}}
			    
			    public record RESTProperties (Url endpoint) implements  NotifierImplRecordProperties {
			    	public static RESTProperties from(final Properties data) {			
			    		return _marshaller.forType(RESTProperties.class).propertiesToRecord(data);
					}
			    }
}
 * 
*/
@Accessors(prefix="_")
@NoArgsConstructor
public class PropertiesToRecordMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private ObjectMapper _mapper;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PropertiesToRecordMarshaller(final ObjectMapper mapper) {
		_mapper = mapper;
	}
	public PropertiesToRecordMarshaller(final Provider<ObjectMapper> mapperProvider) {
		_mapper = mapperProvider.provideValue();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FLUENT
/////////////////////////////////////////////////////////////////////////////////////////		
	public PropertiesToRecordMarshallerAsClass forType(final Class<?> type) {
		return new PropertiesToRecordMarshallerAsClass(type);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	Marshaller using a type
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor
	public class PropertiesToRecordMarshallerAsClass {
		private final Class<?> _type;
		
		@SuppressWarnings("unchecked")
		public <T> T propertiesToRecord(final Properties properties) {
			return _type != null ? (T) _mapper.convertValue(properties, _type)
								 : null;
		}
		public <T> Properties  recordToProperties(final T value) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = _mapper.convertValue(value, Map.class);	        
	        // [] pass map into Properties
	        Properties props = new Properties();
	        map.forEach((key, v) -> {
							            if (value != null) {
							                props.setProperty(key, v.toString());
							            }
	        });
	        return props;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Marshaller using a TypeRef
/////////////////////////////////////////////////////////////////////////////////////////	
}
