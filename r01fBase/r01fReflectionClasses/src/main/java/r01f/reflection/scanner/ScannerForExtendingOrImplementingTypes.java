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
 *	ScannerForExtendingOrImplementingTypes scanner = new ScannerForExtendingOrImplementingTypes(scanHandler);
 *
 *	// [3] - Get types extending or implementing
 *	Set<? extends MyBaseTypeOrInterface> typesExtendingOrImplementing = scanner.findTypesExtendingOrImplementing(MyBaseTypeOrInterface.class);
 * </pre>
 */
@Slf4j
public class ScannerForExtendingOrImplementingTypes
	 extends ClasspathScannerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ScannerForExtendingOrImplementingTypes(final ClassgraphScanHandler classGraphScanHandler) {
		super(classGraphScanHandler);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds types extending or implementing a given base type or interface
	 * @param superTypeOrIface
	 * @return
	 */
	public <T> Set<Class<? extends T>> findTypesExtendingOrImplementing(final Class<T> superTypeOrIface) {
		return this.findTypesExtendingOrImplementing(superTypeOrIface,null);	// do NOT require an annotation
	}
	/**
	 * Finds types extending or implementing a given base type or interface and annotated with the given annotation
	 * @param superTypeOrIface
	 * @param annotationType
	 * @return
	 */
	public <T> Set<Class<? extends T>> findTypesExtendingOrImplementing(final Class<T> superTypeOrIface,final Class<? extends Annotation> annotationType) {
		log.info("...finding subtypes of {} at packages {} (BEWARE that every type between the type to be found and the supertype MUST be accesible in the package names list)",
				 superTypeOrIface,this.getAcceptedJavaPackages());
		Set<Class<? extends T>> outSubTypes = _scanHandler.scanForTypesExtendingOrImplementing(superTypeOrIface,annotationType);

		// When deploying at a WLS, the classes are inside a jar file whose URL is zip:/dominio_wls/servers/server1/tmp/_WL_user/myEAR/n5ymxm/APP-INF/lib/{myJar}.jar!/{the-package}
		// (see org.reflections.vfs.Vfs.DefaultUrlTypes)
		// ... so the scanned URLs loses the package part {myJar}.jar!/{the-package} and ALL jar classes are scanned
		// ... so filter to get just the ones in the required packages
		return _filterTypesInAcceptedPackagesAndResourcePaths(outSubTypes);
	}
}
