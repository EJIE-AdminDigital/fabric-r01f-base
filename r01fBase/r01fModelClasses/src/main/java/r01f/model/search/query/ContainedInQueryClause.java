package r01f.model.search.query;

import java.lang.reflect.Array;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Usage
 * <pre class='brush:java'>
 *		ContainedInQueryClause<Integer> spectrum = ContainedInQueryClause.<Integer>forMetaData("myField")
 *																	  	 .within(new Integer[] {2,3});
 * </pre>
 * @param <T>
 */
@MarshallType(as="containedInClause") 
@Accessors(prefix="_")
@NoArgsConstructor
public class ContainedInQueryClause<T>
     extends ContainsQueryClauseBase<T> {

	private static final long serialVersionUID = -2289516417263071932L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	ContainedInQueryClause(final FieldID fieldId,
						   final T[] spectrum) {
		super(fieldId,
			  spectrum);
	}	
	public static ContainedInQueryClauseStep1Builder forField(final FieldID fieldId) {
		return new ContainedInQueryClauseStep1Builder(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainedInQueryClauseStep1Builder {
		private final FieldID _fieldId;
		
		public <T> ContainedInQueryClause<T> within(final T[] spectrum) {
			return new ContainedInQueryClause<>(_fieldId,
											  	spectrum);
		}
		@SuppressWarnings("unchecked")
		public <T> ContainedInQueryClause<T> within(final Collection<T> spectrum,final Class<T> type) {
			T[] array = (T[])Array.newInstance(type,spectrum.size());
			return this.within(array);
			
		}
//		@SuppressWarnings("unchecked")
//		public <T> ContainedInQueryClause<T> within(final Collection<T> spectrum,
//													final Class<T> spectrumObjType) {
//			return new ContainedInQueryClause<>(_fieldId,
//											  	(T[])spectrum.toArray());
//		}
	} 
}
