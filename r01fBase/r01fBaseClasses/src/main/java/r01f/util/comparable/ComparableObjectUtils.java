package r01f.util.comparable;

import java.util.Collection;
import java.util.Map;

import lombok.NonNull;
import r01f.comparable.ComparableObject;

public abstract class ComparableObjectUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends Object> boolean compare(final O o1,final O o2) {
		return o1 == null ? o2 == null                      // True, if both are null
						  : o2 == null ? false              // o1 is not null, but o2 is null (they are different)
									   : _compare(o1,o2);   // Compare, because both are not null
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <O extends Object> boolean _compare(@NonNull final O o1,@NonNull final O o2) {
		if (ComparableObject.class.isAssignableFrom(o1.getClass())) {
			return ((ComparableObject)o1).isSameObject((ComparableObject)o2);
		} else {
			return o1.equals(o2);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LIST
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends Object> boolean compareCollection(final Collection<O> o1,final Collection<O> o2) {
		return o1 == null ? o2 == null
						  : (o2 != null 
						  	 && 
						  	 o1.size() == o2.size() 
							 &&
							 o1.stream()
				  			   .allMatch(o1obj -> {
											return o2.stream()
								 					  .anyMatch(o2obj -> ComparableObjectUtils.compare(o1obj,o2obj));
										 })
							 );
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	MAP
/////////////////////////////////////////////////////////////////////////////////////////
	public static <OK extends Object,OV extends Object> boolean compareMap(final Map<OK,OV> o1,final Map<OK,OV> o2) {
		return o1 == null ? o2 == null
						  : (o2 != null
							 &&
							 o1.size() == o2.size()
							 &&
							 o1.entrySet().stream()
							   .allMatch(e -> ComparableObjectUtils.compare(e.getValue(),o2.get(e.getKey()))));
	}
}
