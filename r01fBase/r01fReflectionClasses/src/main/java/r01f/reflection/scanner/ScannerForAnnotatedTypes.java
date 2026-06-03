package r01f.reflection.scanner;

import java.lang.annotation.Annotation;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * Usage:
 * <pre class='brush:java'>
 *	// [1] - create the scan handler
 *	ClassgraphScanHandler scanHandler = new ClassgraphScanHandler(acceptedJavaPackages,rejectedJavaPackages,
 *												 				  acceptedResourcePaths,rejectedResourcePaths,
 *				                                 				  classLoaders);
 *	// [2] - Create the scanner
 *	ScannerForAnnotatedTypes scanner = new ScannerForAnnotatedTypes(scanHandler);
 *	
 *	// [3] - Get types extending or implementing
 *	Set<? extends MyAnnotation> typesAnnotated = scanner.findTypesAnnotated(MyAnnotation.class);
 * </pre>
 */
@Slf4j
public class ScannerForAnnotatedTypes
	 extends ClasspathScannerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ScannerForAnnotatedTypes(final ClassgraphScanHandler classGraphScanHandler) {
		super(classGraphScanHandler);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds types annotated with a given annotation
	 * @param annotationType
	 * @return
	 */
	public <A extends Annotation> Set<Class<?>> findTypesAnnotated(final Class<A> annotationType) {
		log.info("...finding types annotated with {} at packages {} (BEWARE that every type between the type to be found and the supertype MUST be accesible in the package names list)",
				 annotationType,this.getAcceptedJavaPackages());
		Set<Class<?>> outAnnotatedTypes = _scanHandler.scanForTypesAnnotatedWith(annotationType);

		// When deploying at a WLS, the classes are inside a jar file whose URL is zip:/dominio_wls/servers/server1/tmp/_WL_user/myEAR/n5ymxm/APP-INF/lib/{myJar}.jar!/{the-package}
		// (see org.reflections.vfs.Vfs.DefaultUrlTypes)
		// ... so the scanned URLs loses the package part {myJar}.jar!/{the-package} and ALL jar classes are scanned
		// ... so filter to get just the ones in the required packages
		return _filterTypesInAcceptedPackagesAndResourcePaths(outAnnotatedTypes);
	}
}
