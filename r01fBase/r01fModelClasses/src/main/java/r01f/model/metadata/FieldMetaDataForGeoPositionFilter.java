package r01f.model.metadata;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoPositionFilter;

@MarshallType(as="metaDataConfigForGeoPositionFilterField")
public class FieldMetaDataForGeoPositionFilter
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 929667993308526216L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForGeoPositionFilter() {
		super();
	}
	public FieldMetaDataForGeoPositionFilter(final FieldMetaDataForGeoPositionFilter other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());
	}
	public FieldMetaDataForGeoPositionFilter(final FieldID fieldId,
							    		     final LanguageTexts name,final LanguageTexts description,
							    		     final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  GeoPositionFilter.class);		 	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof GeoPositionFilter;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
