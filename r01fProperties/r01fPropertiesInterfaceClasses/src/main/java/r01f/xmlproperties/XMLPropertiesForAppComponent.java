package r01f.xmlproperties;

import lombok.experimental.Accessors;
import r01f.oids.CommonOIDs.IsAppCode;
import r01f.oids.CommonOIDs.IsAppComponent;
import r01f.patterns.FactoryFrom;
import r01f.properties.PropertiesForAppComponent;
import r01f.types.Path;

/**
 * Encapsulates properties for an app's component
 *
 * It can be injected like:
 * <pre class='brush:java'>
 * 		@Inject @XMLPropertiesComponent("default")
 * 		XMLPropertiesForAppComponent _componentProps;
 *
 * 		public someMethod() {
 * 			_componentProps.propertyAt(xPath);
 * 		}
 * </pre>
 * ... in order to do so, just create a guice Provider like:
 * <pre class='brush:java'>
 *		@Override
 *		public void configure(Binder binder) {
 *			// ... what ever
 *		}
 *		@Provides @XMLPropertiesComponent("myComponent")
 *		XMLPropertiesForAppComponent provideXMLPropertiesForAppComponent(final XMLProperties props) {
 *			XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("r01m")),
 *																								 "myComponent");
 *			return outPropsForComponent;
 *		}
 * </pre>
 */
@Accessors(prefix="_")
public class XMLPropertiesForAppComponent
	 extends PropertiesForAppComponent<XMLPropertyWrapper> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesForAppComponent(final IsAppCode appCode,final IsAppComponent appComp,
										final FactoryFrom<Path,XMLPropertyWrapper> xmlPropertiesWrapperFactory) {
		super(appCode,appComp,
			  xmlPropertiesWrapperFactory);
	}
}