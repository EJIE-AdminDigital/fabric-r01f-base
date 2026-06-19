package r01f.properties;

/**
 * {@link PropertiesForAppComponent} load listener
 */
 public interface PropertiesForAppComponentPropertiesLoadedListener {
	/**
	 * a new component has been loaded
	 * @param def component definition
	 */
	public void newComponentLoaded(PropertiesComponentDef def);
}
