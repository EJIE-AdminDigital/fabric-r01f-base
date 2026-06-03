package r01f.reflection.scanner;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import io.github.classgraph.ClassGraph;
import lombok.extern.slf4j.Slf4j;
import r01f.types.JavaPackage;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;



@Slf4j
abstract class ClasspathScannerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final ClassgraphScanHandler _scanHandler;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected ClasspathScannerBase(final ClassgraphScanHandler classGraphScanHandler) {
		_scanHandler = classGraphScanHandler;
		
		// BEWARE!!!
		// The org.reflections' ClasspathHelper.forPackage method, at the end does:
        // 		for (ClassLoader classLader : ClasspathHelper.classLoaders()) {
		//			Enumeration<URL> urls = classLoader.getResources(<change package dots for />);
		//			if (urls != null) convert the enumeration into a Collection<URL> and return
		// 		}
		// BUT for an unknown reason, when the package is INSIDE a JAR at APP-INF/lib,
		// classLoader.getResources(pckgName) returns null
		// WHAT TO DO???
		// see:
		//		http://www.javaworld.com/article/2077352/java-se/smartly-load-your-properties.html
		//		http://stackoverflow.com/questions/676250/different-ways-of-loading-a-file-as-an-inputstream
		// 		https://github.com/Atmosphere/atmosphere/issues/1229
		//		http://middlewaresnippets.blogspot.com.es/2011/05/class-loading-and-application-packaging.html
		//
		Collection<Path> pckgPaths = _pathForJavaPackages(this.getAcceptedJavaPackages());	// uses classLoader.getResources(-javaPackage-) to get a PATH

		if (CollectionUtils.isNullOrEmpty(pckgPaths)) log.error("Could NOT get any URL for packages {} from any classloader!!!",
					  											this.getAcceptedJavaPackages());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected Collection<JavaPackage> getAcceptedJavaPackages() {
		return _scanHandler.getAcceptedJavaPackages();
	}
	protected Collection<JavaPackage> getRejectedJavaPackages() {
		return _scanHandler.getRejectedJavaPackages();
	}
	protected Collection<Path> getAcceptedResourcePaths() {
		return _scanHandler.getAcceptedResourcePaths();
	}
	protected Collection<Path> getRejectedResourcePaths() {
		return _scanHandler.getRejectedResourcePaths();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected <T> Set<Class<? extends T>> _filterTypesInAcceptedPackagesAndResourcePaths(final Set<Class<? extends T>> types) {
		return CollectionUtils.hasData(types) ? types.stream()
													 .filter(type -> {
														 		boolean acceptedJavaPackage = true;
														 		if (CollectionUtils.hasData(this.getAcceptedJavaPackages())) {
														 			acceptedJavaPackage = false;
																	for (JavaPackage pckg : this.getAcceptedJavaPackages()) {
																		if (type.getPackage().getName().startsWith(pckg.asString())) {
																			acceptedJavaPackage = true;
																			break;
																		}
																	}
														 		}
																return acceptedJavaPackage;
															 })
													 .collect(Collectors.toUnmodifiableSet())
											   : Collections.emptySet();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	JAVA PACKAGES
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _resourceFromJavaPackage(final String name) {		
		if (name == null) return null;
		
		// replaces dots (.) by slashes (/)
		String outResourceName = name.replace(".","/");
		outResourceName = outResourceName.replace("\\","/");
		if (outResourceName.startsWith("/")) outResourceName = outResourceName.substring(1);
		return outResourceName;
	}
	protected static Path _pathForJavaPackage(final JavaPackage pckg) {
		Path path  = Path.from(_resourceFromJavaPackage(pckg.asString()));
		return path;
	}
	protected static Collection<Path> _pathForJavaPackages(final JavaPackage... pckgs) {
		return CollectionUtils.hasData(pckgs) ? _pathForJavaPackages(Lists.newArrayList(pckgs))
											  : Collections.emptyList();
	}
	public static Collection<Path> _pathForJavaPackages(final Collection<JavaPackage> pckgs) {
		return CollectionUtils.hasData(pckgs) ? pckgs.stream()
												     .map(pckg -> _pathForJavaPackage(pckg))
												     .toList()
											  : Collections.emptyList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLASSLOADERS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static ClassLoader[] _scanClassLoaders(final ClassLoader otherClassLoader) {
		ClassLoader[] outClassLoaders =	otherClassLoader != null ? _classLoaders(_staticClassLoader(),
											 						 			 _contextClassLoader(),
											 						 			 otherClassLoader)
																 : _classLoaders(_staticClassLoader(),
											 						 			_contextClassLoader());
		return outClassLoaders;
	}
	private static ClassLoader _staticClassLoader() {
		return  ClassGraph.class.getClassLoader();
	}
	private static ClassLoader _contextClassLoader() {
		 return Thread.currentThread().getContextClassLoader();
	}
	 /**
	 * Returns an array of class Loaders initialized from the specified array.
	 * <p>
	 * If the input is null or empty, it defaults to both {@link #contextClassLoader()} and {@link #staticClassLoader()}
	 *
	 * @return the array of class loaders, not null
	 */
	private static ClassLoader[] _classLoaders(final ClassLoader... classLoaders) {
		if (classLoaders != null && classLoaders.length != 0) {
			return classLoaders;
		}
		ClassLoader contextClassLoader = _contextClassLoader(), staticClassLoader = _staticClassLoader();
		return contextClassLoader != null ? staticClassLoader != null && contextClassLoader != staticClassLoader 
												? new ClassLoader[] { contextClassLoader,staticClassLoader } 
												: new ClassLoader[] { contextClassLoader } 
										  : new ClassLoader[] {};
	}
}
