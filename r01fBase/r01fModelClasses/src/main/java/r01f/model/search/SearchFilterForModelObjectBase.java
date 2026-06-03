package r01f.model.search;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.HasMetaDataForHasFullTextSummaryModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.HasMetaDataForHasTrackableFacetForModelObject;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.types.Range;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

@Slf4j
@Accessors(prefix="_")
public abstract class SearchFilterForModelObjectBase<SELF_TYPE extends SearchFilterForModelObjectBase<SELF_TYPE>>
    	   implements SearchFilterForModelObject {

	private static final long serialVersionUID = -6979312697491380544L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The language in which the user is doing the filter
	 */
	@MarshallField(as="uiLanguage",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _UILanguage;
	/**
	 * The query that wraps all the clauses
	 */
	@MarshallField(as="booleanQuery")
	@Getter @Setter private BooleanQueryClause _booleanQuery;

/////////////////////////////////////////////////////////////////////////////////////////
//  NOT SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private transient Collection<Class<? extends ModelObject>> _modelObjectTypes;

/////////////////////////////////////////////////////////////////////////////////////////
//  WRAPPERS & UTILs
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	@Getter protected transient final SearchFilterForModelObjectAccessorWrapper<SELF_TYPE> _accessorWrapper;

	@MarshallIgnoredField
	@Getter protected transient final SearchFilterForModelObjectModifierWrapper<SELF_TYPE> _modifierWrapper;
	
	@MarshallIgnoredField
	@Getter protected transient final SearchFilterQueryClausesFactory<SELF_TYPE> _queryClausesFactory;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SearchFilterForModelObjectBase() {
		_accessorWrapper = new SearchFilterForModelObjectAccessorWrapper<>((SELF_TYPE)this);
		_modifierWrapper = new SearchFilterForModelObjectModifierWrapper<>((SELF_TYPE)this);
		
		_queryClausesFactory = new SearchFilterQueryClausesFactory<>(_accessorWrapper,_modifierWrapper);
	}
	public <M extends ModelObject> SearchFilterForModelObjectBase(final Class<? extends M> modelObjectType) {
		this();
		if (modelObjectType != null) {
			Collection<Class<? extends ModelObject>> modelObjectTypes = new HashSet<>(1);
			modelObjectTypes.add(modelObjectType);
			this.setModelObjectTypesToBeFiltered(modelObjectTypes);
		}
	}
	@SuppressWarnings("unchecked")
	public SearchFilterForModelObjectBase(final Class<? extends ModelObject>... modelObjectTypes) {
		this();
		if (CollectionUtils.hasData(modelObjectTypes)) this.setModelObjectTypesToBeFiltered(Arrays.asList(modelObjectTypes));
	}
	public SearchFilterForModelObjectBase(final Collection<Class<? extends ModelObject>> modelObjectTypes) {
		this();
		if (CollectionUtils.hasData(modelObjectTypes)) this.setModelObjectTypesToBeFiltered(modelObjectTypes);
	}
	protected <F extends SearchFilterForModelObjectBase<F>> void _copy(final F other) {
		_UILanguage = other.getUILanguage();
		_booleanQuery = other.getBooleanQuery();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SET MODEL OBJECT TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the model object's type
	 * @param modelObjectType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE filterBy(final Class<? extends ModelObject>... modelObjTypes) {
		Collection<Class<? extends ModelObject>> modelObjTypesCol = CollectionUtils.of(modelObjTypes)
																				   		 .asSet();
		return this.filterBy(modelObjTypesCol);
	}
	/**
	 * Sets the model object's type
	 * @param modelObjectType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE filterBy(final Collection<Class<? extends ModelObject>> modelObjTypesCol) {
		Preconditions.checkArgument(CollectionUtils.hasData(modelObjTypesCol),"The object types cannot be null or empty");

		this.setModelObjectTypesToFilterBy(modelObjTypesCol);
		return (SELF_TYPE)this;
	}
	/**
	 * Returns whether it's filtering by the given model object
	 * @param modelObjType
	 * @return
	 */
	public boolean isFilteringBy(final Class<? extends ModelObject> modelObjType) {
		return CollectionUtils.hasData(this.getFilteredModelObjectTypes())
			&& this.getFilteredModelObjectTypes().contains(modelObjType);
	}
	/**
	 * Returns the number of [model object] types being filtered
	 * @return
	 */
	public int getFilteringByObjTypesCount() {
		return CollectionUtils.hasData(this.getFilteredModelObjectTypes()) ? this.getFilteredModelObjectTypes().size() : 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NO QUERY CLAUSES
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isEmpty() {
		return _booleanQuery == null
			|| CollectionUtils.isNullOrEmpty(_booleanQuery.getClauses());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UI LANGUAGE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the language the user is searching in... this could NOT be the same
	 * as the language the user is filtering objects
	 * @param lang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE theUserIsSearchingIn(final Language lang) {
		_UILanguage = lang;
		return (SELF_TYPE)this;
	}
	/**
	 * Returns the UI language or the provided default one if the stored UI language is null
	 * @param def
	 * @return
	 */
	public Language getUILanguageOrDefault(final Language def) {
		return _UILanguage != null ? _UILanguage : def;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FILTERED OBJECT TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<Class<? extends ModelObject>> getFilteredModelObjectTypes() {
		return _modelObjectTypes;
	}
	@Override
	public Collection<Long> getFilteredModelObjectTypesCodesUsing(final HasTypesMetaData hasTypesMetaData) {	
		return this.getFilteredModelObjectTypes()
				   .stream()
				   .filter(modelObjType -> hasTypesMetaData.existsTypeMetaDataFor(modelObjType))
				   .map(modelObjType -> hasTypesMetaData.getTypeMetaDataFor(modelObjType)
														.getTypeMetaData()
													    .modelObjTypeCode())
				   .filter(Objects::nonNull)
				   .collect(Collectors.toSet());
	}
	/**
	 * Sets the model object types to filter by
	 * @param modelObjTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE setModelObjectTypesToFilterBy(final Collection<Class<? extends ModelObject>> modelObjTypes) {
		this.setModelObjectTypesToBeFiltered(modelObjTypes);
		return (SELF_TYPE)this;
	}
	/**
	 * Sets the model object types to filter by
	 * @param modelObjTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE setModelObjectTypesToFilterBy(final Class<? extends ModelObject>... modelObjTypes) {
		Collection<Class<? extends ModelObject>> colTypes = Sets.newLinkedHashSet(Lists.newArrayList(modelObjTypes));
		this.setModelObjectTypesToBeFiltered(colTypes);
		return (SELF_TYPE)this;
	}
	@Override
	public void setModelObjectTypesToBeFiltered(final Collection<Class<? extends ModelObject>> modelObjectTypes) {
		Preconditions.checkArgument(CollectionUtils.hasData(modelObjectTypes),"The model object types to be filtered MUST not be null or empty");
		_modelObjectTypes = modelObjectTypes;

		// All the type's facets are stored at the TYPE_FACETS_FIELD_ID as a multi-valued field
		Collection<Long> typesCodes = this.getFilteredModelObjectTypesCodesUsing(TypeMetaDataInspector.singleton());
		FieldID fieldId = FieldID.from(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_FACETS);
		
		_queryClausesFactory.addMustBeContainedInQueryClause(fieldId,
													   		 typesCodes,Long.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O getOid() {
		// obviously, only a single model object type can be set to be filtered if also an oid is set
		FieldID fieldId = FieldID.from(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);

		// Get the equals query clause if it exists
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		EqualsQueryClause<O> oidClause = clause != null ? (EqualsQueryClause<O>)clause : null;
		return oidClause != null ? (O)oidClause.getEqValue() : null;
	}
	@Override
	public <O extends OID> void setOid(final O oid) {
		// obviously, only a single model object type can be set to be filtered if also an oid is set
		FieldID fieldId = FieldID.from(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
		
		_queryClausesFactory.addMustEqualsQueryClause(fieldId,
													  oid);
	}
	/**
	 * Fluent-API to set the oid
	 * @param oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <O extends OID> SELF_TYPE withOid(final O oid) {
		this.setOid(oid);
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasTextFilter() {
		return this.getTextFilter() != null;
	}
	@Override
	public ContainsTextQueryClause getTextFilter() {
		return _getTextFilter(false);	// not strict
	}
	private ContainsTextQueryClause _getTextFilter(final boolean strict) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasFullTextSummaryModelObject.SEARCHABLE_METADATA.FULL_TEXT);
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		ContainsTextQueryClause textClause = clause != null ? clause.as(ContainsTextQueryClause.class)
															: null;
		if (strict) throw new IllegalStateException("NO text filter for field " + fieldId + " at query=" + this.toCriteriaString());
		return textClause;
	}
	public String getText() {
		ContainsTextQueryClause textClause = _getTextFilter(false);	// not strict
		return textClause != null ? textClause.getText() : null;
	}
	public Language getTextLanguage() {
		ContainsTextQueryClause textClause = _getTextFilter(false);	// not strict
		return textClause != null ? textClause.getLang() : null;
	}
	public void setText(final String text) {
		this.setText(text,
					 null);		// language independent
	}
	public void setText(final String text,
						final Language lang) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasFullTextSummaryModelObject.SEARCHABLE_METADATA.FULL_TEXT);
		
		_queryClausesFactory.addMustTextQueryClause(fieldId,
													text,lang);
	}
	/**
	 * Fluent-API to set the full text search
	 * @param oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchFilterTextLangStep<SELF_TYPE> withText(final String text) {
		return new SearchFilterTextLangStep<>((SELF_TYPE)this,
											  text);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class SearchFilterTextLangStep<FILTER_TYPE extends SearchFilterForModelObjectBase<FILTER_TYPE>> {
		private final FILTER_TYPE _filter;
		private final String _text;

		public FILTER_TYPE in(final Language lang) {
			_filter.setText(_text,lang);
			return _filter;
		}
		public FILTER_TYPE languageIndependent() {
			_filter.setText(_text);
			return _filter;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATOR & EDITOR
/////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
	public SELF_TYPE createdBy(final LoginID creator) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		
		_queryClausesFactory.addMustEqualsQueryClause(fieldId,
													  creator);	
		return (SELF_TYPE)this;
    }
	public LoginID getCreator() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
    @SuppressWarnings("unchecked")
	public SELF_TYPE lastEditedBy(final LoginID lastEditor) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);

		_queryClausesFactory.addMustEqualsQueryClause(fieldId,
													  lastEditor);	
		return (SELF_TYPE)this;
    }
	public LoginID getLastEditor() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE createdOrEditedBy(final LoginID lastCreatorOrUpdator) {
		Preconditions.checkArgument(lastCreatorOrUpdator != null,"The creator or updator cannot be null");

		// creator = userCode OR lastUpdator = userCode
		FieldID creatorFieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		FieldID updatorFieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);
		BooleanQueryClause booleanClause = BooleanQueryClause.create("createdOrEditedBy")
														.field(creatorFieldId).should().beEqualTo(lastCreatorOrUpdator)
														.field(updatorFieldId).should().beEqualTo(lastCreatorOrUpdator)
														.build();
		_modifierWrapper.addClause(booleanClause,QueryClauseOccur.MUST);
		return (SELF_TYPE)this;
	}
	public LoginID getCreatedOrEditedByUser() {
		QualifiedQueryClause<BooleanQueryClause> bQryClause = _booleanQuery.findBooleanQueryClause("createdOrEditedBy");
		return bQryClause != null ? Iterables.getFirst(bQryClause.getClause().getClauses(),null)	// any clause
											 .getClause().<LoginID>getValue()
								  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE / LAST UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE createdInRange(final Range<Instant> range) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATE_DATE);
		
		// Search for a full day
		Range<Instant> newDateRange = range;
		if (range.getLowerBound() != null
		&& 	range.getUpperBound() != null) {
			// TODO why add one day to the upper bound?
			newDateRange = Range.openClosed(range.getLowerBound(),
											range.getUpperBound()
												 .plus(1,ChronoUnit.DAYS));
		}
		_queryClausesFactory.addMustRangeQueryClause(fieldId,
													 newDateRange);
		return (SELF_TYPE)this;
	}
	public Range<Instant> getCreatedRange() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATE_DATE);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE lastUpdatedInRange(final Range<Instant> range) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATE_DATE);
		_queryClausesFactory.addMustRangeQueryClause(fieldId,
													 range);				
		return (SELF_TYPE)this;
	}
	public Range<Instant> getLastUpdatedRange() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATE_DATE);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
    public SearchFilterAsCriteriaString toCriteriaString() {
		SearchFilterToAndFromCriteriaStringMarshaller<SELF_TYPE> m = new SearchFilterToAndFromCriteriaStringMarshaller<>(TypeMetaDataInspector.singleton());
		return m.toCriteriaString((SELF_TYPE)this);
    }
	public static <F extends SearchFilterForModelObject> F fromCriteriaString(final SearchFilterAsCriteriaString criteriaStr) {
		SearchFilterToAndFromCriteriaStringMarshaller<F> m = new SearchFilterToAndFromCriteriaStringMarshaller<>(TypeMetaDataInspector.singleton());
		return m.fromCriteriaString(criteriaStr);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <F extends SearchFilterForModelObject> F  createCloneOf(final F other) {
		SearchFilterAsCriteriaString serialized = other.toCriteriaString();
		return SearchFilterForModelObjectBase.<F>fromCriteriaString(serialized);
	}
}
