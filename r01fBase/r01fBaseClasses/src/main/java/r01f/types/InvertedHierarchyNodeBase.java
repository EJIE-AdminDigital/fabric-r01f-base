package r01f.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.hierarchy.HasMultipleParents;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * A node in an hierarchical structure of T objects
 * Used to represent an [object]'s hierarchy like:
 * <pre>
 * 		entity
 * 			+- entity 
 * 			|		+- entity 
 * 			|		+- entity 
 * 			+- entity 
 * 			|		+- entity 
 * 			+- entity 
 * </pre>
 * BUT starting at a node and going UP in the hierarchy: a node's children are the node's PARENTs
 * @param <T>
 */
@Accessors(prefix="_")
public abstract class InvertedHierarchyNodeBase<T,SELF_TYPE extends InvertedHierarchyNodeBase<T,SELF_TYPE>>
  		   implements HasMultipleParents<SELF_TYPE>,		// has an hierarchical structure
  			 		  Serializable {

	private static final long serialVersionUID = 6955527630723451277L;
/////////////////////////////////////////////////////////////////////////////////////////
//  DATA ASSOCIATED WITH THE NODE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="data")
	@Getter @Setter protected T _data;

/////////////////////////////////////////////////////////////////////////////////////////
//  HIERARCHY
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="parents",
				   whenXml=@MarshallFieldAsXml(collectionElementName="parent"))
	protected List<SELF_TYPE> _parents;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public InvertedHierarchyNodeBase() {
		// no args constructor
	}
	public InvertedHierarchyNodeBase(final T data) {
		this();
		_data = data;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SELF_TYPE addDirectAncestor(final SELF_TYPE child) {
		int position = CollectionUtils.hasData(_parents) ? _parents.size() : 0;
		return this.insertDirectAncestorAt(child,position);
	}
	@Override
	public void addDirectAncestors(final Collection<SELF_TYPE> children) {
		if (CollectionUtils.hasData(children)) {
			for (SELF_TYPE item : children) this.addDirectAncestor(item);
		}
	}
	@Override
	public SELF_TYPE insertDirectAncestorAt(final SELF_TYPE child,final int index) {
		if (_parents == null) _parents = new ArrayList<>();
		_parents.add(index,child);
		return child;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void removeDirectAncestor(final SELF_TYPE child) {
		if (_parents != null) _parents.remove(child);
	}
	@Override
	public void removeDirectAncestorAt(final int index) {
		if (_parents != null) _parents.remove(index);
	}
	@Override
	public void removeAllDirectAncestors() {
		if (_parents != null) _parents.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET / HAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasDirectAncestors() {
		return CollectionUtils.hasData(_parents);
	}
	@Override
	public SELF_TYPE getDirectAncestorAt(final int index) {
		return _parents != null ? _parents.get(index)
								 : null;
	}
	@Override
	public int getDirectAncestorCount() {
		return _parents != null ? _parents.size()
								 : 0;
	}
	@Override
	public int getDirectAncestorIndex(final SELF_TYPE child) {
		return _parents != null ? _parents.indexOf(child)
								: -1;
	}
	@Override
	public Collection<SELF_TYPE> getDirectAncestors() {
		return _parents;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean containsDirectAncestorWithData(final T data) {
		return this.getAncestorWithData(data,
								   		this.getDirectAncestors(),
								   		false) != null;		// not recursive
	}
	public boolean containsAncestorWithData(final T data) {
		return this.getAncestorWithData(data,
								   		this.getDirectAncestors(),
								   		true) != null;		// recursive
	}
	public SELF_TYPE getDirectAncestorWithData(final T data) {
		return this.getAncestorWithData(data,
								   		this.getDirectAncestors(),
								   		false);				// not recursive
	}
	public SELF_TYPE getAncestorWithData(final T data,
									     final Collection<SELF_TYPE> nodes,
									     final boolean recurse) {
		SELF_TYPE outNode = null;
		if (CollectionUtils.hasData(nodes)) {
			for (SELF_TYPE node : nodes) {
				if (node.getData().equals(data)) {
					outNode = node;
					break;
				}
				if (recurse) {
					outNode = this.getAncestorWithData(data,
										   	      	   node.getDirectAncestors(),
										   	      	   true);
					if (outNode != null) break;
				}
			}
		}
		return outNode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FIND
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Find the root nodes 
	 * Beware that a certain node can belong to multiple hierarchies
	 * ie:
	 * 		+-node1              +-node4                   
	 * 			+-node2              |
	 * 				+-node3 ---------+
	 * @param pred
	 * @return
	 */
	public Collection<SELF_TYPE> findRootNodes() {
		return this.findRootNodesMatching(n -> true);	// any root node
	}
	/**
	 * Find the root nodes matching the given condition
	 * Beware that a certain node can belong to multiple hierarchies
	 * ie:
	 * 		+-node1              +-node4
	 * 			+-node2              |
	 * 				+-node3 ---------+
	 * @param pred
	 * @return
	 */
	public Collection<SELF_TYPE> findRootNodesMatching(final Predicate<SELF_TYPE> pred) {
		Predicate<SELF_TYPE> thePred = pred != null ? pred : n -> true;	// always true
		return this.findAncestorNodesMatching(thePred.and(node -> !node.hasDirectAncestors()));	// root = node without ancestors
	}
	/**
	 * Find the ancestor nodes matching the given condition
	 * Beware that a certain node can belong to multiple hierarchies
	 * ie:
	 * 		+-node1              +-node4
	 * 			+-node2              |
	 * 				+-node3 ---------+
	 * @param pred
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<SELF_TYPE> findAncestorNodesMatching(final Predicate<SELF_TYPE> pred) {
		Collection<SELF_TYPE> outRootNodes = Lists.newArrayList();
		_recurseFindAncestorNodeMatching((SELF_TYPE)this,
									 	 pred,
									 	 outRootNodes);
		return outRootNodes;
	}
	private void _recurseFindAncestorNodeMatching(final SELF_TYPE node,
									   		  	  final Predicate<SELF_TYPE> pred,
									   		  	  final Collection<SELF_TYPE> accum) {
		if (!node.hasDirectAncestors()) return;
		node.getDirectAncestors()
			.forEach(ancestorNode -> {
							if (pred.test(ancestorNode)) accum.add(ancestorNode);
							_recurseFindAncestorNodeMatching(ancestorNode,
													  		 pred,
													  		 accum);	// beware! recursion
					 });
	}
	/**
	 * Returns the multiple posible paths from a node to the root nodes in the hierarchy
	 * Beware that a certain node can belong to multiple hierarchies
	 * ie:
	 * 		[A]          [G]          [F]
	 * 		 |           | |           |
	 *       +----[B]----+ +----[E]----+
	 *             |             |
	 *             +-----[C]-----+
	 *                    |
	 *                   [D]
	 * ... so [D] has many possible paths to root:
	 * 		[D] > [C] > [B] > [A]
	 * 		[D] > [C] > [B] > [G]
	 * 		[D] > [C] > [E] > [G]
	 * 		[D] > [C] > [E] > [F]
	 * In order to compute these paths a path accumulator is used in a recursive way
	 * 	                                        [D] > [C] > [B] > [A]
	 *                      [D] > [C] > [B] -->                      
	 *                                          [D] > [C] > [B] > [G]
	 * [D] --> [D] > [C] -->
	 *                                          [D] > [C] > [E] > [G]
	 *                       [D] > [C] > [E] --> 
	 *                                          [D] > [C] > [E] > [F]
	 * in every iteration, the path is CLONED for every node's ancestor which is then added 
	 * to the clone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<List<SELF_TYPE>> getPathsToRoots() {
		//  paths accumulator
		Collection<List<SELF_TYPE>> pathsAccum = Lists.newArrayList(); 

		// current path accumulator
		List<SELF_TYPE> pathAccum = new LinkedList<>();
		pathAccum.add((SELF_TYPE)this);
		
		// recurse!!!
		_recurseCollectPathToRoot((SELF_TYPE)this,
								  pathAccum,
								  pathsAccum);
		// return
		return pathsAccum;
	}
	private void _recurseCollectPathToRoot(final SELF_TYPE node,
									   	   final List<SELF_TYPE> currPathAccum,
									   	   final Collection<List<SELF_TYPE>> pathsAccum) {
		if (!node.hasDirectAncestors()) {
			pathsAccum.add(currPathAccum);	// reached the root > add to the path collection
			return;
		}
		
		// if the node has more than a single direct ancestor, CLONE the pathAccum
		// as many times as the ancestor nodes
		node.getDirectAncestors()
			.stream()
			.forEach(ancestorNode -> {
							// accum the current path
							List<SELF_TYPE> pathAccum = Lists.newArrayList(currPathAccum);	// clone this node's path
							pathAccum.add(ancestorNode);
							// recurse!!!!!
							_recurseCollectPathToRoot(ancestorNode,
													  pathAccum,
													  pathsAccum);
					 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public interface InvertedHierarchyNodeDataDebug<T> {
		public CharSequence debugInfoOf(final T data);
	}
	@SuppressWarnings("unchecked")
	public CharSequence debug(final InvertedHierarchyNodeDataDebug<T> nodeDataDebug) {
		StringBuilder dbg = new StringBuilder();
		_recurseDebug(dbg,
					  Lists.newArrayList((SELF_TYPE)this),
					  nodeDataDebug,
					  0);
		return dbg;
	}
	private void _recurseDebug(final StringBuilder dbg,
							   final Collection<SELF_TYPE> nodes,
							   final InvertedHierarchyNodeDataDebug<T> nodeDataDebug,
							   final int indent) {
		if (CollectionUtils.isNullOrEmpty(nodes)) return;
		for (SELF_TYPE node : nodes) {
			// indent
			for (int i=0; i < indent; i++) dbg.append("\t");
			// node data
			dbg.append("- ").append(nodeDataDebug.debugInfoOf(node.getData())).append("\n");
			// recurse
			_recurseDebug(dbg,
						  node.getDirectAncestors(),
						  nodeDataDebug,
						  indent + 1);
		}
	}
}
