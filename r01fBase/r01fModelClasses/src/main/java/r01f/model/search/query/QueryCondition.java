package r01f.model.search.query;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum QueryCondition 
 implements EnumExtended<QueryCondition> {
	beEqualTo,			// text, date or number
	beInsideRange,		// a range for example of dates or numbers
	beWithin,			// within an spectrum of values (ie an enum)
	containAll,			// contain all elements
	last,				// for dates: ie last x days from now
	next,				// for dates: ie next x days from now
	containText,		// text contain
	beNull,				// be null
	haveData;			// have data (not null)
	
	public static String pattern() {
		return CollectionUtils.of(QueryCondition.values()).toStringSeparatedWith('|');
	}
	public static QueryCondition fromName(final String name) {
		return EnumExtended.fromName(name,QueryCondition.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + QueryCondition.class + " element with name=" + name));
	}
	public static QueryCondition fromQuery(final QueryClause clause) {
		QueryCondition outPredicate = null;
		if (clause instanceof EqualsQueryClause) {
			outPredicate = beEqualTo;
		} else if (clause instanceof ContainsTextQueryClause) {
			outPredicate = containText;
		} else if (clause instanceof ContainedInQueryClause) {
			outPredicate = beWithin;
		} else if (clause instanceof ContainsAllQueryClause) {
			outPredicate = containAll;
		} else if (clause instanceof RangeQueryClause) {
			outPredicate = beInsideRange;
		} else if (clause instanceof IsNullQueryClause) {
			outPredicate = beNull;
		} else if (clause instanceof HasDataQueryClause) {
			outPredicate = haveData;
		} else if (clause instanceof BooleanQueryClause) {
			throw new IllegalStateException("NOT currently supported!!");
		}
		return outPredicate;
	}
}