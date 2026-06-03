package r01f.facets;

import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.types.summary.Summary;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		Summarizable outSummarizable = new ImmutableSummarizable(this.getClass()) {
 * 												@Override
 * 												public Summary getSummary() {
 *													// use SummaryBuilder to build a summary
 *												}
 * 									   }
 * </pre>
 * The summary can be created from the [name]; to do so there exists 
 * two cases:
 * a) If the type implements {@link HasLangInDependentNamedFacet}
 * <pre class='brush:java'>
 * 		public class MyLangIndependentSummarizableType
 * 	   	  implements HasLangInDependentNamedFacet,
 * 					 HasSummaryFacet {
 * 
 *	        @Override
 *	        public Summarizable asSummarizable() {
 *	        	Summary sum = SummaryBuilder.languageInDependent()
 *	        								.create(this);
 *	        	return SummarizableBuilder.summarizableFrom(sum);
 *	        }
 *      }
 * </pre>
 * b) If the type implements {@link HasLangDependentNamedFacet}
 * <pre class='brush:java'>
 * 		public class MyLangDependentSummarizableType
 * 	   	  implements HasLangDependentNamedFacet,
 * 					 HasSummaryFacet {
 * 
 *	        @Override
 *	        public Summarizable asSummarizable() {
 *	        	Summary sum = SummaryBuilder.languageDependent()
 *	        								.create(this);
 *	        	return SummarizableBuilder.summarizableFrom(sum);
 *	        }
 *      }
 * </pre>
 */
public interface Summarizable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Interface to be implemented by {@link IndexableModelObject}s that can be summarized to
	 * be full-text indexed
	 */
	@FunctionalInterface
	public static interface HasSummaryFacet
			 		extends Facet {
		/**
		 * @return the full text summary
		 */
		public Summarizable asSummarizable();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the summary
	 * @return
	 */
	public Summary getSummary();
	/**
	 * Sets the summary
	 * @param summary
	 */
	public void setSummary(final Summary summary);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@RequiredArgsConstructor
	public static abstract class ImmutableSummarizable
				      implements Summarizable {
		@Override
		public void setSummary(final Summary summary) {
			throw new UnsupportedOperationException("Cannot set summary!");
		}
	}
}
