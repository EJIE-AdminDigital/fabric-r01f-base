package r01f.model.metadata;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.PolymorphicFieldTypeResolve;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class TypeFieldMetaData 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final TypeMetaData<? extends MetaDataDescribable> _containerType;
	@Getter private final FieldID _id;
	@Getter private final MetaDataForField _fieldMetaData;
	@Getter private final Type _fieldType;
	@Getter private final TypeMetaData<? extends MetaDataDescribable> _fieldTypeMetaData; 		// if the type of this field is another type with metadata
	
	public Class<?> getRawFieldType() {
		return TypeToken.of(_fieldType)
						.getRawType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  as FieldMetaData conversion
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient Memoized<? extends FieldMetaData> _asFieldMetaData = 
					Memoized.using(() -> {
										HasTypesMetaData typeMetaDataInspector = TypeMetaDataInspector.singleton();																						
										FieldMetaData outFieldMetaData = this.asFieldMetaData(this)
																	   		 .using(typeMetaDataInspector);
										return outFieldMetaData;
								   });
	/**
	 * Returns this field as a {@link FieldMetaData} object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <F extends FieldMetaData> F asFieldMetaData() {
		return (F)_asFieldMetaData.get();
	}
	public <F extends FieldMetaData> FieldMetaDataBuilderAsFieldUsingHasTypeMetaDataStep<F> asFieldMetaData(final TypeFieldMetaData typeFieldMetaData) {
		return new FieldMetaDataBuilderAsFieldUsingHasTypeMetaDataStep<>(typeFieldMetaData);
	}
	@SuppressWarnings("unchecked")
	@RequiredArgsConstructor(access=AccessLevel.MODULE)
	final class FieldMetaDataBuilderAsFieldUsingHasTypeMetaDataStep<F extends FieldMetaData> {
		private final TypeFieldMetaData _typeFieldMetaData;

		public F using(final HasTypesMetaData metaDataInspector) {
			// [1] create the field metadata
			FieldMetaData outFieldMetaData = null;
			if (CollectionUtils.hasData(_typeFieldMetaData.getFieldMetaData().polymorphicResolution())) {
				// it's a polymorphic type
				FieldMetaDataForPolymorphicType md = new FieldMetaDataForPolymorphicType(_typeFieldMetaData.getId(),
																						 _laguageTextsFor(_typeFieldMetaData.getFieldMetaData().alias()),
																						 _laguageTextsFor(_typeFieldMetaData.getFieldMetaData().description()),
																						 _typeFieldMetaData.getRawFieldType());
				Map<Class<? extends MetaDataDescribable>,Class<?>> resolutionMap = Maps.newHashMapWithExpectedSize(_typeFieldMetaData.getFieldMetaData().polymorphicResolution().length);
				for (PolymorphicFieldTypeResolve res : _typeFieldMetaData.getFieldMetaData().polymorphicResolution()) {
					resolutionMap.put(res.whenContainerType(),res.resolveTo());
				}
				md.setFieldDataTypeMap(resolutionMap);
				outFieldMetaData = md;
			}
			else {
				// it's NOT a polymorphic type (the usual case)
				outFieldMetaData = FieldMetaDataBuilder.forId(_typeFieldMetaData.getId())
										   .withName(_laguageTextsFor(_typeFieldMetaData.getFieldMetaData().alias()))
										   .withDescription(_laguageTextsFor(_typeFieldMetaData.getFieldMetaData().description()))
										   .using(metaDataInspector)
										   .of(_typeFieldMetaData.getContainerType().getType(),
											   _typeFieldMetaData.getFieldType());
			}
			// [2] set the search engine config
			outFieldMetaData.setSearchEngineIndexingConfig(FieldMetaDataSearchEngineIndexingConfig.forFieldTypeWithConfig(_typeFieldMetaData.getRawFieldType(),
																														  _typeFieldMetaData.getFieldMetaData().storage()));
			return (F)outFieldMetaData;
		}
	}
	private static LanguageTexts _laguageTextsFor(final DescInLang[] descs) {
		if (CollectionUtils.isNullOrEmpty(descs)) return null;
		LanguageTexts outLangTexts = new LanguageTextsMapBacked();
		for (DescInLang desc : descs) {
			outLangTexts.add(desc.language(),desc.value());
		}
		return outLangTexts;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the field is indexed
	 */ 
	public boolean isIndexed() {
		return _fieldMetaData != null
			&& _fieldMetaData.storage() != null
			&& _fieldMetaData.storage().indexed();
	}
	public FieldID getIndexableFieldId() {
		if (!this.isIndexed()) throw new UnsupportedOperationException(String.format("Field with id=%s of type %s is NOT indexable!",
																					 _id,_containerType.getType()));
		return _id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tField").append(" id=").append(_id)
							.append(" type=").append(_fieldType)
							.append(_fieldTypeMetaData != null ? " (has metadata)" : "")
							.append("\n");
		
		if (_fieldMetaData != null) {
			sb.append("\t\tMetaData").append("\n");
			if (CollectionUtils.hasData(_fieldMetaData.alias())) {
				sb.append("\t\tAlias:\n");
				for (DescInLang desc : _fieldMetaData.alias()) {
					sb.append("\t\t\t").append(desc.language()).append("=").append(desc.value()).append("\n");
				}
			}
			if (CollectionUtils.hasData(_fieldMetaData.description())) {
				sb.append("\t\tDescription:\n");
				for (DescInLang desc : _fieldMetaData.description()) {
					sb.append("\t\t\t").append(desc.language()).append("=").append(desc.value()).append("\n");
				}
			}
			if (_fieldMetaData.storage() != null) {
				sb.append("\t\tStorage:\n");
				sb.append("\t\t\t-  Indexed: ").append(_fieldMetaData.storage().indexed()).append("\n");
				sb.append("\t\t\t-   Stored: ").append(_fieldMetaData.storage().stored()).append("\n");
				sb.append("\t\t\t-Tokenized: ").append(_fieldMetaData.storage().tokenized()).append("\n");
				sb.append("\t\t\t- Boosting: ").append(_fieldMetaData.storage().boosting()).append("\n");
			}
		}
		return sb;
	}
}
