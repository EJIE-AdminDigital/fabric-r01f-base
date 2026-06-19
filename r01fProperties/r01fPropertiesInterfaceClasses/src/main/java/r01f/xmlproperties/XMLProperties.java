package r01f.xmlproperties;

import r01f.properties.PropertiesBase;

/**
 * {@link XMLProperties} is the main component of the R01's properties systems
 * Properties have two levels
 * <ol>
 * 		<li>By application code</li>
 * 		<li>By component (module) within an application</li>
 * </ol>
 * Properties are defined in XML format by app code / module; this fact means that for a given
 * application code any number of modules may be defined
 * Example of an XML properties file:
 * <pre class="brush:xml">
 *          <misProps>
 *              <miProp>el valor</miProp>
 *              <miOtraProp value='a'></miOtraProp>
 *              <miOtraLista>
 *              	<prop1>value1</prop1>
 *              	<prop2>value2</prop2>
 *             	</miOtraLista>
 *          </misProps>
 * </pre>
 *
 * Example of how application properties may be distributed in multiple XML files by module
 * <pre>
 * 		- 'main' 			-- general properties
 * 		- 'contentServer' 	-- content server related properties
 * 		- etc
 * </pre>
 *
 * Each of the properties XML might be stored in a different place_
 * ie:
 * <pre>
 * 		- 'main' 			-- stored in a file within the classpath
 * 		- 'contentServer' 	-- stored in the BBDD
 * 		- etc
 * </pre>
 *
 * <h2>COMPONENT LOADING</h2>
 * <hr/>
 * In order to a component to be loaded, the load method must be set before
 * (see {@link PropertiesComponentDef})
 * <ol>
 * 	 	<li>The classpath MUST contain a folder named <b>{appCode}/components</b></li>
 * 		<li>Inside the <b>{appCode}/components</b> folder there MUST be a XML file for each component<br/>
 * 			<pre>
 * 				ie: If the appCode is xxx and it have two components (foo and bar) a xxx/components folder MUST exists in the classpath containing
 * 				    TWO xml files:
 * 						- xxx/components/xxx.foo.xml
 * 						- xxx/components/xxx.bar.xml
 * 			</pre>
 * 			Think about these files as an index or a definition of how / from where to load the real XML properties file
 * 			(ie: if the xml properties are real filesystem files, the component file contains the real path of the file
 * 				 if the xml properties are stored at a database table row, the component file contains how to connect to de bd and the table / row selection
 * 		</li>
 * </ol>
 * <h3>Environment dependent component loading</h3>
 * A system var named r01Env can be set (as a jvm argument -Dr01Env=loc or by means of System.setProperty("r01Env","loc"))
 * This var can set an environment where the components are loaded: it the r01Env var exists, the component loading will not be done from /{appCode}/components,
 * the components will be searched at: /{appCode}/components/{r01Env}/
 *
 * <h2>XMLProperties usage</h2>
 * <hr/>
 * properties stored at XMLProperties files are retrieved using xpath sentences issued at a {@link XMLPropertiesForApp} object
 * <pre class="brush:java">
 * 		XMLPropertiesForApp props = ...
 * 		props.of("componente").at("misProps/miProp").asString("defaultValue");
 * 		props.of("componente").at("miOtraProp/@value").asString();
 * </pre>
 *
 * <h2>Property Caching</h2>
 * A high level vision of the cache system is:
 * <pre>
 * 		XMLProperties  > has a cache of app properties: app - XMLPropertiesForApp
 * 			|
 * 			|_XMLPropertiesForApp  > has a cache of app component properties: appComponent - XMLPropertiesForAppComponent
 * 				|
 * 				|_XMLPropertiesForAppComponent  > has a cache of individual properties
 * 	</pre>
 *
 * <h2>Property loading</h2>
 * In order to get a {@link XMLPropertiesForApp} object:
 * <pre class="brush:java">
 * 		// [1] Create an XMLProperties object
 * 		XMLProperties props = XMLProperties.create()
 * 										   .notUsingCache();	// or usingCache()
 * 		// [2] Get the properties for an application
 * 		XMLPropertiesForApp appProps = props.forApp(AppCode.forId("xx"),1000);
 * 		// [3] Access the properties of a component
 * 		String prop = props.forComponent(component)
 * 						   .propertyAt(xPath)
 * 						   .asString()
 * 		// Or even simpler
 * 		String prop = XMLProperties.createForAppComponent(appCode,component)
 * 								   .notUsingCache()
 * 								   .propertyAt(xPath)
 * 								   .asString();
 * </pre>
 * <b>IMPORTANT</b>As exposed above there exists 3 cache levels, so it's advisable to have
 * 					a single instance of the {@link XMLProperties} object
 * 				    (ie: manage it as a guice singleton or make it static)
 *
 * <pre>
 */
public interface XMLProperties
	     extends PropertiesBase<XMLPropertiesForApp,XMLPropertiesForAppComponent> {
	// just extend
}
