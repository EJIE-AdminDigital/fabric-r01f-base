package r01f.xmlproperties;

import java.io.IOException;

import r01f.configproperties.PropertiesComponentDefLoader;
import r01f.configproperties.PropertiesErrorType;
import r01f.exceptions.EnrichedRuntimeException;
import r01f.oids.CommonOIDs.Environment;
import r01f.oids.CommonOIDs.IsAppCode;
import r01f.oids.CommonOIDs.IsAppComponent;
import r01f.properties.PropertiesComponentDef;
import r01f.util.types.Strings;


public class XMLPropertiesException
	 extends EnrichedRuntimeException {

	private static final long serialVersionUID = -285396264237852297L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesException(final PropertiesErrorType type,
								  final String msg) {
		super(type,
			  msg);
	}
	public XMLPropertiesException(final PropertiesErrorType type,
								  final Throwable th) {
		super(type,
			  th);
	}
	public XMLPropertiesException(final PropertiesErrorType type,
								  final String msg,
								  final Throwable cause) {
		super(type,
			  msg,
			  cause);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static XMLPropertiesException componentDefLoadError(final Environment env,
															   final IsAppCode appCode,final IsAppComponent component) {
		return XMLPropertiesException.componentDefLoadError(env,appCode,component,
							   		  						null);
	}
	public static XMLPropertiesException componentDefLoadError(final Environment env,
															   final IsAppCode appCode,final IsAppComponent component,
															   final IOException ioEx) {
		String err = null;
		if (env == null) {
			err = Strings.customized("Error trying to load the component definition xml for appCode.component={}.{}; Ensure that the file {} is in the application classpath",
						 			 appCode,component,
						 			 PropertiesComponentDefLoader.componentDefFilePath(appCode,component));
		} else {
			err = Strings.customized("Error trying to load the component definition xml for env/appCode.component={}/{}.{}; Ensure that the file {} or {}  is in the application classpath",
						 			 env,appCode,component,
						 			 PropertiesComponentDefLoader.componentDefFilePath(appCode,component),
						 			 PropertiesComponentDefLoader.componentDefFilePath(env,
						 					 										   appCode, component));
		}
		return new XMLPropertiesException(PropertiesErrorType.COMPONENTDEF_NOT_FOUND,
										  err,
										  ioEx);
	}
	public static XMLPropertiesException propertiesLoadError(final Environment env,
															 final IsAppCode appCode,
															 final PropertiesComponentDef compDef) {
		String err = null;
		if (env == null) {
			err = Strings.customized("The XML properties file {} was NOT found for appCode.component={}.{}",
									 compDef != null ? Strings.customized("{} ({})",
											 							  compDef.getPropertiesFileURI(),
											 							  compDef.getLoaderDef() != null ? compDef.getLoaderDef().getLoader() : "unknown loader type")
											 		 : "the definition was NOT found",
									 appCode,compDef.getName());
		} else {
			err = Strings.customized("The XML properties file {} was NOT found for env/appCode.component={}/{}.{}",
									 compDef != null ? Strings.customized("{} ({})",
											 							  compDef.getPropertiesFileURI(),
											 							  compDef.getLoaderDef() != null ? compDef.getLoaderDef().getLoader() : "unknown loader type")
											 		 : "the definition was NOT found",
						  			 env,appCode,compDef.getName());
		}
		return new XMLPropertiesException(PropertiesErrorType.PROPERTIES_NOT_FOUND,
										  err);
	}
	public static XMLPropertiesException propertiesXMLError(final Environment env,
															final IsAppCode appCode,final IsAppComponent component) {
		String err = null;
		if (env == null) {
			err = Strings.customized("The properties XML for appCode.component={}.{} contains some error or is malformed",
						 			 appCode,component);
		} else {
			err = Strings.customized("The properties XML for env/appCode.component={}/{}.{} contains some error or is malformed",
						 			 env,appCode,component);
		}
		return new XMLPropertiesException(PropertiesErrorType.PROPERTIES_XML_MALFORMED,
										  err);
	}
}
