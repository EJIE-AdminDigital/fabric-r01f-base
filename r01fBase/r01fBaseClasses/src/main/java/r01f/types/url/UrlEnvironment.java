package r01f.types.url;

import r01f.enums.EnumExtended;

/**
 * Environment for a {@link WebUrlBase}
 * Usually this interface is implemented as an {@link Enum} 
 * <pre class='brush:java'>
 * 		public enum MyEnvironment 
 * 		 implements WebUrlEnvironment<MyEnvironment>,
 * 									  EnumExtended<MyEnvironment> {
 * 			DES,
 * 			PRU,
 * 			PROD;
 * 		}
 * </pre>
 */
public interface UrlEnvironment<T extends Enum<T> & UrlEnvironment<T>> 
		 extends EnumExtended<T> {

}