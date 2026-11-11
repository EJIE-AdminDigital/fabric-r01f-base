package r01f.types.contact;

import java.util.Collection;
import java.util.stream.Collectors;

import r01f.facets.Facet;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

public interface HasContactWebSites 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<ContactWeb> getContactWebSites();
	public void setContactWebSites(Collection<ContactWeb> contactWebSites);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default void addOrUpdateContactWebSite(final ContactWeb web) {
		if (web == null) return;
		
		if (this.getContactWebSites() == null) this.setContactWebSites(Lists.newArrayList());
		// check if ther exists another web for the same address
		ContactWeb other = this.getContactWebSiteFor(web.getUrl());
		// update the existing or add
		if (other != null) {
			other.updateFrom(web);
		} else {
			this.getContactWebSites()
				.add(web);
		}
	}
	public default void addOrUpdateContactWebSites(final Collection<ContactWeb> webs) {
		if (this.getContactWebSites() == null) this.setContactWebSites(Lists.newArrayList());
		if (CollectionUtils.hasData(webs)) {
			for (ContactWeb web : webs) {
				this.addOrUpdateContactWebSite(web);
			}
		}
	}
	public default ContactWeb removeContactWebSite(final ContactInfoUsage usage) {
		ContactWeb outWeb = IsContactMean.find(this.getContactWebSites(),usage);
		if (outWeb != null) this.getContactWebSites().remove(outWeb);
		return outWeb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are web site associated with the contact info
	 */
	public default boolean hasContactWebSites() {
		return CollectionUtils.hasData(this.getContactWebSites());
	}
	/**
	 *
	 * @return true if there are none private  web sites associated with the contact info
	 */
	public default boolean hasNonePrivateContactWebSites() {
		if (hasContactWebSites()) {
			for (ContactWeb contactWebSite : this.getContactWebSites()) {
				if (!contactWebSite.isPrivate()) {
					return true;
				}
			};
		}
		return false;
	}
	public default Collection<ContactWeb> getContactWebSitesWithDefaultOneFirst() {
		return IsContactMean.orderDefaultFirst(this.getContactWebSites());
	}
	/**
	 * Returns the first {@link ContactWeb} with the given url
	 * @param url
	 * @return
	 */
	public default ContactWeb getContactWebSiteFor(final Url url) {
		return CollectionUtils.hasData(this.getContactWebSites()) 
					? CollectionUtils.of(this.getContactWebSites())
									 .findFirstElementMatching(web -> web != null
											 					   && web.getUrl().equals(url))
					: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Collection} of {@link Url}s
	 * @return
	 */
	public default Collection<Url> getWebSiteUrls() {
		return  CollectionUtils.hasData(this.getContactWebSites())
					? this.getContactWebSites().stream()
									  .filter(web -> web.getUrl() != null)
									  .map(ContactWeb::getUrl)
									  .collect(Collectors.toList())
					: null;
	}
	/**
	 * Check if there exists a {@link ContactWeb} with the given email
	 * @param theSiteUrl
	 * @return
	 */
	public default boolean hasWebSite(final Url theSiteUrl) {
		return this.getWebSiteUrls()
				   .stream()
				   .filter(url -> url.equals(theSiteUrl))
         		   .count() > 0;
	}
	/**
	 * Returns the web urls as a comma separated list
	 * @param separator
	 * @return
	 */
	public default String getWebSiteUrlsCharSeparated(final char separator) {
		Collection<Url> urls = this.getWebSiteUrls();
		return CollectionUtils.hasData(urls)
					? CollectionUtils.of(urls)
									 .toStringSeparatedWith(separator)
				    : null;
	}
	/**
	 * Returns an url for an intended usage
	 * @param usage
	 * @return
	 */
	public default Url getWebSiteUrl(final ContactInfoUsage usage) {
		ContactWeb web = IsContactMean.find(this.getContactWebSites(),usage);
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns an url for an intended usage or any if the requested one
	 * does not exists
	 * @param usage
	 * @return
	 */
	public default Url getWebSiteUrlOrAny(final ContactInfoUsage usage) {
		Url outUrl = this.getWebSiteUrl(usage);
		if (outUrl == null && CollectionUtils.hasData(this.getContactWebSites())) {
			ContactWeb url = CollectionUtils.pickOneElement(this.getContactWebSites());
			if (url != null) outUrl = url.getUrl();
		}
		return outUrl;
	}
	/**
	 * Returns the default url address
	 * @return
	 */
	public default Url getDefaultWebSiteUrl() {
		ContactWeb web = IsContactMean.findDefault(this.getContactWebSites());
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns the default url or any of them if none is set as default one
	 * @return
	 */
	public default Url getDefaultWebSiteUrlOrAny() {
		ContactWeb web = IsContactMean.findDefault(this.getContactWebSites());
		if (web == null && CollectionUtils.hasData(this.getContactWebSites())) web = CollectionUtils.pickOneElement(this.getContactWebSites());
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns a web url other than default
	 * @return
	 */
	public default Url getWebSiteUrlOtherThanDefaul() {
		ContactWeb web = IsContactMean.findOtherThanDefault(this.getContactWebSites());
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns a web url (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public default Url getWebSiteUrl() {
		// Try to find the default
		Url defUrl = this.getDefaultWebSiteUrl();
		// ... if there's NO default, return any of them
		if (defUrl == null) {
			ContactWeb contactWeb = IsContactMean.findOne(this.getContactWebSites());
			defUrl = contactWeb != null ? contactWeb.getUrl() : null;
		}
		return defUrl;
	}
}