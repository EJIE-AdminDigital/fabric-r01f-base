package r01f.types.contact;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.summary.Summary;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="personWithContact")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class PersonalDataWithContactInfo
  implements Serializable,
  			 HasSummaryFacet {

	private static final long serialVersionUID = 5346229482920059868L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Person data: name, surname, etc
	 */
	@MarshallField(as="personalData")
	@Getter @Setter private PersonalData _personalData;
	/**
	 * Contact Info
	 */
	@MarshallField(as="contactInfo")
	@Getter @Setter private ContactInfo _contactInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHOS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		Summary summary = _personalData != null ? _personalData.asSummarizable()
															   .getSummary()
										  : null;
		return SummarizableBuilder.summarizableFrom(summary);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTACT
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<ContactPhone> getContactPhones() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactPhones())) return Collections.emptyList();
		return _contactInfo.getContactPhones()
						   .stream()
						   .collect(Collectors.toList());
	}
	public Collection<Phone> getContactPhoneNumbers() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactPhones())) return Collections.emptyList();
		return _contactInfo.getContactPhones()
						   .stream()
						   .map(ContactPhone::getNumber)
						   .collect(Collectors.toList());
	}
	public Collection<ContactEMail> getContactMails() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactEMails())) return Collections.emptyList();
		return _contactInfo.getContactEMails()
						   .stream()
						   .collect(Collectors.toList());
	}
	public Collection<EMail> getContactMailAddresses() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactEMails())) return Collections.emptyList();
		return _contactInfo.getContactEMails()
						   .stream()
						   .map(ContactEMail::getMail)
						   .collect(Collectors.toList());
	}
	public Collection<ContactSocialNetwork> getContactSocialNetworks() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactSocialNetworks())) return Collections.emptyList();
		return _contactInfo.getContactSocialNetworks()
						   .stream()
						   .collect(Collectors.toList());
	}
	public Collection<Url> getContactSocialNetworkUrls() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactSocialNetworks())) return Collections.emptyList();
		return _contactInfo.getContactSocialNetworks()
						   .stream()
						   .map(ContactSocialNetwork::getProfileUrl)
						   .collect(Collectors.toList());
	}
	public Collection<ContactWeb> getContactWebSites() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactWebSites())) return Collections.emptyList();
		return _contactInfo.getContactWebSites()
						   .stream()
						   .collect(Collectors.toList());
	}
	public Collection<Url> getContactWebSiteUrls() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getContactWebSites())) return Collections.emptyList();
		return _contactInfo.getContactWebSites()
						   .stream()
						   .map(ContactWeb::getUrl)
						   .collect(Collectors.toList());
	}
}
