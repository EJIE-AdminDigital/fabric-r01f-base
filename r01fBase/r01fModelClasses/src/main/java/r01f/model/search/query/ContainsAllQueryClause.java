package r01f.model.search.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Usage
 * <pre class='brush:java'>
 *		ContainsAllQueryClause<Integer> spectrum = ContainsAllQueryClause.<Integer>forMetaData("myField")
 *																	  	 .all(new Integer[] {2,3});
 * </pre>
 * @param <T>
 */
@MarshallType(as="containsAllClause") 
@Accessors(prefix="_")
@NoArgsConstructor
public class ContainsAllQueryClause<T>
     extends ContainsQueryClauseBase<T> {

	private static final long serialVersionUID = -8832812784591841196L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	ContainsAllQueryClause(final FieldID fieldId,
						   final T[] spectrum) {
		super(fieldId,
			  spectrum);
	}	
	public static ContainsAllQueryClauseStep1Builder forField(final FieldID fieldId) {
		return new ContainsAllQueryClauseStep1Builder(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainsAllQueryClauseStep1Builder {
		private final FieldID _fieldId;
		
		public <T> ContainsAllQueryClause<T> all(final T[] spectrum) {
			return new ContainsAllQueryClause<>(_fieldId,
											  	spectrum);
		}
	} 
}
