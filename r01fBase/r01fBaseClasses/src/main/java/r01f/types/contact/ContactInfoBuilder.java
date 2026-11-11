package r01f.types.contact;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;
import r01f.util.types.collections.CollectionUtils;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class ContactInfoBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final ContactInfo _contactInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactInfoBuilderVisibilityStep create() {
		return ContactInfoBuilder.createWithId(null);	// no id
	}
	public static ContactInfoBuilderVisibilityStep createWithId(final ContactInfoID id) {
		return new ContactInfoBuilder(new ContactInfo(id))
						.new ContactInfoBuilderVisibilityStep();
	}
	public static ContactInfoBuilderPhonesStep createHidden() {
		return ContactInfoBuilder.create().hidden();
	}
	public static ContactInfoBuilderPhonesStep createHiddenWithId(final ContactInfoID id) {
		return ContactInfoBuilder.createWithId(id).hidden();
	}
	public static ContactInfoBuilderPhonesStep createVisible() {
		return ContactInfoBuilder.create().visible();
	}
	public static ContactInfoBuilderPhonesStep createVisibleWithId(final ContactInfoID id) {
		return ContactInfoBuilder.createWithId(id).visible();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderVisibilityStep {
		public ContactInfoBuilderPhonesStep visible() {
			_contactInfo.setPrivate(false);
			return new ContactInfoBuilderPhonesStep();
		}
		public ContactInfoBuilderPhonesStep hidden() {
			_contactInfo.setPrivate(true);
			return new ContactInfoBuilderPhonesStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderPhonesStep {
		public ContactInfoBuilderMailsStep addPhone(final ContactPhone... phones) {
			List<ContactPhone> contactPhones = CollectionUtils.hasData(phones) ? Arrays.asList(phones)
																					   .stream()
																					   .filter(Objects::nonNull)
																					   .toList()
																			   : null;
			if (CollectionUtils.isNullOrEmpty(contactPhones)) return new ContactInfoBuilderMailsStep();
			for (ContactPhone phone : contactPhones) _contactInfo.addPhone(phone);
			return new ContactInfoBuilderMailsStep();
		}
		public ContactInfoBuilderMailsStep noPhones() {
			return new ContactInfoBuilderMailsStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderMailsStep {
		public ContactInfoBuilderWebsStep addMail(final ContactEMail... mails) {
			List<ContactEMail> contactMails = CollectionUtils.hasData(mails) ? Arrays.asList(mails)
																					.stream()
																					.filter(Objects::nonNull)
																					.toList()
																			: null;
			if (CollectionUtils.isNullOrEmpty(contactMails)) return new ContactInfoBuilderWebsStep();
			for (ContactEMail mail : contactMails) _contactInfo.addOrUpdateContactEMail(mail);
			return new ContactInfoBuilderWebsStep();
		}
		public ContactInfoBuilderWebsStep noMail() {
			return new ContactInfoBuilderWebsStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderWebsStep {
		public ContactInfoBuilderSocialNetworksStep addWeb(final ContactWeb... webs) {
			List<ContactWeb> contactWebs = CollectionUtils.hasData(webs) ? Arrays.asList(webs)
																				 .stream()
																				 .filter(Objects::nonNull)
																				 .toList()
																		 : null;
			if (CollectionUtils.isNullOrEmpty(contactWebs)) return new ContactInfoBuilderSocialNetworksStep();
			for (ContactWeb web : contactWebs) _contactInfo.addWebSite(web);
			return new ContactInfoBuilderSocialNetworksStep();
		}
		public ContactInfoBuilderSocialNetworksStep noWeb() {
			return new ContactInfoBuilderSocialNetworksStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderSocialNetworksStep {
		public ContactInfoBuilderPreferedLanguageStep addSocialNetwork(final ContactSocialNetwork... socNets) {
			List<ContactSocialNetwork> contactNets = CollectionUtils.hasData(socNets) ? Arrays.asList(socNets)
																			 	    		  .stream()
																			 	    		  .filter(Objects::nonNull)
																			 	    		  .toList()
																			 	      : null;
			if (CollectionUtils.isNullOrEmpty(contactNets)) return new ContactInfoBuilderPreferedLanguageStep();
			for (ContactSocialNetwork socNet : contactNets) _contactInfo.addSocialNetwork(socNet);
			return new ContactInfoBuilderPreferedLanguageStep();
		}
		public ContactInfoBuilderBuildStep noSocialNetwork() {
			return new ContactInfoBuilderBuildStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderPreferedLanguageStep {
		public ContactInfoBuilderBuildStep contactIn(final Language lang) {
			_contactInfo.setPreferedLanguage(lang);
			return new ContactInfoBuilderBuildStep();
		}
		public ContactInfoBuilderBuildStep noPreferedLanguage() {
			return new ContactInfoBuilderBuildStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderBuildStep {
		public ContactInfo build() {
			return _contactInfo;
		}
	}
}
