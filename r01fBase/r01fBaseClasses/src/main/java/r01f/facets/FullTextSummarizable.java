package r01f.facets;

import r01f.types.summary.Summary;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		FullTextSummarizable summarizable = new FullTextSummarizable() {
 * 												@Override
 * 												public Summary getFullTextSummary() {
 * 													// use SummaryBuilder to build the full text summary	
 * 												}
 * 											}
 * </pre>
 * The [full text summary] can be created from the "normal" {@link Summary}
 * using a helpper:
 * <pre class='brush:java'>
 * 		public class MyLangIndependentSummarizableType
 * 	   	  implements HasLangInDependentNamedFacet,
 * 					 HasSummaryFacet {
 * 
 *	        @Override
 *	        public Summarizable asSummarizable() {
 *				...
 *	        }
 *    	    @Override
 *    	    public FullTextSummarizable asFullTextSummarizable() {
 *    	    	return SummarizableBuilder.fullTextSummarizableFrom(this);
 *    	    }
 *      }
 * </pre>
 */
@FunctionalInterface
public interface FullTextSummarizable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Interface to be implemented by {@link IndexableModelObject}s that can be summarized to
	 * be full-text indexed
	 */
	@FunctionalInterface
	public static interface HasFullTextSummaryFacet
			 		extends Facet {
		/**
		 * @return the full text summary
		 */
		public FullTextSummarizable asFullTextSummarizable();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the full text summary
	 */
	public Summary getFullTextSummary();
}
