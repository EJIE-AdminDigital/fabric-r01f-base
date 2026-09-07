package r01f.types.hierarchy;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.facets.HasOID;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.patterns.CommandOn;
import r01f.patterns.FactoryFrom;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@Accessors(prefix="_")
public abstract class HasChildrenHieararchicalBase<T extends HasChildren<T>>
			  extends HasChildrenListBackedBase<T>
		   implements HasChildren<T>,
		   			  // List<T>,	// do NOT implement List/Collection > problems while marshalling since it's detected as a Collection<Collection> that cannot be marshalled to XML by Jackson 	
			   		  Debuggable,
			   		  Serializable {

	private static final long serialVersionUID = -8444274808218172580L;
/////////////////////////////////////////////////////////////////////////////////////////
//	NON-SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private final transient Function<T,String> _itemDebug;
	private static final Function<Object,String> DEFAULT_ITEM_DEBUG = item -> {
																			if (item instanceof Debuggable dbg) {
																				return dbg.debugInfo().toString();
																			} else if (item instanceof CanBeRepresentedAsString str) {
																				return str.asString();
																			} else if (item instanceof HasOID hasOid) {
																				return hasOid.getOid().asString();
																			} 
																			return item.toString();
																	  };
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public HasChildrenHieararchicalBase() {
		this((Function<T,String>) (Function<?, String>)DEFAULT_ITEM_DEBUG);
	}
	@SuppressWarnings("unchecked")
	public HasChildrenHieararchicalBase(final Function<T, String> itemDebug) {
	    _itemDebug = itemDebug != null ? itemDebug
	                                   : (Function<T, String>) (Function<?, String>) DEFAULT_ITEM_DEBUG;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLONE
/////////////////////////////////////////////////////////////////////////////////////////
	protected static <T extends HasChildren<T>> List<T> _recurseCloneChildren(final Collection<T> children,
																			  final FactoryFrom<T,T> cloneFactory) {
		if (CollectionUtils.isNullOrEmpty(children)) return null;
		return children.stream()
					   .map(child -> cloneFactory.from(child))
					   .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FIND / GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds the descendants (children and children of children) matching the given predicate 
	 * Note that inside an taxonomy, many items could match so a {@link Collection} is returned
	 * @param pred the predicate to match						
	 * @return the collection of items
	 */
	public Collection<T> findItemsMatching(final Predicate<T> pred) {
		if (!this.hasChildren()) return null;
		Collection<T> outItems = new LinkedList<>();
		for (T item : this.getChildren()) {
			_recurseAccumMatching(outItems,
								  item,
								  pred);
		}
		return outItems;
	}
	/**
	 * Finds the descendants (children and children of children) matching the given predicate 
	 * Note that inside an taxonomy, many items could match so a {@link Collection} is returned
	 * @param childs the children
	 * @param pred the predicate to match						
	 * @return the collection of items
	 */
	public static <T extends HasChildren<T>> Collection<T> findItemsMatching(final Collection<T> childs,
												       						 final Predicate<T> pred) {
		if (CollectionUtils.isNullOrEmpty(childs)) return null;
		
		Collection<T> outChildren = new LinkedList<>();
		for (T item : childs) {
			_recurseAccumMatching(outChildren,
								  item,
								  pred);
		}
		return outChildren;
	}
	/**
	 * Recurse accumulate childs matching the given predicate
	 * @param accum
	 * @param child
	 * @param pred
	 */
	private static <T extends HasChildren<T>> void _recurseAccumMatching(final Collection<T> accum,
											    					     final T child,
											    					     final Predicate<T> pred) {
		if (pred.test(child)) accum.add(child);
		if (!child.hasChildren()) return;
		for (T childItem : child.getChildren()) {
			_recurseAccumMatching(accum,
							      childItem,
								  pred);
		}
	}
	/**
	 * Finds a label that mached the predicate within the provided label collection and it's hierarchy
	 * @param labels label collection where the label is searched
	 * @param pred the predicate
	 * @return the label if it exists in the taxonomy, null otherwise
	 */
	public static <T extends HasChildren<T>> T recurseFindFirstItemMatching(final Collection<T> childs,
															   			  	final Predicate<T> pred) {
		if (CollectionUtils.isNullOrEmpty(childs)) return null;
		T outItem = null;
		for (T item : childs) {
			if (pred.test(item)) {
				outItem = item;
			} else if (item.hasChildren()) {
				outItem = HasChildrenHieararchicalBase.recurseFindFirstItemMatching(item.getChildren(),
																   		 			pred);
			}
			if (outItem != null) break;
		}
		return outItem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<T> getUnderlyingHierachyItems() {
		if (!this.hasChildren()) return null;
		Collection<T> itemAccum = new LinkedList<>();
		_recurseAccumItems(itemAccum,
						   this.getChildren());
		return itemAccum;
	}
	private static <T extends HasChildren<T>> void _recurseAccumItems(final Collection<T> itemAccum,
									 						   		  final Collection<T> items) {
		if (CollectionUtils.isNullOrEmpty(items)) return;
		
		itemAccum.addAll(items);
		for (T item : items) _recurseAccumItems(itemAccum,
										  		item.getChildren());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXECUTE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Executes a command on all items (children and children of children)
	 * @param cmd
	 */
	public void recurseExecuteOnAllItems(final CommandOn<T> cmd) {
		if (this.hasChildren()) _recurseExecuteOnItems(this.getChildren(),
												 	   cmd);
	}
	private static <T extends HasChildren<T>> void _recurseExecuteOnItems(final Collection<T> items,
										 			  					  final CommandOn<T> cmdOnItem) {
		for (T item : items) {
			if (item.hasChildren()) _recurseExecuteOnItems(item.getChildren(),
													  	   cmdOnItem);	// recurse
			cmdOnItem.executeOn(item);	// execute command on the item itself
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder outSb = new StringBuilder();
		this.debugInfo(outSb);
		return outSb;
	}
	@Override
	public void debugInfo(final StringBuilder sb) {
		_recurseDebugInfo(sb,
						  this.getChildren(),
						  1);
	}
	private void _recurseDebugInfo(final StringBuilder sb,
								   final Collection<T> childItems,
								   final int deepth) {
		if (CollectionUtils.hasData(childItems)) {
			for (T childItem : childItems) {
				for (int i=0; i<deepth; i++) sb.append("\t");
				sb.append(_itemDebug.apply(childItem)).append("\n");
				_recurseDebugInfo(sb,
								  childItem.getChildren(),
								  deepth+1);
			}
		}
	}
}
