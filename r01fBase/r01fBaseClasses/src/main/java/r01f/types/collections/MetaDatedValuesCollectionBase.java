package r01f.types.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.patterns.FactoryFrom;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@ConvertToDirtyStateTrackable
public abstract class MetaDatedValuesCollectionBase<T,I extends MetaDatedValuesCollectionItemBase<T,I>,
													SELF_TYPE extends MetaDatedValuesCollectionBase<T,I,SELF_TYPE>>
	 		  extends LinkedHashSet<I> {

	   private static final long serialVersionUID = -5293574857431064553L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final FactoryFrom<T,I> _itemFactory;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MetaDatedValuesCollectionBase(final FactoryFrom<T,I> itemFactory) {
		_itemFactory = itemFactory;
	}
	public MetaDatedValuesCollectionBase(final FactoryFrom<T,I> itemFactory,
								     	 final int length) {
		super(length);
		_itemFactory = itemFactory;
	}
	@SuppressWarnings("unchecked")
	public MetaDatedValuesCollectionBase(final FactoryFrom<T,I> itemFactory,
									 	 final I... items) {
		this(itemFactory);
		this.addItems(items);
	}
	public MetaDatedValuesCollectionBase(final FactoryFrom<T,I> itemFactory,
									 	 final Collection<I> items) {
		this(itemFactory);
		this.addItems(items);
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE addItems(final I... items) {
		if (CollectionUtils.hasData(items)) this.addAll(Arrays.stream(items)
															  .filter(item -> item.getValue() != null)
															  .collect(Collectors.toList()));
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE addItems(final Collection<I> items) {
		if (CollectionUtils.hasData(items)) this.addAll(items.stream()
															 .filter(item -> item.getValue() != null)
															 .collect(Collectors.toList()));
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE addValues(final T... values) {
		if (CollectionUtils.hasData(values)) this.addItems(Arrays.stream(values)
															  	 .map(item -> _itemFactory.from(item))
															  	 .collect(Collectors.toList()));
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE addValues(final Collection<T> values) {
		if (CollectionUtils.hasData(values)) this.addItems(values.stream()
															   	 .map(item -> _itemFactory.from(item))
															   	 .collect(Collectors.toList()));
		return (SELF_TYPE)this;
	}
	public Collection<I> removeItemsWithValueMatching(final Predicate<T> valuePred) {
		if (this.size() == 0) return Collections.emptyList();

		// find the items to be removed
		Collection<I> itemsToBeRemoved = this.getItemsMatching(item -> item.getValue() != null
											  						&& valuePred.test(item.getValue()))
											 .stream()
											 .collect(Collectors.toList());
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed ? itemsToBeRemoved
					   : Collections.emptyList();
	}
	public Collection<I> removeItemMatching(final Predicate<I> pred) {
		if (this.size() == 0) return Collections.emptyList();

		// find the items to be removed
		Collection<I> itemsToBeRemoved = this.getItemsMatching(item -> pred.test(item))
											 .stream()
											 .collect(Collectors.toList());
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed ? itemsToBeRemoved
					   : Collections.emptyList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the SINGLE item with the given id (if any) 
	 * If more than 1 item matches the given id, only one is returned and a warning is logged 
	 * If no item matches the given id, null is returned 
	 * NOTE: the collection is supposed to have a SINGLE item matching the given predicate BUT if it contains more than 1, only one is returned and a warning is logged 
	 * @param itemId
	 * @return
	 */
	public MetaDatedValuesCollectionItemBase<T,I> getSingleItemWithId(final CollectionItemID itemId) {
		Collection<I> outItems = this.getItemsWithIds(List.of(itemId));
		if (outItems != null && outItems.size() > 1) log.warn("The collection is supposed to have a SINGE item matching the given predicate BUT contains {}: returning ONLY one",
															  outItems.size());
		return outItems != null && outItems.size() >= 1 ? outItems.stream()
																  .findFirst().orElse(null)
														: null;
	}
	/**
	 * Returns the items with the given id (if any)
	 * @param itemId
	 * @return
	 */
	public Collection<I> getItemsWithId(final CollectionItemID itemId) {
		return this.getItemsWithIds(List.of(itemId)); 
	}
	/**
	 * Returns the items with the given ids (if any)
	 * @param itemIds
	 * @return
	 */
	public Collection<I> getItemsWithIds(final Collection<CollectionItemID> itemIds) {
		Predicate<I> pred = item -> item.getId() != null
								 && item.getId().isContainedIn(itemIds);
		return this.getItemsMatching(pred);
	}
	/**
	 * Returns the values of the items with the given id (if any)
	 * @param itemId
	 * @return
	 */
	public T getValueOfSingleValueItemWithId(final CollectionItemID itemId) { 
		MetaDatedValuesCollectionItemBase<T,I> item = this.getSingleItemWithId(itemId); 
		return item != null ? item.getValue() : null; 
	}
	/**
	 * Returns only objs in a certain language
	 * @param tag
	 * @return
	 */
	protected Collection<T> getValueOfItemsWithId(final CollectionItemID itemId) {
		Collection<I> items = this.getItemsWithId(itemId);
		return CollectionUtils.hasData(items) ? items.stream()
											  		 .map(item -> item.getValue())
											  		 .filter(val -> val != null)
											  		 .toList()
										 	  : Collections.emptyList();
	}
	/**
	 * Returns only objs tagged by a certain tag
	 * @param tag
	 * @return
	 */
	protected Collection<I> getItemsTaggedWith(final String tag) {
		Predicate<I> pred = item -> item.asTaggeable()
						   			    .containsTag(tag);
		return this.getItemsMatching(pred);
	}
	/**
	 * Returns only objs tagged by a certain tag
	 * @param tag
	 * @return
	 */
	protected Collection<T> getValuesOfItemsTaggedWith(final String tag) {
		Predicate<I> pred = item -> item.asTaggeable()
						   			    .containsTag(tag);
		return this.getValuesOfItemsMatching(pred);
	}
	/**
	 * Returns only items matching the given predicate
	 * @param pred
	 * @return
	 */
	public Collection<I> getItemsMatching(final Predicate<I> pred) {
		return this.stream()
				   .filter(pred)
				   .toList();
	}
	/**
	 * Returns the values of items matching the given predicate
	 * @param pred
	 * @return
	 */
	public Collection<T> getValuesOfItemsMatching(final Predicate<I> pred) {
		return this.stream()
				   .filter(pred)
				   .map(item -> item.getValue())
				   .toList();
	}
}