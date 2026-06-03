package r01f.model.search.query;

import r01f.enums.EnumExtended;

public enum QueryClauseType 
 implements EnumExtended<QueryClauseType> {
	BOOLEAN,		// join of two or more clauses
	EQUALS,			// a = b
	CONTAINED_IN,	// a,b,c...
	CONTAINS_TEXT,	// text*
	RANGE,			// (..10) / (1..10) (10..)
	NULL,			// a IS NULL / b IS NOT NULL
	HAS_DATA;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static QueryClauseType fromName(final String name) {
		return EnumExtended.fromName(name,QueryClauseType.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + QueryClauseType.class + " element with name=" + name));
	}
}
