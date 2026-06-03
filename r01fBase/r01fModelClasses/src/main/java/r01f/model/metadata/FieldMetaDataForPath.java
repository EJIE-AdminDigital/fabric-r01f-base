package r01f.model.metadata;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.IsPath;

@MarshallType(as="metaDataConfigForPathField")
public class FieldMetaDataForPath
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 929667993308526216L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForPath() {
		super();
	}
	@SuppressWarnings("unchecked")
	public FieldMetaDataForPath(final FieldMetaDataForPath other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 (Class<? extends IsPath>)other.getDataType());
	}
	public FieldMetaDataForPath(final FieldID fieldId,
							    final LanguageTexts name,final LanguageTexts description,
							    final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
								final Class<? extends IsPath> pathType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  pathType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof IsPath 
						  && value.getClass().equals(_dataType);
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
