package r01f.types;

import lombok.experimental.Accessors;
import r01f.patterns.FactoryFrom;

/**
 * A node in an hierarchical structure of T objects
 * @param <T>
 */
@Accessors(prefix="_")
public class InvertedHierarchyNode<T>
     extends InvertedHierarchyNodeBase<T,InvertedHierarchyNode<T>> {

	private static final long serialVersionUID = 7151854368273327003L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public InvertedHierarchyNode() {
		// no args constructor
	}
	public InvertedHierarchyNode(final T data) {
		super(data);
	}
	public static <T> InvertedHierarchyNode<T> create(final T data) {
		return new InvertedHierarchyNode<>(data);
	}
	public static <T> InvertedHierarchyNode<T> clone(final InvertedHierarchyNode<T> other,
										 	 		 final FactoryFrom<T,T> dataCloner) {
		return _recurseClone(other,
							 dataCloner);
	}
	private static <T> InvertedHierarchyNode<T> _recurseClone(final InvertedHierarchyNode<T> other,
							   			   			  		  final FactoryFrom<T,T> dataCloner) {
		InvertedHierarchyNode<T> cloned = new InvertedHierarchyNode<>(dataCloner.from(other.getData()));
		if (other.hasDirectAncestors()) {
			for (InvertedHierarchyNode<T> ancestor : other.getDirectAncestors()) {
				InvertedHierarchyNode<T> ancestorCloned = _recurseClone(ancestor,
															 		 	dataCloner);
				cloned.addDirectAncestor(ancestorCloned);
			}
		}
		return cloned;
	}
}
