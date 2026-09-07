package r01f.xmlproperties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.patterns.IsBuilder;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XMLPropertiesBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static XMLPropertiesCacheUsageStep create() {
		return XMLPropertiesBuilder.create((Environment)null);
	}
	public static XMLPropertiesCacheUsageStep create(final Environment env) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesCacheUsageStep(env);
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final AppCode appCode) {
		return XMLPropertiesBuilder.createForApp((Environment)null,appCode);
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final Environment env,
																 final AppCode appCode) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesForAppCacheUsageStep(env,
															   appCode);
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final String appCode) {
		return XMLPropertiesBuilder.createForApp((Environment)null,AppCode.forId(appCode));
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final Environment env,
																 final String appCode) {
		return XMLPropertiesBuilder.createForApp(env,AppCode.forId(appCode));
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final AppCode appCode,final AppComponent component) {
		return XMLPropertiesBuilder.createForAppComponent((Environment)null,
														  appCode,component);
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final Environment env,
																				   final AppCode appCode,final AppComponent component) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesForAppComponentCacheUsageStep(env,
																		appCode,component);
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final String appCode,final String component) {
		return XMLPropertiesBuilder.createForAppComponent((Environment)null,
														  AppCode.forId(appCode),AppComponent.forId(component));
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final Environment env,
																				   final String appCode,final String component) {
		return XMLPropertiesBuilder.createForAppComponent(env,
														  AppCode.forId(appCode),AppComponent.forId(component));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesCacheUsageStep {
		private final Environment _env;
		public XMLProperties notUsingCache() {
			return _buildXMLProperties(false);
		}
		public XMLProperties usingCache() {
			return _buildXMLProperties(true);
		}
		private XMLProperties _buildXMLProperties(final boolean useCache) {
			return new XMLPropertiesImpl(_env,
										 useCache);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesForAppCacheUsageStep {
		private final Environment _env;
		private final AppCode _appCode;
		public XMLPropertiesForApp notUsingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep(_env)
											.notUsingCache();
			return props.forApp(_appCode);
		}
		public XMLPropertiesForApp usingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep(_env)
											.usingCache();
			return props.forApp(_appCode);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesForAppComponentCacheUsageStep {
		private final Environment _env;
		private final AppCode _appCode;
		private final AppComponent _appComponent;
		public XMLPropertiesForAppComponent notUsingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep(_env)
											.notUsingCache();
			return props.forAppComponent(_appCode,_appComponent);
		}
		public XMLPropertiesForAppComponent usingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep(_env)
											.usingCache();
			return props.forAppComponent(_appCode,_appComponent);
		}
	}
}
