package r01f.properties;

/**
 * Designates a component that has properties for an app component
 */
public interface HasPropertiesForAppComponent<W extends PropertiesForAppComponent<? extends PropertyWrapper<?>>> {
	/**
	 * @return the properties
	 */
	public W getProperties();
}
