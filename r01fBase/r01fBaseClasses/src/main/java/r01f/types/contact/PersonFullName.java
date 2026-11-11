package r01f.types.contact;

import java.util.stream.Stream;

import lombok.Getter;
import r01f.crypto.Hash;
import r01f.facets.FullTextSummarizable;
import r01f.facets.FullTextSummarizable.HasFullTextSummaryFacet;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryStringBacked;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.SelfValidates;

@MarshallType(as="personlFullName")
public record PersonFullName(// name
							 @MarshallField(as="name",escape=true)
							 @Getter String name,
							 // surname
							 @MarshallField(as="surname",escape=true)
							 @Getter String surname,
							 // second surname
							 @MarshallField(as="surname2",escape=true)
							 @Getter String surname2) 
    implements CanBeRepresentedAsString,
    		   HasSummaryFacet,
  			   HasFullTextSummaryFacet,
			   SelfValidates<PersonFullName> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersonFullName {
		if (Strings.isNullOrEmpty(name)) throw new IllegalArgumentException("The name is mandatory");
	}
	public PersonFullName(final String name) {
		this(name,
			 null,	// surname
			 null); // surname2
	}
	public PersonFullName(final String name,final String surname) {
		this(name,
			 surname,
			 null); // surname2
	}
	public static PersonFullName of(final String name,final String surname,final String surname2) {
		return new PersonFullName(name,surname,surname2);
	}
	public static PersonFullName of(final String name,final String surname) {
		return new PersonFullName(name,surname);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET & SET
/////////////////////////////////////////////////////////////////////////////////////////
	public String getSurname1() {
		return this.surname;
	}
	public String getFamilyName() {
		return this.surname;
	}
	public String getLastName() {
		return this.surname;
	}
	public String getLastName2() {
		return this.surname2;
	}
	public String getFamilyName2() {
		return this.surname2;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasName() {
		return Strings.isNOTNullOrEmpty(this.name);
	}
	public boolean hasSurname() {
		return Strings.isNOTNullOrEmpty(this.surname);
	}
	public boolean hasSurname2() {
		return Strings.isNOTNullOrEmpty(this.surname2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<PersonFullName> validate() {
		return Strings.isNOTNullOrEmpty(this.name) 
			&& Strings.isNOTNullOrEmpty(this.surname) 
				? ObjectValidationResultBuilder.on(this)
											   .isValid()
				: ObjectValidationResultBuilder.on(this)
											   .isNotValidBecause("The name and surname are mandatory for a {}",
																  PersonFullName.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAN BE REPRESENTED AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		String outSummary = null;

		String name = this.getName();
		String surname = this.getSurname();

		if (Strings.isNOTNullOrEmpty(surname) 
		 && Strings.isNOTNullOrEmpty(this.getName())) {
			outSummary = Strings.customized("{}, {}",
											surname,name);
		}
		else if (Strings.isNOTNullOrEmpty(surname)) {
			outSummary = surname;
		}
		else if (Strings.isNOTNullOrEmpty(name)) {
			outSummary = name;
		}
		else {
			outSummary = Strings.customized("--NO summary for {}--",PersonFullName.class);
		}
		return outSummary;
	}
	/**
	 * Returns the display name with the name first, then the surnames.
	 * @return
	 */
	public String getDisplayName() {
		return this.asString();
	}
	/**
	 * Returns the display name with the surnames first, then the name. 
	 * If there are surnames, they are separated from the name by a comma.
	 * @return
	 */
	public String getDisplayNameWithSurnamesFirst() {
		String surnames = Strings.customized("{}{}",
								  			 this.surname != null ? this.surname + " " : "",
								  			 this.surname2 != null ? this.surname2 + " " : "")
					  			 .trim();
		return Strings.customized("{}{}",
								  Strings.isNOTNullOrEmpty(surnames) ? surnames + ", " : "",
								  this.name != null ? this.name : "")
					  .trim();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HasSummary
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return SummarizableBuilder.summarizableFrom(this.getSummary());
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this.asSummarizable());
	}
	public Summary getSummary() {
		return SummaryStringBacked.of(this.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HASH
/////////////////////////////////////////////////////////////////////////////////////////
	public Hash getHash() {
		String joined = _join(this.getName(),
							  this.getSurname(),this.getSurname2());		
		return Hash.createSHA256From(joined);
	}
	public static Hash hashOfFullNameComponent(final String str) {
		if (Strings.isNullOrEmpty(str)) return null;
		String normalized = PersonFullName.normalizeFullNameComponent(str);
		return Hash.createSHA256From(normalized.toString());
	}
	public static String normalizeFullNameComponent(final String str) {
		if (Strings.isNullOrEmpty(str)) return null;
		String normalized1 = str.strip()
								.toLowerCase();
		CharSequence normalized2 = StringEncodeUtils.replaceAccentedCharsByNonAccentedEquivalents(normalized1);
		return normalized2.toString();
	}
	private static String _join(final String... strs) {
		if (CollectionUtils.isNullOrEmpty(strs)) return null;
		return Stream.of(strs)
					 // remove null or empty strings
					 .filter(Strings::isNOTNullOrEmpty)
					 // normalize
					 .map(PersonFullName::normalizeFullNameComponent)
					 // join
					 .reduce((s1,s2) -> String.format("%s$$%s",s1,s2))
					 .orElse(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////	
	public PersonFullName withName(final String name) {
		return new PersonFullName(name,
								  this.surname,
								  this.surname2);
	}
	public PersonFullName withSurname(final String surname) {
		return new PersonFullName(this.name,
								  surname,
								  this.surname2);
	}
	public PersonFullName withLastName(final String lastName) {
		return new PersonFullName(this.name,
								  lastName,
								  this.surname2);
	}
	public PersonFullName withFamilyName(final String familyName) {
		return new PersonFullName(this.name,
								  familyName,
								  this.surname2);
	}
	public PersonFullName withSurname2(final String surname2) {
		return new PersonFullName(this.name,
								  this.surname,
								  surname2);
	}
	public PersonFullName withFamilyName2(final String familyName2) {
		return new PersonFullName(this.name,
								  this.surname,
								  familyName2);
	}
	public PersonFullName withLastName2(final String lastName2) {
		return new PersonFullName(this.name,
								  this.surname,
								  lastName2);
	}
}	