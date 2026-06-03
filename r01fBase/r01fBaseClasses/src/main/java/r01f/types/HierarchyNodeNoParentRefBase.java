package r01f.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.patterns.FactoryFrom;
import r01f.types.hierarchy.HasChildren;
import r01f.types.hierarchy.IsHierarchical;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * A node in an hierarchical structure of T objects
 * Used to represent an [entity] hierarchy like:
 * <pre>
 * 		entity
 * 			+- entity 
 * 			|		+- entity 
 * 			|		+- entity 
 * 			+- entity 
 * 			|		+- entity 
 * 			+- entity 
 * </pre>
 * starting at a node and going DOWN in the hierarchy: a node's children are the node's children
 * @param <T>
 */
@Accessors(prefix="_")
public abstract class HierarchyNodeNoParentRefBase<T,SELF_TYPE extends HierarchyNodeNoParentRefBase<T,SELF_TYPE>>
  		   implements HasChildren<SELF_TYPE>,		// has an hierarchical structure
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
	@MarshallField(as="children",
				   whenXml=@MarshallFieldAsXml(collectionElementName="child"))
	protected List<SELF_TYPE> _children;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public HierarchyNodeNoParentRefBase() {
		// no args constructor
	}
	public HierarchyNodeNoParentRefBase(final T data) {
		this();
		_data = data;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SELF_TYPE addChild(final SELF_TYPE child) {
		int position = CollectionUtils.hasData(_children) ? _children.size() : 0;
		return this.insertChildAt(child,position);
	}
	@Override
	public void addChildren(final Collection<SELF_TYPE> children) {
		if (CollectionUtils.hasData(children)) {
			for (SELF_TYPE item : children) this.addChild(item);
		}
	}
	@Override
	public SELF_TYPE insertChildAt(final SELF_TYPE child,final int index) {
		if (_children == null) _children = new ArrayList<>();
		_children.add(index,child);
		return child;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void removeChild(final SELF_TYPE child) {
		if (_children != null) _children.remove(child);
	}
	@Override
	public void removeChildAt(final int index) {
		if (_children != null) _children.remove(index);
	}
	@Override
	public void removeAllChilds() {
		if (_children != null) _children.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasChildren() {
		return CollectionUtils.hasData(_children);
	}
	@Override
	public SELF_TYPE getChildAt(final int index) {
		return _children != null ? _children.get(index)
								 : null;
	}
	@Override
	public int getChildCount() {
		return _children != null ? _children.size()
								 : 0;
	}
	@Override
	public int getChildIndex(final SELF_TYPE child) {
		return _children != null ? _children.indexOf(child)
								 : -1;
	}
	@Override
	public Collection<SELF_TYPE> getChildren() {
		return _children;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean containsChildWithData(final T data) {
		return this.getDescendantWithData(data,
								   		  this.getChildren(),
								   		  false) != null;		// not recursive
	}
	public boolean containsDescendantWithData(final T data) {
		return this.getDescendantWithData(data,
								   		  this.getChildren(),
								   		  true) != null;		// recursive
	}
	public SELF_TYPE getChildWithData(final T data) {
		return this.getDescendantWithData(data,
								   		  this.getChildren(),
								   		  false);				// not recursive
	}
	public SELF_TYPE getDescendantWithData(final T data,
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
					outNode = this.getDescendantWithData(data,
										   	      		 node.getChildren(),
										   	      		 true);
					if (outNode != null) break;
				}
			}
		}
		return outNode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	protected <U,H extends IsHierarchical<H>> H _recurseTransformUsing(final Function<T,U> fun,
																	   final FactoryFrom<U,H> factory) {
		// transform the node
		U transformed = fun.apply(this.getData());
		H node = factory.from(transformed);
		
		// transform children
		if (this.hasChildren()) {
			Collection<H> child = this.getChildren()
									  .stream()
									  .map(childNode -> childNode._recurseTransformUsing(fun,
																						 factory))
									  .collect(Collectors.toList());
			node.addChildren(child);
		}
		// return
		return node;
	}
	@SuppressWarnings("unchecked")
	protected <H extends IsHierarchical<H>> H _recurseTransformUsing(final Function<SELF_TYPE,H> fun) {
		// transform the node
		H node = fun.apply((SELF_TYPE)this);
		
		// transform children
		if (this.hasChildren()) {
			Collection<H> child = this.getChildren()
									  .stream()
									  .map(childNode -> childNode._recurseTransformUsing(fun))
									  .collect(Collectors.toList());
			node.addChildren(child);
		}
		// return
		return node;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public interface HierarchyNodeDataDebug<T> {
		public CharSequence debugInfoOf(final T data);
	}
	@SuppressWarnings("unchecked")
	public CharSequence debug(final HierarchyNodeDataDebug<T> nodeDataDebug) {
		StringBuilder dbg = new StringBuilder();
		_recurseDebug(dbg,
					  Lists.newArrayList((SELF_TYPE)this),
					  nodeDataDebug,
					  0);
		return dbg;
	}
	private void _recurseDebug(final StringBuilder dbg,
							   final Collection<SELF_TYPE> nodes,
							   final HierarchyNodeDataDebug<T> nodeDataDebug,
							   final int indent) {
		if (CollectionUtils.isNullOrEmpty(nodes)) return;
		for (SELF_TYPE node : nodes) {
			// indent
			for (int i=0; i < indent; i++) dbg.append("\t");
			// node data
			dbg.append("- ").append(nodeDataDebug.debugInfoOf(node.getData())).append("\n");
			// recurse
			_recurseDebug(dbg,
						  node.getChildren(),
						  nodeDataDebug,
						  indent + 1);
		}
	}
}
