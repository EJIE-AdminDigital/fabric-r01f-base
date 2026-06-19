package r01f.types.contact;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.enums.EnumExtended;
import r01f.locale.I18NBundleAccess;
import r01f.locale.I18NKey;

/**
 * NIF = Número de Identificación Fiscal (Spanish Tax Identification Number)
 * It's an "umbrella" for several types of tax identification numbers:
 * - DNI (Documento Nacional de Identidad): for Spanish citizens
 * - NIE (Número de Identidad de Extranjero): for foreign residents in Spain
 * - CIF (Código de Identificación Fiscal): for legal entities (companies, associations, etc.)
 * - NAF (Número de Afiliación a la Seguridad Social): for social security purposes (not covered by this validator)
 * 
 * (the CIF "concept" is being replaced by the "concept" of [NIF for legal entities], but it's still widely used)
 * 
 * Format:
 * 	- DNI: 8 digits + 1 letter (e.g. 12345678Z)
 *  - NIE: 1 letter (X, Y, Z) + 7 digits + 1 letter (e.g. X1234567L)
 *  - Special NIF: 1 letter (K, L, M) + 7 digits + 1 letter (e.g. K1234567T)
 *  		- K: Españoles menores de 14 años que NO tienen DNI pero que necesita un número para tributar porque por ejemplo ha recibido una herencia o tiene un patrimonio que tributa.
 *  		- L: Españoles residentes en el extranjero y NO estan inscritos en el padrón por lo que no es fácil conseguir un DNI
 *  		- M: Extranjeros sin NIE pero que realizan una operación económica 
 *  - CIF: 1 letter (A, B, C, D, E, F, G, H, J, U, V) + 7 digits + 1 control character (letter or digit) (e.g. A12345678)
 *  		- Sociedades mercantiles
 * 				- A: Sociedades Anónimas.
 * 				- B: Sociedades de Responsabilidad Limitada.
 * 				- C: Sociedades colectivas.
 * 				- D: Sociedades comanditarias.
 *			- Entidades de Economía Social y Religiosa
 *				- F: Sociedades cooperativas y sociedades laborales.
 *				- G: Asociaciones.
 *				- R: Instituciones Religiosas.
 *			- Organismos Públicos
 *				- P: Ayuntamientos y organismos públicos.
 *				- Q: Organismos públicos (universidades, ministerios, entes estatales...)
 *				- S: Órganos de la Administración del Estado y Comunidades Autónomas.
 *			- Entidades Sin Personalidad Jurídica
 *				- E: Comunidades de bienes y herencias yacentes.
 *				- H: Comunidades de propietarios en régimen de propiedad horizontal.
 *				- U: Uniones Temporales de Empresas.
 *				- V: Otros tipos de entidades (no incluidas en los grupos anteriores).
 *			- Entidades Extranjeras y Casos Especiales
 *				- N: Entidades extranjeras.
 *				- W: Establecimientos permanentes de entidades no residentes en España.
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum NIFType 
 implements EnumExtended<NIFType> {
	DNI(I18NKey.named("personId.type.spanish-nif.dni")),
	NIE(I18NKey.named("personId.type.spanish-nif.nie")),
	SPECIAL_NIF(I18NKey.named("personId.type.spanish-nif.special-nif")),
	LEGAL_ENTITY_NIF(I18NKey.named("personId.type.spanish-nif.legal-entity-nif"));	// also known as CIF (Código de Identificación Fiscal)
	
	private final I18NKey _i18nKey;
	
	public String nameUsing(final I18NBundleAccess i18n) {
		return i18n.getMessage(_i18nKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public static NIFType fromString(final String str) {
		return NIFType.from(str);
	}
	public static NIFType from(final IsPersonID personId) {
		if (personId == null) throw new IllegalArgumentException("NOT Valid NIF: " + personId);
		return NIFType.from(personId.asString());
		
	}
	public static NIFType from(final String personIdStr) {
		if (!NIFValidator.isValidNIF(personIdStr)) throw new IllegalArgumentException("NOT Valid NIF: " + personIdStr);
        
        // [0] - clean up and normalize
        String id = personIdStr.trim()
        					   .toUpperCase();
        // [1] - Check DNI: 8 numbers + 1 letter
        if (id.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) return DNI;

        // [2] - NIE: Letter (X, Y, Z) + 7 números + 1 letra
        if (id.matches("^[XYZ][0-9]{7}[TRWAGMYFPDXBNJZSQVHLCKE]$")) return NIE;

        // [3] - Legal entity NIF (also known as CIF): Letter + 8 characters (7 numbers + control)
        // 		 supported letters: A, B, C, D, E, F, G, H, J, N, P, Q, R, S, U, V, W
        if (id.matches("^[ABCDEFGHJNPRSUUVW][0-9]{7}[0-9A-J]$")) return LEGAL_ENTITY_NIF;

        // [4] - Special NIF: letter (K, L, M) + 7 numbers + 1 letter
        if (id.matches("^[KLM][0-9]{7}[TRWAGMYFPDXBNJZSQVHLCKE]$")) return SPECIAL_NIF;

        // [99] - Unknown NIF
        throw new IllegalArgumentException("Unknown NIF type: " + id);
	}
}
