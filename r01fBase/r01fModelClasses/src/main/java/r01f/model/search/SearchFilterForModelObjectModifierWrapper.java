package r01f.model.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import lombok.RequiredArgsConstructor;
import r01f.generics.TypeRef;
import r01f.model.metadata.FieldID;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsAllQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.ContainsTextQueryClause.ContainedTextAt;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.HasDataQueryClause;
import r01f.model.search.query.IsNullQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@RequiredArgsConstructor
public class SearchFilterForModelObjectModifierWrapper<F extends SearchFilterForModelObjectBase<F>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final F _wrappedFilter;

/////////////////////////////////////////////////////////////////////////////////////////
//  MODIFIERS
/////////////////////////////////////////////////////////////////////////////////////////
	public F mustMeetThisMetaDataCondition(final QueryClause metaDataCondition) {
		return addOrUpdateClause(metaDataCondition,QueryClauseOccur.MUST);
	}
	public F mustMeetThisMetaDataCondition(final Set<QueryClause> metaDataConditions) {
		for (QueryClause condition : metaDataConditions) {
			this.mustMeetThisMetaDataCondition(condition);
		}
		return _wrappedFilter;
	}
	public F mustNOTMeetThisMetaDataCondition(final QueryClause metaData) {
		return addOrUpdateClause(metaData,QueryClauseOccur.MUST_NOT);
	}
	public F mustNOTMeetThisMetaDataCondition(final Set<QueryClause> metaDataConditions) {
		for (QueryClause condition : metaDataConditions) {
			this.mustNOTMeetThisMetaDataCondition(condition);
		}
		return _wrappedFilter;
	}
	public F canMeetThisMetaDataCondition(final QueryClause metaDataCondition) {
		return addOrUpdateClause(metaDataCondition,QueryClauseOccur.SHOULD);
	}
	public F canMeetThisMetaDataCondition(final Set<QueryClause> metaDataConditions) {
		Preconditions.checkArgument(metaDataConditions != null,"The metadata condition cannot be null");
		for (QueryClause condition : metaDataConditions) {
			this.canMeetThisMetaDataCondition(condition);
		}
		return _wrappedFilter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD / REMOVE / UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	public void addClause(final QueryClause clause,
						  final QueryClauseOccur occur) {
		if (clause == null) return;
		QueryClauseOccur theOccur = occur != null ? occur : QueryClauseOccur.MUST;

		if (_wrappedFilter.getBooleanQuery() == null) _wrappedFilter.setBooleanQuery(new BooleanQueryClause(new HashSet<>()));
		_wrappedFilter.getBooleanQuery()
					  .add(clause,theOccur);
	}
	public boolean removeAllClausesFor(final FieldID fieldId) {
		if (_wrappedFilter.getBooleanQuery() == null) return false;
		return _wrappedFilter.getBooleanQuery().removeAllFor(fieldId);
	}
	public F addOrUpdateClause(final QueryClause clause,
							   final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(clause != null,"The clause condition cannot be null");
		QueryClause existing = _wrappedFilter.getAccessorWrapper()
											 .queryClauses()
											 .find(clause.getFieldId());
		if (existing != null) {
			this.removeAllClausesFor(clause.getFieldId());
			this.addClause(clause,
					   	   clauseOccur);
		} else {
			this.addClause(clause,
					   	   clauseOccur);
		}
		return _wrappedFilter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	public <T> F addOrUpdateEqualsClauseIfValueNOTNullOrRemoveItIfNull(final FieldID fieldId,
										 							   final T eqValue,
										 							   final QueryClauseOccur clauseOccur) {
		if (eqValue == null) {
			this.removeAllClausesFor(fieldId);
		} else {
			this.addOrUpdateEqualsClause(fieldId,
										 eqValue,
										 clauseOccur);
		}
		return _wrappedFilter;
	}
	public <T> F addOrUpdateEqualsClause(final FieldID fieldId,
										 final T eqValue,
										 final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(eqValue != null,"The value to be set in the equals clause cannot be null");
		QueryClause clause = _wrappedFilter.getAccessorWrapper()
										   .queryClauses()
										   .find(fieldId);
		if (clause != null) {
			EqualsQueryClause<T> eq = clause.as(new TypeRef<EqualsQueryClause<T>>() {/* nothing */});
			eq.setEqValue(eqValue);
		} else {
			if (eqValue instanceof Range || eqValue instanceof com.google.common.collect.Range) throw new IllegalArgumentException("For range conditions use _addOrUpdateRangeClause!");
			EqualsQueryClause<T> eq = EqualsQueryClause.forField(fieldId)
													   .of(eqValue);
			this.addClause(eq,clauseOccur);
		}
		return _wrappedFilter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTAINED IN
/////////////////////////////////////////////////////////////////////////////////////////
	public <T> F addOrUpdateContainedInClauseIfValueNOTNullOrRemoveItIfNull(final FieldID fieldId,
											  								final T[] values,
											  								final QueryClauseOccur clauseOccur) {
		if (CollectionUtils.isNullOrEmpty(values)) {
			this.removeAllClausesFor(fieldId);
		} else {
			this.addOrUpdateContainedInClause(fieldId,
											  values,
											  clauseOccur);
		}
		return _wrappedFilter;
	}
	public <T> F addOrUpdateContainedInClause(final FieldID fieldId,
											  final T[] values,
											  final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(CollectionUtils.hasData(values),"The values to be set in the contained in clause cannot be null");

		QueryClause clause = _wrappedFilter.getAccessorWrapper()
										   .queryClauses()
										   .find(fieldId);
		if (clause != null) {
			ContainedInQueryClause<T> contained = clause.as(new TypeRef<ContainedInQueryClause<T>>() {/* nothing */ });
			contained.setSpectrum(values);
		} else {
			@SuppressWarnings("unused")
			ContainedInQueryClause<T> contained = ContainedInQueryClause.<T>forField(fieldId)
															 			.within(values);
			this.addClause(contained,clauseOccur);
		}
		return _wrappedFilter;
	}
	@SuppressWarnings("unchecked")
	public <T> F addOrUpdateContainedInClause(final FieldID fieldId,
											  final Collection<T> values,
											  final QueryClauseOccur clauseOccur) {
		return this.addOrUpdateContainedInClause(fieldId,
											 	 (T[])values.toArray(),
											 	 clauseOccur);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTAIN ALL
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public <T> F addOrUpdateContainsAllClauseIfValueNOTNullOrRemoveItIfNull(final FieldID fieldId,
											  								final T[] values,
											  								final QueryClauseOccur clauseOccur) {
		if (CollectionUtils.isNullOrEmpty(values)) {
			this.removeAllClausesFor(fieldId);
		} else {
			this.addOrUpdateContainsAllClause(fieldId,
											  values,
											  clauseOccur);
		}
		return _wrappedFilter;
	}
	@SuppressWarnings("unused")
	public <T> F addOrUpdateContainsAllClause(final FieldID fieldId,
											  final T[] values,
											  final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(CollectionUtils.hasData(values),"The values to be set in the contained in clause cannot be null");

		QueryClause clause = _wrappedFilter.getAccessorWrapper()
										   .queryClauses()
										   .find(fieldId);
		if (clause != null) {
			ContainsAllQueryClause<T> containsAll = clause.as(new TypeRef<ContainsAllQueryClause<T>>() {/* nothing */ });
			containsAll.setSpectrum(values);
		} else {
			ContainsAllQueryClause<T> contained = ContainsAllQueryClause.<T>forField(fieldId)
															 			.all(values);
			this.addClause(contained,clauseOccur);
		}
		return _wrappedFilter;
	}
	@SuppressWarnings("unchecked")
	public <T> F addOrUpdateContainsAllClause(final FieldID fieldId,
											  final Collection<T> values,
											  final QueryClauseOccur clauseOccur) {
		return this.addOrUpdateContainsAllClause(fieldId,
											 	 (T[])values.toArray(),
											 	 clauseOccur);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	public <T extends Comparable<T>> F addOrUpdateRangeClauseIfValueNOTNullOrRemoveItIfNull(final FieldID fieldId,
															  						   		final Range<T> range,
															  						   		final QueryClauseOccur clauseOccur) {
		if (range == null) {
			this.removeAllClausesFor(fieldId);
		} else {
			this.addOrUpdateRangeClause(fieldId,
										range,
										clauseOccur);
		}
		return _wrappedFilter;
	}
	public <T extends Comparable<T>> F addOrUpdateRangeClause(final FieldID fieldId,
															  final Range<T> range,
															  final QueryClauseOccur clauseOccur) {
		QueryClause clause = _wrappedFilter.getAccessorWrapper()
										   .queryClauses()
										   .find(fieldId);
		if (clause != null) {
			RangeQueryClause<T> requestRangeClause = clause.as(new TypeRef<RangeQueryClause<T>>() {/* nothing */});
			requestRangeClause.setRange(range);
		} else {
			RangeQueryClause<T> requestRangeClause = RangeQueryClause.forField(fieldId)
																	 .of(range);
			this.addClause(requestRangeClause,clauseOccur);
		}
		return _wrappedFilter;
	}
	public <T extends Comparable<T>> F addOrUpdateRangeClause(final FieldID fieldId,
															  final com.google.common.collect.Range<T> range,
															  final QueryClauseOccur clauseOccur) {
		return this.addOrUpdateRangeClause(fieldId,
									   	   new Range<>(range),
									   	   clauseOccur);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTAINS TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	public <T extends Comparable<T>> F addOrUpdateContainsTextClauseIfValueNOTNullOrRemoveItIfNull(final FieldID fieldId,
															  						   			   final String text,
															  						   			   final QueryClauseOccur clauseOccur) {
		if (Strings.isNullOrEmpty(text)) {
			this.removeAllClausesFor(fieldId);
		} else {
			this.addOrUpdateContainsTextClause(fieldId,
											   text,
											   clauseOccur);
		}
		return _wrappedFilter;
	}
	public F addOrUpdateContainsTextClause(final FieldID fieldId,
										   final String text,
										   final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(Strings.isNOTNullOrEmpty(text),"The text to be set in the contains text clause cannot be null");
		QueryClause clause = _wrappedFilter.getAccessorWrapper()
										   .queryClauses()
										   .find(fieldId);
		if (clause != null) {
			ContainsTextQueryClause contains = clause.as(ContainsTextQueryClause.class);
			contains.setText(text);
		} else {
			ContainsTextQueryClause contains = ContainsTextQueryClause.forField(fieldId)
																	  .at(ContainedTextAt.FULL)
																	  .text(text)
																	  .languageIndependent();
			this.addClause(contains,clauseOccur);
		}
		return _wrappedFilter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HAS DATA / IS NULL
/////////////////////////////////////////////////////////////////////////////////////////
	public F addOrUpdateIsNullClause(final FieldID fieldId,
									 final boolean isNull,
									 final QueryClauseOccur clauseOccur) {
		QueryClause clause = _wrappedFilter.getAccessorWrapper().queryClauses().find(fieldId);
		if (clause != null) {
			// nothing... the clause already exists
		} else {
			IsNullQueryClause isNullClause = IsNullQueryClause.forField(fieldId)
															  .isNull(isNull);
			this.addClause(isNullClause,clauseOccur);
		}
		return _wrappedFilter;
	}
	public F addOrUpdateHasDataClause(final FieldID fieldId,
									  final QueryClauseOccur clauseOccur) {
		QueryClause clause = _wrappedFilter.getAccessorWrapper()
										   .queryClauses()
										   .find(fieldId);
		if (clause != null) {
			// nothing... the clause already exists
		} else {
			HasDataQueryClause hasData = HasDataQueryClause.forField(fieldId);
			this.addClause(hasData,clauseOccur);
		}
		return _wrappedFilter;
	}
}
