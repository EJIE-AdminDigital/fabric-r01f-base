package r01f.model.search;

import lombok.experimental.Accessors;

/**
 * Provider of {@link SearchResults} for a page
 * <pre class='brush:java'>
 *	// Provider of search results pages (quite verbose for java6 WTF!)
 *	SearchResultsProvider<MyFilter,MySearchResultsItem> resultsProvider = 
 *			new SearchResultsProvider<MyFilter,MySearchResultsItem>() {
 *						@Override
 *						public SearchResults<MyFilter,MySearchResultsItem> provide(final searchFilter,final int startPosition,final int pageSize) {
 *							// ... use some api to retrieve the results
 *							return searchResults;
 *						}
 *			};
 * </pre>
 * @param <F>
 * @param <I>
 */
@FunctionalInterface
@Accessors(prefix="_")
public interface SearchResultsProvider<F extends SearchFilter,
								   	   I extends SearchResultItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides 
	 * @param searchFilter
	 * @param startPosition
	 * @param numberOfRows
	 * @return
	 */
	public SearchResults<F,I> provide(final F searchFilter,final int startPosition,
									  final int numberOfRows);
	/**
	 * Provides
	 * @param searchFilter
	 * @param startPosition
	 * @return
	 */
	public default SearchResults<F,I> provide(final F searchFilter,final int startPosition) {
		return this.provide(searchFilter,startPosition,
							SearchResults.DEFAULT_PAGE_SIZE);
	}
}
