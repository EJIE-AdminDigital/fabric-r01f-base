package r01f.xmlproperties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.patterns.IsBuilder;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XMLPropertiesBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static XMLPropertiesCacheUsageStep create() {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesCacheUsageStep();
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final AppCode appCode) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesForAppCacheUsageStep(appCode);
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final String appCode) {
		return XMLPropertiesBuilder.createForApp(AppCode.forId(appCode));
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final AppCode appCode,final AppComponent component) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesForAppComponentCacheUsageStep(appCode,component);
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final String appCode,final String component) {
		return XMLPropertiesBuilder.createForAppComponent(AppCode.forId(appCode),AppComponent.forId(component));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesCacheUsageStep {
		public XMLProperties notUsingCache() {
			return _buildXMLProperties(false);
		}
		public XMLProperties usingCache() {
			return _buildXMLProperties(true);
		}
		private XMLProperties _buildXMLProperties(final boolean useCache) {
			return new XMLPropertiesImpl(useCache);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesForAppCacheUsageStep {
		private final AppCode _appCode;
		public XMLPropertiesForApp notUsingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.notUsingCache();
			return props.forApp(_appCode);
		}
		public XMLPropertiesForApp usingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.usingCache();
			return props.forApp(_appCode);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesForAppComponentCacheUsageStep {
		private final AppCode _appCode;
		private final AppComponent _appComponent;
		public XMLPropertiesForAppComponent notUsingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.notUsingCache();
			return props.forAppComponent(_appCode,_appComponent);
		}
		public XMLPropertiesForAppComponent usingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.usingCache();
			return props.forAppComponent(_appCode,_appComponent);
		}
	}
}
