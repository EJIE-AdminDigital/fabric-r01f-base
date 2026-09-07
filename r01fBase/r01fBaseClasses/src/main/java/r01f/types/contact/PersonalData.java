package r01f.types.contact;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.FullTextSummarizable;
import r01f.facets.FullTextSummarizable.HasFullTextSummaryFacet;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Objects;
import r01f.util.types.Strings;

/**
 * Person contact data
 */
@MarshallType(as="personalData")
@Accessors(prefix="_")
@NoArgsConstructor
public class PersonalData
  implements Serializable,
  			 HasSummaryFacet,
  			 HasFullTextSummaryFacet {

	private static final long serialVersionUID = -4090115487051906413L;

/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * ID (ie spanish NIF, passport, etc)
	 */
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonID _personId;
	/**
	 * Person ID type
	 */
	@MarshallField(as="personIdType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonIDType _personIdType;
	/**
	 * Name
	 */
	@MarshallField(as="fullName",escape=true)
	@Getter @Setter protected PersonFullName _fullName;
	/**
	 * Mr, Ms, Doc, etc
	 */
	@MarshallField(as="salutation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonSalutation _salutation;
	/**
	 * Preferred language
	 */
	@MarshallField(as="preferredLang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Language _preferredLang;
	/**
	 * The person gender
	 */
	@MarshallField(as="gender",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonGender _gender;
	/**
	 * Birth date
	 */
	@MarshallField(as="birthDate",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Instant _birthDate;
	/**
	 * Details about the person
	 */
	@MarshallField(as="details",escape=true)
	@Getter @Setter protected String _details;
/////////////////////////////////////////////////////////////////////////////////////////
//  HasSummary
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return _fullName != null ? _fullName.asSummarizable()
								 : null;
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this.asSummarizable());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public String getName() {
		return _fullName != null ? _fullName.getName() : null;
	}
	public void setName(final String name) {
		_fullName = _fullName != null ? _fullName.withName(name)
									  : new PersonFullName(name,null,null);
	}
	public String getSurname1() {
		return _fullName != null ? _fullName.getSurname() : null;
	}
	public void setSurname1(final String surname1) {
		_fullName = _fullName != null ? _fullName.withSurname(surname1)
									  : new PersonFullName(null,surname1,null);
	}
	public String getLastName() {
		return this.getSurname1();
	}
	public String getFamilyName() {
		return this.getSurname1();
	}
	public String getSurname2() {
		return _fullName != null ? _fullName.getSurname2() : null;
	}
	public void setSurname2(final String surname2) {
		_fullName = _fullName != null ? _fullName.withSurname2(surname2)
									  : new PersonFullName(null,null,surname2);
	}
	public String getLastName2() {
		return this.getSurname2();
	}
	public String getFamilyName2() {
		return this.getSurname2();
	}
	/**
	 * Returns the surname composing the surname1 and surname2
	 * @return
	 */
	public String getSurname() {
		String outSurname = null;
		if (Strings.isNOTNullOrEmpty(this.getSurname1()) && Strings.isNOTNullOrEmpty(this.getSurname2())) {
			outSurname = Strings.customized("{} {}",this.getSurname1(),this.getSurname2());
		}
		else if (Strings.isNOTNullOrEmpty(this.getSurname1())) {
			outSurname = this.getSurname1();
		}
		else if (Strings.isNOTNullOrEmpty(this.getSurname2())) {
			outSurname = this.getSurname2();
		}
		return outSurname;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof PersonalData)) return false;

		PersonalData otherPerson = (PersonalData)obj;

		boolean nameEq = _fullName != null ? _fullName.equals(otherPerson.getFullName()) ? true : false
									   	   : true;	// both null
		boolean salutEq = _salutation != null ? _salutation.equals(otherPerson.getSalutation()) ? true : false
											   : true;	// both null
		boolean langEq = _preferredLang != null ? _preferredLang.equals(otherPerson.getPreferredLang()) ? true : false
											    : true;	// both null
		boolean genderEq = _gender != null ? _gender.equals(otherPerson.getGender()) ? true : false
										   : true;	// both null
		boolean detailsEq = _details != null ? _details.equals(otherPerson.getDetails()) ? true : false
										     : true;	// both null
		return  nameEq 
			 && salutEq && langEq
			 && genderEq
			 && detailsEq;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_fullName,
								_salutation,_preferredLang,
								_gender,
								_details);
	}
}
