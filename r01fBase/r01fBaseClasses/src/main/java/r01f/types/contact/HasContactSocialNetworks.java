package r01f.types.contact;

import java.util.Collection;

import r01f.facets.Facet;
import r01f.oids.SecurityIDs.LoginID;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

public interface HasContactSocialNetworks 
	     extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<ContactSocialNetwork> getContactSocialNetworks();
	public void setContactSocialNetworks(Collection<ContactSocialNetwork> socialNets);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default void addOrUpdateContactSocialNetwork(final ContactSocialNetwork net) {
		if (net == null) return;
		
		if (this.getContactSocialNetworks() == null) this.setContactSocialNetworks(Lists.newArrayList());
		// check if there exists another social net with the same user
		ContactSocialNetwork other = this.getContactSocialNetworkFor(net.getSocialNetworkType(),net.getUser());
		// update the existing or add
		if (other != null) {
			other.updateFrom(net);
		} else {
			this.getContactSocialNetworks()
				.add(net);
		}
	}
	public default void addOrUpdateContactSocialNetworks(final Collection<ContactSocialNetwork> nets) {
		if (this.getContactSocialNetworks() == null) this.setContactSocialNetworks(Lists.newArrayList());
		if (CollectionUtils.hasData(nets)) {
			for (ContactSocialNetwork net : nets) {
				this.addOrUpdateContactSocialNetwork(net);
			}
		}
	}
	public default ContactSocialNetwork removeContactSocialNetwork(final ContactInfoUsage id) {
		ContactSocialNetwork outNet = IsContactMean.find(this.getContactSocialNetworks(),id);
		if (outNet != null) this.getContactSocialNetworks()
								.remove(outNet);
		return outNet;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are social network associated with the contact info
	 */
	public default boolean hasContactSocialNetworks() {
		return CollectionUtils.hasData(this.getContactSocialNetworks());
	}
	/**
	 *
	 * @return true if there are none private social networks associated with the contact info
	 */
	public default boolean hasNonePrivateContactSocialNetworks() {
		if (hasContactSocialNetworks()) {
			for (ContactSocialNetwork contactSocialNetwork : this.getContactSocialNetworks()) {
				if (!contactSocialNetwork.isPrivate()) {
					return true;
				}
			};
		}
		return false;
	}
	public default boolean hasContactSocialNetwork(final ContactSocialNetworkType type,
												   final LoginID user) {
		return this.getContactSocialNetworks().stream()
									 .filter(socialNet -> socialNet != null
												       && socialNet.getSocialNetworkType() == type
												       && socialNet.getUser().is(user))
			         				 .count() > 0;
	}
	public default Collection<ContactSocialNetwork> getContactSocialNetworksWithDefaultOneFirst() {
		return IsContactMean.orderDefaultFirst(this.getContactSocialNetworks());
	}
	/**
	 * Returns the first {@link ContactSocialNetwork} with the given type for the given user
	 * @param type
	 * @param user
	 * @return
	 */
	public default ContactSocialNetwork getContactSocialNetworkFor(final ContactSocialNetworkType type,
																   final LoginID user) {
		return CollectionUtils.hasData(this.getContactSocialNetworks())
					? CollectionUtils.of(this.getContactSocialNetworks())
									 .findFirstElementMatching(socialNet -> socialNet != null
															  			 && socialNet.getSocialNetworkType() == type
															  			 && socialNet.getUser().is(user))
					: null;
	}
	/**
	 * Returns a social network for an intended usage
	 * @param type
	 * @param usage
	 * @return
	 */
	public default ContactSocialNetwork getContactSocialNetwork(final ContactSocialNetworkType type,
								  				 		 		final ContactInfoUsage usage) {
		return CollectionUtils.hasData(this.getContactSocialNetworks())
						? CollectionUtils.of(this.getContactSocialNetworks())
								     	 .findFirstElementMatching(el -> el.getUsage() == usage
																	  && el.getSocialNetworkType() == type)
						: null;
	}
	/**
	 * Returns the default social network
	 * @return
	 */
	public default ContactSocialNetwork getDefaultContactSocialNetwork() {
		ContactSocialNetwork net = IsContactMean.findDefault(this.getContactSocialNetworks());
		return net;
	}
	/**
	 * Returns the default social network or any of them if any is set as the default one
	 * @return
	 */
	public default ContactSocialNetwork getDefaultContactSocialNetworkOrAny() {
		ContactSocialNetwork net = IsContactMean.findDefault(this.getContactSocialNetworks());
		if (CollectionUtils.hasData(this.getContactSocialNetworks())) net = CollectionUtils.pickOneElement(this.getContactSocialNetworks());
		return net;
	}
}