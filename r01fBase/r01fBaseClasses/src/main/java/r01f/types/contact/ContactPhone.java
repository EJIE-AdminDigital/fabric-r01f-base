package r01f.types.contact;


import java.time.ZoneId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.HourOfDay;
import r01f.util.types.Objects;
import r01f.util.types.Strings;

/**
 * Contact person's phone
 * <pre class='brush:java'>
 *	ContactPhone phone = ContactPhone.createToBeUsedFor(ContactInfoUsage.PERSONAL)
 *									 .useAsDefault()
 *									 .withNumber("688671967")
 *									 .availableRangeForCalling(Ranges.closed(0,22));
 * </pre>
 */
@MarshallType(as="phoneChannel")
@Accessors(prefix="_")
@NoArgsConstructor
public class ContactPhone
	 extends ContactMeanDataBase<ContactPhone> {

	private static final long serialVersionUID = 6677974112128068298L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Phone type (mobile, non-mobile, fax, ...)
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ContactPhoneType _type = ContactPhoneType.MOBILE;
	/**
	 * Phone number
	 */
	@MarshallField(as="number",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Phone _number;
	/**
	 * Extension
	 */
	@MarshallField(as="extension",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private PhoneExtension _extension;
	/**
	 * Hour range when could be contacted
	 *
	 * It is stored like a String but internally is used like a Range<Integer>. That is to avoid GWT incompatibility.
	 * The lombok Getter and Setter are necessary because the explicit methods have the @GWTIncompatible annotation
	 * and wont be generated in the GWT compilation.
	 */
	@MarshallField(as="availability",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) private String _availableRangeForCallingStr = null;
	public r01f.types.Range<Integer> getAvailableRangeForCalling() {
		if (Strings.isNullOrEmpty(_availableRangeForCallingStr)) return null;
		return r01f.types.Range.parse(_availableRangeForCallingStr,Integer.class);
	}
	public void setAvailableRangeForCalling(final r01f.types.Range<Integer> availableRangeForCalling) {
		if (availableRangeForCalling == null) {
			_availableRangeForCallingStr = null;
		} else {
			_availableRangeForCallingStr = availableRangeForCalling.asString();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactPhone createToBeUsedFor(final ContactInfoUsage usage) {
		ContactPhone outPhone = new ContactPhone();
		outPhone.usedFor(usage);
		return outPhone;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactPhone type(final ContactPhoneType type) {
		_type = type;
		return this;
	}
	public ContactPhone withNumber(final Phone number) {
		_number = number;
		return this;
	}
	public ContactPhone withNumber(final String number) {
		_number = Phone.create(number);
		return this;
	}
	public ContactPhone withExtension(final PhoneExtension extension) {
		_extension = extension;
		return this;
	}
	public ContactPhone withExtension(final String extension) {
		_extension = PhoneExtension.create(extension);
		return this;
	}
	public ContactPhone availableRangeForCalling(final r01f.types.Range<Integer> range) {
		_availableRangeForCallingStr = range.asString();
		return this;
	}
	public ContactPhone allwaysAvailableForCalling() {
		_availableRangeForCallingStr = null;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateFrom(final ContactPhone other) {
		super.updateFrom(other);
		_type = other.getType();
		_number = other.getNumber();
		_availableRangeForCallingStr = other.getAvailableRangeForCallingStr();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isAvailableNow(final ZoneId zoneId) {
		return this.isAvailable(HourOfDay.nowAt(zoneId));
	}
	public boolean isAvailable(final HourOfDay hourOfDay) {
		r01f.types.Range<Integer> availableRange = this.getAvailableRangeForCalling();
		int hour = hourOfDay.asInteger();
		return availableRange.getLowerBound() <= hour && availableRange.getUpperBound() > hour;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactPhone other)) return false;
		return super.equals(other)
			&& Objects.areEqual(this.getType(),other.getType(),ContactPhoneType::equals)
			&& Objects.areEqual(this.getNumber(),other.getNumber(),Phone::equals)
			&& Objects.areEqual(this.getAvailableRangeForCallingStr(),other.getAvailableRangeForCallingStr(),String::equals);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private,
								_usage,
							    _usageDetails,
							    _default,
							    _type,
							    _number,
							    _availableRangeForCallingStr);
	}
}
