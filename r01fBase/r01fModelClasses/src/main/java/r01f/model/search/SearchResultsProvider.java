package r01f.model.search;

import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.model.search.query.SearchResultsOrdering;

/**
 * Provider of {@link SearchResults} for a page
 * <pre class='brush:java'>
 *	// Provider of search results pages (quite verbose for java6 WTF!)
 *	SearchResultsProvider<MyFilter,MySearchResultsItem> resultsProvider = 
 *			new SearchResultsProvider<MyFilter,MySearchResultsItem>() {
 *						@Override
 *						public SearchResults<MyFilter,MySearchResultsItem> provide(final searchFilter,
 *																				   final Collection<SearchResultsOrdering> ordering,
 *																				   final int startPosition,
 *																				   final int pageSize) {
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
	public SearchResults<F,I> provide(final F searchFilter,
									  final Collection<SearchResultsOrdering> ordering,
									  final int startPosition,
									  final int numberOfRows);
		
	/**
	 * Provides 
	 * @param searchFilter
	 * @param startPosition
	 * @param numberOfRows
	 * @return
	 */
	public default SearchResults<F,I> provide(final F searchFilter,
									  		  final int startPosition,
									  		  final int numberOfRows) {
		return this.provide(searchFilter,
							null,	// no ordering
							startPosition,
							numberOfRows);
	}
	/**
	 * Provides
	 * @param searchFilter
	 * @param startPosition
	 * @return
	 */
	public default SearchResults<F,I> provide(final F searchFilter,
											  final Collection<SearchResultsOrdering> ordering,
											  final int startPosition) {
		return this.provide(searchFilter,
							ordering,
							startPosition,
							SearchResults.DEFAULT_PAGE_SIZE);
	}
	/**
	 * Provides
	 * @param searchFilter
	 * @param startPosition
	 * @return
	 */
	public default SearchResults<F,I> provide(final F searchFilter,
											  final int startPosition) {
		return this.provide(searchFilter,
							null,	// no ordering
							startPosition);
	}
}
