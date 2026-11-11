package r01f.http.loadbalance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OID;
import r01f.oids.OIDTyped;
import r01f.types.url.Url;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class LoadBalancerIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static interface LoadBalancerID
				    extends OIDTyped<String> {
		@Override
		public default boolean isValid() {
			return this.getId() != null
			    && this.getId().length() <= OID.OID_LENGTH;
		}
	}
//	@Immutable
//	public static abstract class LoadBalancerIDBase
//	              		 extends OIDBaseMutable<String> 	// usually this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
//					  implements LoadBalancerID {
//
//		private static final long serialVersionUID = 4449032738491944411L;
//
//		public LoadBalancerIDBase() {
//			/* default no args constructor for serialization purposes */
//		}
//		public LoadBalancerIDBase(final String id) {
//			super(id);
//		}
//		@Override
//		public boolean isValid() {
//			return super.isValid() && this.getId().length() <= OID.OID_LENGTH;
//		}
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="loadBalancedServiceId")
	public record LoadBalancedServiceID(@Getter String id)
	   implements LoadBalancerID {

		public static LoadBalancedServiceID from(final String id) {
			return new LoadBalancedServiceID(id);
		}
		public static LoadBalancedServiceID forId(final String id) {
			return new LoadBalancedServiceID(id);
		}
		public static LoadBalancedServiceID valueOf(final String id) {
			return new LoadBalancedServiceID(id);
		}
		public static LoadBalancedServiceID fromString(final String id) {
			return new LoadBalancedServiceID(id);
		}
		public static LoadBalancedServiceID named(final String id) {
			return new LoadBalancedServiceID(id);
		}		
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * backend server id
	 */
	@Immutable
	@MarshallType(as="backendServerId")
	public record LoadBalancedBackEndServerID(@Getter String id) 
	   implements LoadBalancerID {

		public static LoadBalancedBackEndServerID from(final String id) {
			return new LoadBalancedBackEndServerID(id);
		}
		public static LoadBalancedBackEndServerID forId(final String id) {
			return new LoadBalancedBackEndServerID(id);
		}
		public static LoadBalancedBackEndServerID valueOf(final String id) {
			return new LoadBalancedBackEndServerID(id);
		}
		public static LoadBalancedBackEndServerID fromString(final String id) {
			return new LoadBalancedBackEndServerID(id);
		}
		public static LoadBalancedBackEndServerID from(final Url url) {
			return new LoadBalancedBackEndServerID(url.asString());
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
}
