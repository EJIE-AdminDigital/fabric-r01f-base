package r01f.configproperties;

import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableTypeBase;
import r01f.exceptions.EnrichedThrowableTypeBuilder;
import r01f.exceptions.ExceptionSeverity;
import r01f.xmlproperties.XMLPropertiesException;

/**
 * {@link XMLPropertiesException} types
 */
@Accessors(prefix="_")
public final class PropertiesErrorType
           extends EnrichedThrowableTypeBase {
	private static final long serialVersionUID = -4840066381003758879L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	private PropertiesErrorType(final String name,
								final int group,final int code,
								final ExceptionSeverity severity) {
		super(name,
			  group,code,
			  severity);
	}
	private static EnrichedThrowableTypeBuilder<PropertiesErrorType>.EnrichedThrowableTypeBuilderCodesStep withName(final String name) {
		return new EnrichedThrowableTypeBuilder<PropertiesErrorType>() {
						@Override
						protected PropertiesErrorType _build(@SuppressWarnings("hiding") final String name,
															  	final int group,final int code,
															  	final ExceptionSeverity severity) {
							return new PropertiesErrorType(name,
														      group,code,
														      severity);
						}
			   }.withName(name);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final PropertiesErrorType COMPONENTDEF_NOT_FOUND = PropertiesErrorType.withName("COMPONENTDEF_NOT_FOUND")
																					 .coded(2,1)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
	public static final PropertiesErrorType COMPONENTDEF_XML_MALFORMED = PropertiesErrorType.withName("COMPONENTDEF_XML_MALFORMED")
																					 .coded(2,2)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
	public static final PropertiesErrorType PROPERTIES_NOT_FOUND = PropertiesErrorType.withName("PROPERTIES_NOT_FOUND")
																					 .coded(2,3)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
	public static final PropertiesErrorType PROPERTIES_XML_MALFORMED = PropertiesErrorType.withName("PROPERTIES_XML_MALFORMED")
																					 .coded(2,4)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
}
