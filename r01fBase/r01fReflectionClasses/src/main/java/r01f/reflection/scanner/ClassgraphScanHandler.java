package r01f.reflection.scanner;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.JavaPackage;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class ClassgraphScanHandler
  implements AutoCloseable {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static int DEFAULT_NUMBER_OF_THREADS_USED_TO_SCAN = Runtime.getRuntime()
																	  .availableProcessors();
	public static String[] DEFAULT_REJECTED_PACKAGES  = {"java.","javax.","jakarta.",
														 "com.","org.",
														 "weblogic.","ejie.weblogic.*","oracle",
														 "apache.","software.","io.*",
														 "aopalliance.*"};
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Collection<JavaPackage> _acceptedJavaPackages;
	@Getter private final Collection<JavaPackage> _rejectedJavaPackages;

	@Getter private final Collection<Path> _acceptedResourcePaths;
	@Getter private final Collection<Path> _rejectedResourcePaths;

	@Getter private final ScanResult _scanResult;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public ClassgraphScanHandler(final Collection<JavaPackage> packagesToScan,
								 final ClassLoader... classLoaders) {
		this(packagesToScan,null,	// do NOT reject any package
			 null,null, 			// scan ALL paths
			 classLoaders);
	}
	public ClassgraphScanHandler(final Collection<JavaPackage> acceptedJavaPackages,
								 final Collection<Path> acceptedResourcePaths,
								 final ClassLoader... classLoaders) {
		this(acceptedJavaPackages,null,	// do NOT reject any package
			 acceptedResourcePaths,null,// do NOT reject any resource
			 classLoaders);
	}
	public ClassgraphScanHandler(final Collection<JavaPackage> acceptedJavaPackages,final Collection<JavaPackage> rejectedJavaPackages,
							   	 final Collection<Path> acceptedResourcePaths,final Collection<Path> rejectedResourcePaths,
							   	 final ClassLoader... classLoaders) {
		// [0] - Store config
		// store config
		_acceptedJavaPackages = acceptedJavaPackages;
		_rejectedJavaPackages = rejectedJavaPackages;

		_acceptedResourcePaths = acceptedResourcePaths;
		_rejectedResourcePaths = rejectedResourcePaths;

		// [1] - Configure
		ClassGraph classGraph =  new ClassGraph();

		//...optimization
		classGraph.disableModuleScanning();
		// Required Jar Scanning for SpringBoot Jar.
		//classGraph.disableNestedJarScanning();
		//classGraph.verbose();

		//...class loaders
		if (CollectionUtils.hasData(classLoaders)) {
			for (ClassLoader c : classLoaders ) {
				classGraph.addClassLoader(c);
			}
		}
		//...packages to scan & reject
		if (CollectionUtils.hasData(acceptedJavaPackages)) {
			String[] packagesToScanAString = acceptedJavaPackages.stream()
														   .map(p -> p.asString() )
														   .toArray(String[]::new);
			classGraph.acceptPackages(packagesToScanAString);
		}
		if (CollectionUtils.hasData(rejectedJavaPackages)) {
			String[] packagesToRejectAsString  =  rejectedJavaPackages.stream()
															   	  .map(p -> p.asString() )
															   	  .toArray(String[]::new);
			classGraph.rejectPackages(packagesToRejectAsString);
		} else {
			classGraph.rejectPackages(DEFAULT_REJECTED_PACKAGES);
		}
		//...resource paths to scan & reject
		if (CollectionUtils.hasData(acceptedResourcePaths)) {
			String[] pathsToScanAsString = acceptedResourcePaths.stream()
													  			.map( p -> p.asAbsoluteString() )
													  			.toArray(String[]::new);
			classGraph.acceptPaths(pathsToScanAsString);
		}
		if (CollectionUtils.hasData(rejectedResourcePaths)) {
			String[] pathsToScanAsString = rejectedResourcePaths.stream()
													  			.map( p -> p.asAbsoluteString() )
													  			.toArray(String[]::new);
			classGraph.rejectPaths(pathsToScanAsString);
		}

		// [2] - Scan (just get class info & annotations)
		_scanResult = classGraph.enableClassInfo()
				 				.enableAnnotationInfo()
				 				.scan(DEFAULT_NUMBER_OF_THREADS_USED_TO_SCAN);	// threads to use by default
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SCAN METHODS
/////////////////////////////////////////////////////////////////////////////////////////
  	/**
  	 * Finds all types implementing a given interface
  	 * @param implementedInterface
  	 * @return
  	 */
  	public Set<Class<?>> scanForTypesImplementing(final Class<?> implementedInterface) {
		ClassInfoList classInfoList = _scanForTypesImplementing(implementedInterface);
		return classInfoList.loadClasses()
							.stream()
							.collect(Collectors.toUnmodifiableSet());
  	}
  	private ClassInfoList _scanForTypesImplementing(final Class<?> implementedInterface) {
		ClassInfoList classInfoList = _scanResult.getClassesImplementing(implementedInterface.getName());
//												 .filter(ci -> !ci.isAbstract() 	// not a base type
//														    && !ci.isInterface() 	// not an interface
//														    && ci.isPublic());		// a public type
		return classInfoList;
  	}
  	/**
  	 * Find all types extending a given base type or interface
  	 * @param <T>
  	 * @param baseClass
  	 * @return
  	 */
  	@SuppressWarnings("unchecked")
  	public <T> Set<Class<? extends T >> scanForTypesExtending(final Class<T> baseClass) {
		ClassInfoList classInfoList = _scanForTypesExtending(baseClass);
		return classInfoList.loadClasses()
							.stream()
							.map(t -> (Class<? extends T>)t)	// force set
							.collect(Collectors.toUnmodifiableSet());
  	}
  	private <T> ClassInfoList _scanForTypesExtending(final Class<T> baseClass) {
		ClassInfoList classInfoList = _scanResult.getSubclasses(baseClass.getName());
		return classInfoList;
  	}
  	/**
  	 * Find all types extending or implementing a given base type or interface
  	 * @param <T>
  	 * @param superTypeOrIface
  	 * @return
  	 */
  	@SuppressWarnings("unchecked")
  	public <T> Set<Class<? extends T >> scanForTypesExtendingOrImplementing(final Class<T> superTypeOrIface) {
		ClassInfoList classInfoList = _scanForTypesExtendingOrImplementing(superTypeOrIface);
		return classInfoList.loadClasses()
							.stream()
							.map(t -> (Class<? extends T>)t)	// force set
							.collect(Collectors.toUnmodifiableSet());
  	}
  	private <T> ClassInfoList _scanForTypesExtendingOrImplementing(final Class<T> baseClass) {
		ClassInfoList classInfoList = null;
		// [ Important ]! Here Classgraph and org.Reflections differs.
		// With the subType method, org.reflection no differences interfaces and classes but classgraph do!!
		if (baseClass.isInterface()) {
			classInfoList = _scanForTypesImplementing(baseClass);	// _scanResult.getClassesImplementing(baseClass.getName());
		} else {
			classInfoList = _scanForTypesExtending(baseClass);		// _scanResult.getSubclasses(baseClass.getName());
		}
		return classInfoList;
  	}
  	/**
  	 * Finds all types annotated with a given annotation
  	 * @param annotation
  	 * @return
  	 */
  	public Set<Class<?>> scanForTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
		ClassInfoList classInfoList = _scanForTypesAnnotatedWith(annotation);
		return classInfoList.loadClasses()
							.stream()
							.collect(Collectors.toUnmodifiableSet());
  	}
  	private ClassInfoList _scanForTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
		ClassInfoList classInfoList = _scanResult.getClassesWithAnnotation(annotation.getName());
		return classInfoList;
  	}
  	/**
  	 * Finds all types implementing a given interface and annotated with a given annotation
  	 * @param implementedInterface
  	 * @param annotation
  	 * @return
  	 */
  	@SuppressWarnings("unchecked")
  	public <T> Set<Class<? extends T>> scanForTypesImplementing(final Class<?> implementedInterface,final Class<? extends Annotation> annotation) {
  		ClassInfoList implementing = _scanForTypesImplementing(implementedInterface);
  		ClassInfoList annotated = annotation != null ? _scanForTypesAnnotatedWith(annotation)
  													 : implementing;
  		return implementing.intersect(annotated)	// implementing & annotated
  						   .loadClasses()
						   .stream()
						   .map(t -> (Class<? extends T >)t)	// force set
						   .collect(Collectors.toUnmodifiableSet());
  	}
  	/**
  	 * Finds all types extending or implementing a a given base type or interface and annotated with a given annotation
  	 * @param superTypeOrIface
  	 * @param annotation
  	 * @return
  	 */
  	@SuppressWarnings("unchecked")
  	public <T> Set<Class<? extends T>> scanForTypesExtendingOrImplementing(final Class<T> superTypeOrIface,final Class<? extends Annotation> annotation) {
  		ClassInfoList extending = _scanForTypesExtendingOrImplementing(superTypeOrIface);
  		ClassInfoList annotated = annotation != null ? _scanForTypesAnnotatedWith(annotation)
  													 : extending;
  		return extending.intersect(annotated)
  						.loadClasses()
						.stream()
						.map(t -> (Class<? extends T >)t)	// force set
						.collect(Collectors.toUnmodifiableSet());
  	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
  	@Override
  	public void close() {
  		if (_scanResult != null) {
  			_scanResult.close();
  		}
  	}
}