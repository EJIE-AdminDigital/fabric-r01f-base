package r01f.types.contact;


import java.util.function.Function;

import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

/**
 * Models a person identity card number (spanish dni or social security number)
 */
@MarshallType(as="personId")
@Immutable
public record PersonID(String id)
   implements IsPersonID {
	
	public PersonID(final String id,
					final boolean strict) {
		this(IsPersonID.normalize(id));	// normalize!!
		//if (strict && !this.isValid()) throw new IllegalArgumentException(Throwables.message("{} is NOT a valid NIF",this.getId()));
	}
	public static PersonID valueOf(final String s) {
		return new PersonID(s);
	}
	public static PersonID forId(final String id) {
		return new PersonID(id);
	}
	public static PersonID forId(final String id,
								 final boolean strict) {
		return new PersonID(id,strict);
	}
	public static PersonID fromSpanishNIF(final String id) {
		return PersonID.fromSpanishNIF(id,true);
	}
	public static PersonID fromSpanishNIF(final String id,
										  final boolean normalize) {
		return new PersonID(IsPersonID.normalize(id));	// normalize!!
	}
	public static Function<CharSequence,PersonID> FROM_STRING_FUNC = str -> PersonID.forId(str.toString());
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getId() {
		return this.id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean isValid() {
		return Strings.isNOTNullOrEmpty(this.getId());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,PersonID> SPANISH_NIF_FROM_STRING_TRANSFORM = id -> PersonID.forId(id);
	public static final Function<PersonID,String> SPAHINSH_NIF_TO_STRING_TRANSFORM = nif -> nif.asString();
}
