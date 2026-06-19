package r01f.model.persistence;

import java.util.Collection;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="findResult",typeId="ok")
@Accessors(prefix="_")
public class FindOK<T>
	 extends PersistenceOperationExecOK<Collection<T>>
  implements FindResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The found object type
	 * (beware that {@link PersistenceOperationOnObjectOK} wraps a {@link Collection} 
	 *  of this objects)
	 */
	@MarshallField(as="foundObjType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _foundObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOK() {
		super(PersistenceRequestedOperation.FIND,PersistencePerformedOperation.FOUND);
	}
	public FindOK(final Class<T> entityType) {
		this();
		_foundObjectType = entityType;
	}
	public FindOK(final Class<T> entityType,
				  final Collection<T> results) {
		this();
		_foundObjectType = entityType;
		_methodExecResult = results;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public Stream<T> stream() {
		Collection<T> col = this.getMethodExecResult();
		return col != null ? col.stream()
						   : Stream.empty();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} persistence operation requested on entity of type {} and found {} results",
								  _calledMethod,_foundObjectType,CollectionUtils.safeSize(_methodExecResult));
	}
}
