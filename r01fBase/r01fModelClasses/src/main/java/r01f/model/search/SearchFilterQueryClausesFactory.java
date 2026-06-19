package r01f.model.search;

import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.generics.TypeRef;
import r01f.locale.Language;
import r01f.model.metadata.FieldID;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
public class SearchFilterQueryClausesFactory<F extends SearchFilterForModelObjectBase<F>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final SearchFilterForModelObjectAccessorWrapper<F> _accessorWrapper;
	private final SearchFilterForModelObjectModifierWrapper<F> _modifierWrapper;
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////	
	public <T> void addMustEqualsQueryClause(final FieldID fieldId,
										 	 final T val) {
		this.addEqualsQueryClause(fieldId,
								  val,
								  QueryClauseOccur.MUST);
	}
	public <T> void addMustNOTEqualsQueryClause(final FieldID fieldId,
										 	 	final T val) {
		this.addEqualsQueryClause(fieldId,
								  val,
								  QueryClauseOccur.MUST_NOT);
	}
	public <T> void addShouldEqualsQueryClause(final FieldID fieldId,
										 	   final T val) {
		this.addEqualsQueryClause(fieldId,
								  val,
								  QueryClauseOccur.SHOULD);
	}
	public <T> void addEqualsQueryClause(final FieldID fieldId,
										 final T val,
										 final QueryClauseOccur occur) {
		if (val == null) {
			_modifierWrapper.removeAllClausesFor(fieldId);
			return;
		}

		// If there exists an equals query clause, modify it; if not set it
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			// existing clause
			if (clause instanceof EqualsQueryClause) {
				EqualsQueryClause<T> existingClause = clause.as(new TypeRef<EqualsQueryClause<T>>() { /* nothing */ });
				existingClause.setEqValue(val);
			} else {
				log.warn(Strings.customized("The filter query clause is NOT of the expected type {} or is null",
											EqualsQueryClause.class));
			}
		} else {
			// Non existing clause
			EqualsQueryClause<T> newClause = EqualsQueryClause.forField(fieldId)
															  .of(val);
			_modifierWrapper.addClause(newClause,occur);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEXT
/////////////////////////////////////////////////////////////////////////////////////////	
	public void addMustTextQueryClause(final FieldID fieldId,
								   	   final String text,final Language lang) {
		this.addTextQueryClause(fieldId,
								text,lang,QueryClauseOccur.MUST);
	}
	public void addMustNOTTextQueryClause(final FieldID fieldId,
								   	      final String text,final Language lang) {
		this.addTextQueryClause(fieldId,
								text,lang,QueryClauseOccur.MUST_NOT);
	}
	public void addShouldTextQueryClause(final FieldID fieldId,
								   	     final String text,final Language lang) {
		this.addTextQueryClause(fieldId,
								text,lang,QueryClauseOccur.SHOULD);
	}
	public void addTextQueryClause(final FieldID fieldId,
								   final String text,final Language lang,
								   final QueryClauseOccur occur) {
		if (Strings.isNullOrEmpty(text)) {
			_modifierWrapper.removeAllClausesFor(fieldId);
			return;
		}

		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			// existing clause
			if (clause instanceof ContainsTextQueryClause) {
				ContainsTextQueryClause textClause = clause.as(ContainsTextQueryClause.class);
				textClause.setText(text);
			} else {
				log.warn(Strings.customized("The text clause is NOT of the expected type {} or is null",
										    ContainsTextQueryClause.class));
			}
		} else {
			// Non existing clause
			ContainsTextQueryClause textClause = ContainsTextQueryClause.forField(fieldId)
															  			.fullText(text)
															  			.in(lang);
			_modifierWrapper.addClause(textClause,occur);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE
/////////////////////////////////////////////////////////////////////////////////////////	
	public <T extends Comparable<? super T>> void addMustRangeQueryClause(final FieldID fieldId,
								   								 	  	  final Range<T> range) {
		this.addRangeQueryClause(fieldId,range,
								 QueryClauseOccur.MUST);
	}
	public <T extends Comparable<? super T>> void addMustNOTRangeQueryClause(final FieldID fieldId,
								   								 	  	  	 final Range<T> range) {
		this.addRangeQueryClause(fieldId,range,
								 QueryClauseOccur.MUST_NOT);
	}
	public <T extends Comparable<? super T>> void addShouldNOTRangeQueryClause(final FieldID fieldId,
								   								 	  	  	   final Range<T> range) {
		this.addRangeQueryClause(fieldId,range,
								 QueryClauseOccur.SHOULD);
	}
	public <T extends Comparable<? super T>> void addRangeQueryClause(final FieldID fieldId,
								   								 	  final Range<T> range,
								   									  final QueryClauseOccur occur) {
		if (range == null) {
			_modifierWrapper.removeAllClausesFor(fieldId);
			return;
		}
		
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			RangeQueryClause<T> rangeClause = (RangeQueryClause<T>)clause;	// clause.as(new TypeRef<RangeQueryClause<Instant>>() { /* nothing */ });
			rangeClause.setRange(range);
		} else {
			RangeQueryClause<T> rangeClause = RangeQueryClause.forField(fieldId)
															  .of(range);
			_modifierWrapper.addClause(rangeClause,occur);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTAINED IN
/////////////////////////////////////////////////////////////////////////////////////////	
	public <T> void addMustBeContainedInQueryClause(final FieldID fieldId,
										  	  	  	final Collection<T> values,final Class<T> type) {
		this.addContainedInQueryClause(fieldId,
									   values,type,
									   QueryClauseOccur.MUST);
	}
	public <T> void addMustNOTBeContainedInQueryClause(final FieldID fieldId,
										  	  	  	   final Collection<T> values,final Class<T> type) {
		this.addContainedInQueryClause(fieldId,
									   values,type,
									   QueryClauseOccur.MUST_NOT);
	}
	public <T> void addShouldBeContainedInQueryClause(final FieldID fieldId,
										  	  	  	  final Collection<T> values,final Class<T> type) {
		this.addContainedInQueryClause(fieldId,
									   values,type,
									   QueryClauseOccur.SHOULD);
	}
	public <T> void addContainedInQueryClause(final FieldID fieldId,
										  	  final Collection<T> values,final Class<T> type,
										  	  final QueryClauseOccur occur) {
		QueryClause clause = _accessorWrapper.queryClauses()
											 .find(fieldId);
		if (CollectionUtils.isNullOrEmpty(values)) {
			_modifierWrapper.removeAllClausesFor(fieldId);
			return;
		}
		
		if (clause != null) {
			@SuppressWarnings({ "unchecked" })
			ContainedInQueryClause<T> typeContainedIn = clause.as(ContainedInQueryClause.class);
			typeContainedIn.setSpectrumFrom(values,
											type);
		} else {
			ContainedInQueryClause<T> typeContainedIn = ContainedInQueryClause.forField(fieldId)
									   					   					  .within(values,type);
			_modifierWrapper.addClause(typeContainedIn,occur);
		}
	}
}
