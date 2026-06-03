package r01f.types.contact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Objects;

/**
 * Person Data with ID.
 */
@MarshallType(as="person")
@Accessors(prefix="_")
@NoArgsConstructor
public class Person<PERSONID extends IsPersonID>
	 extends PersonalData  {

	private static final long serialVersionUID = 3678962348416518107L;

/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Person id (ie dni number)
	 */
	@MarshallField(as="id")
	@Getter @Setter private PERSONID  _id;
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Person)) return false;
		boolean personalData = super.equals(obj);
		Person<?> otherPerson = (Person<?>)obj;
		boolean idEq = _id != null ? _id.equals(otherPerson.getId()) ? true : false
				                   :  false;

		return idEq
			&& personalData;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_id,
								_name,_surname1,_surname2,
								_salutation,_preferredLang,
								_gender,
								_details);
	}
}
