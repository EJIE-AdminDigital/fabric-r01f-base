package r01f.types.contact;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Objects;


/**
 * Email Data
 * <pre class='brush:java'>
 *	ContactMail user = ContactMail.createToBeUsedFor(ContactInfoUsage.PERSONAL)
 *								  .mailTo(EMail.of("futuretelematics@gmail.com"));
 * </pre>
 */
@MarshallType(as="emailChannel")
@Accessors(prefix="_")
@NoArgsConstructor
public class ContactEMail 
     extends ContactMeanDataBase<ContactEMail> {
	
	private static final long serialVersionUID = 586551188692425105L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Email
	 */
	@MarshallField(as="addr",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private EMail _mail;
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactEMail createToBeUsedFor(final ContactInfoUsage usage) {
		ContactEMail outMail = new ContactEMail();
		outMail.usedFor(usage);
		return outMail;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactEMail mailTo(final EMail mail) {
		_mail = mail;
		return this;
	}
	public ContactEMail mailTo(final String mail) {
		_mail = EMail.create(mail);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateFrom(final ContactEMail other) {
		super.updateFrom(other);
		_mail = other.getMail();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactEMail)) return false;
		ContactEMail other = (ContactEMail)obj;
		return super.equals(other)
			&& Objects.areEqual(this.getMail(),other.getMail(),EMail::equals)
			&& Objects.areEqual(this.getUsageDetails(),other.getUsageDetails(),String::equals)
			&& Objects.areEqual(this.isDefault(),other.isDefault(),Boolean::equals);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private,
								_usage,
							    _usageDetails,
							    _default,
							    _mail);
	}
}
