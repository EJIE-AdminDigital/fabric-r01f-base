package r01f.types.url;

import r01f.enums.EnumExtended;

/**
 * Security zones
 * Usually this interface is implemented as an {@link Enum}
 * <pre class='brush:java'>
 * 		public enum MySecurityZone implements WebUrlSecurityZone<MySecurityZone>,
 * 											  EnumExtended<MySecurityZone> {
 * 			INTRANET,
 * 			INTERNET;
 * 
 * 			private EnumExtendedWrapper<MySecurityZone> enums = new EnumExtendedWrapper<MySecurityZone>(MySecurityZone.values());
 * 			@Override
 * 			public boolean isIn(MySecurityZone... zones) {
 * 				return enums.isIn(zones);
 * 			}
 * 			@Override
 * 			public boolean is(MySecurityZone zone) {
 * 				return enums.is(zone);
 * 			}
 * 		}
 * </pre>
 */
public interface UrlSecurityZone<E extends Enum<E> & UrlSecurityZone<E>>
		 extends EnumExtended<E> {
	/**
	 * @return true if it's an EXTERNAL url (one that's not an intranet, extranet, euskadi.net, etc... url like www.google.com)
	 */
	public boolean isExternal();
}