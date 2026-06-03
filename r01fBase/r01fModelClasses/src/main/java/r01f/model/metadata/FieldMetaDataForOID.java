package r01f.model.metadata;

import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForOIDField")
public class FieldMetaDataForOID
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 8974728672923211198L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForOID() {
		super();
	}
	@SuppressWarnings("unchecked")
	public FieldMetaDataForOID(final FieldMetaDataForOID other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),			 
			 (Class<? extends OID>)other.getDataType());
	}
	public FieldMetaDataForOID(final FieldID fieldId,
							   final LanguageTexts name,final LanguageTexts description,
							   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
							   final Class<? extends OID> oidType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  oidType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof OID 
				 		  && (value.getClass() == _dataType		// same type
				 		      || 														    // or
				 		     _dataType.isAssignableFrom(value.getClass()));	// a subtype
				 		      
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
