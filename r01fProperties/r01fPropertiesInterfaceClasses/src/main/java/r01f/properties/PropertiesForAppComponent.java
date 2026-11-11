package r01f.properties;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.oids.CommonOIDs.IsAppCode;
import r01f.oids.CommonOIDs.IsAppComponent;
import r01f.patterns.FactoryFrom;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * Encapsulates properties for an app's component
 *
 * It can be injected like:
 * <pre class='brush:java'>
 * 		@Inject @XMLPropertiesComponent("default")
 * 		XMLPropertiesForAppComponent _componentProps;
 *
 * 		public someMethod() {
 * 			_componentProps.propertyAt(path);
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
public abstract class PropertiesForAppComponent<W extends PropertyWrapper<?>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final IsAppCode _appCode;
	@Getter protected final IsAppComponent _appComponent;

	protected final FactoryFrom<Path,W> _properyWrapperFactory;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected PropertiesForAppComponent(final IsAppCode appCode,final IsAppComponent appComp,
									 	final FactoryFrom<Path,W> propertiesWrapperFactory) {
		_appCode = appCode;
		_appComponent = appComp;
		_properyWrapperFactory = propertiesWrapperFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTY ACCESSOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieves a property from it's path in the XMLProperties file
	 * @param path the path
	 * @return a property wrapper that offers different type retrieving
	 */
	public W propertyAt(final Path path) {
		W prop = _properyWrapperFactory.from(path);
		return prop;
	}
	/**
	 * Retrieves a property from it's path in the XMLProperties file
	 * @param path the path
	 * @return a property wrapper that offers different type retrieving
	 */
	public W propertyAt(final String path) {
		return this.propertyAt(Path.from(path));
	}
	/**
	 * Retrieves a property from it's path in the XMLProperties file
	 * @param pathWithPlaceHolders a path sentence with placeholders that are replaced by vars
	 * @param vars the vars
	 * @return a property wrapper that ofers different type retrieving
	 */
	public W propertyAt(final String pathWithPlaceHolders,final String... vars) {
		String thepath = Strings.customized(pathWithPlaceHolders,
											vars);
		return this.propertyAt(thepath);
	}
	/**
	 * Retrieves a property from it's path in the XMLProperties file
	 * @param pathWithPlaceHolders a path sentence with placeholders that are replaced by vars
	 * @param vars the vars
	 * @return a property wrapper that ofers different type retrieving
	 */
	public W propertyAt(final Path pathWithPlaceHolders,final String... vars) {
		return this.propertyAt(pathWithPlaceHolders.asString(),vars);
	}
}
