package r01f.types.contact;

import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasID;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoFacets.HasGeoPosition;
import r01f.types.geo.GeoPosition;
import r01f.util.types.collections.CollectionUtils;

/**
 * Contact info
 */
@MarshallType(as="contactInfo")
@Accessors(prefix="_")
public class ContactInfo
  implements Serializable,
  			 HasEMailAddresses,HasContactEMails,
  			 HasPhoneNumbers,HasContactPhones,
  			 HasContactSocialNetworks,
  			 HasContactWebSites,
  			 HasGeoPosition,
  			 HasID<ContactInfoID> {

	private static final long serialVersionUID = 8960930452114541680L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * an optional id
	 */
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ContactInfoID _id;
	/**
	 * true if the contact data is private and should not be publicly exposed
	 * (ie: opendata)
	 */
	@MarshallField(as="private",
				   whenXml=@MarshallFieldAsXml(attr=true))		
	@Getter @Setter protected boolean _private;
	/**
	 * Phones
	 */
	@MarshallField(as="phoneChannels")
	@Getter @Setter private Collection<ContactPhone> _contactPhones;
	/**
	 * Social network users
	 */
	@MarshallField(as="socialNetworkChannels")
	@Getter @Setter private Collection<ContactSocialNetwork> _contactSocialNetworks;
	/**
	 * email
	 */
	@MarshallField(as="emailChannels")
	@Getter @Setter private Collection<ContactEMail> _contactEMails;
	/**
	 * web sites
	 */
	@MarshallField(as="webSiteChannels")
	@Getter @Setter private Collection<ContactWeb> _contactWebSites;
	/**
	 * The language the user prefers to be used in the interaction with him/her
	 */
	@MarshallField(as="preferredLanguage")
	@Getter @Setter private Language _preferedLanguage;
	/**
	 * The geo-position
	 */
	@MarshallField(as="geoPosition")
	@Getter @Setter private GeoPosition _geoPosition;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfo() {
		// default no-args constructor
	}
	public ContactInfo(final ContactInfoID id) {
		_id = id;
	}
	public static ContactInfo create() {
		ContactInfo outContact = new ContactInfo();
		return outContact;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Do NOT expose contact info data 	
	 */
	public ContactInfo doNotExposeData() {
		_private = true;
		if (CollectionUtils.hasData(_contactPhones)) _contactPhones.forEach(phone -> phone.setPrivate(true));
		if (CollectionUtils.hasData(_contactEMails)) _contactEMails.forEach(mail -> mail.setPrivate(true));
		if (CollectionUtils.hasData(_contactWebSites)) _contactWebSites.forEach(web -> web.setPrivate(true));
		if (CollectionUtils.hasData(_contactSocialNetworks)) _contactSocialNetworks.forEach(net -> net.setPrivate(true));
		
		return this;
	}
	/**
	 * Expose contact info data 	
	 */
	public ContactInfo exposeData() {
		_private = false;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
////////// Phone
	public ContactInfo addPhone(final ContactPhone phone) {
		if (this.isPrivate() && phone != null) phone.setPrivate(true);
		this.addOrUpdateContactPhone(phone);
		return this;
	}
	public ContactInfo addPhones(final Collection<ContactPhone> phones) {
		this.addOrUpdateContactPhones(phones);
		return this;
	}
////////// Mail
	public ContactInfo addMail(final ContactEMail mail) {
		if (this.isPrivate() && mail != null) mail.setPrivate(true);
		this.addOrUpdateContactEMail(mail);
		return this;
	}
	public ContactInfo addMails(final Collection<ContactEMail> mails) {
		this.addOrUpdateContactEMails(mails);
		return this;
	}
////////// Social network
	public ContactInfo addSocialNetwork(final ContactSocialNetwork net) {
		if (this.isPrivate() && net != null) net.setPrivate(true);
		this.addOrUpdateContactSocialNetwork(net);
		return this;
	}
	public ContactInfo addSocialNetworks(final Collection<ContactSocialNetwork> nets) {
		this.addOrUpdateContactSocialNetworks(nets);
		return this;
	}
////////// WebSite
	public ContactInfo addWebSite(final ContactWeb web) {
		if (this.isPrivate() && web != null) web.setPrivate(true);
		this.addOrUpdateContactWebSite(web);		
		return this;
	}
	public ContactInfo addWebSites(final Collection<ContactWeb> webs) {
		this.addOrUpdateContactWebSites(webs);
		return this;
	}
}