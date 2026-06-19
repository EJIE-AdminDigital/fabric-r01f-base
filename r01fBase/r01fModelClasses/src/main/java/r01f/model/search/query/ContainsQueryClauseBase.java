package r01f.model.search.query;

import java.lang.reflect.Array;
import java.util.Collection;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class ContainsQueryClauseBase<T>
      		  extends QueryClauseBase {

	private static final long serialVersionUID = -6482692753116017927L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="spectrum",
				   whenXml=@MarshallFieldAsXml(collectionElementName="item"))
	@Getter @Setter protected T[] _spectrum;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	ContainsQueryClauseBase(final FieldID fieldId,
						    final T[] spectrum) {
		super(fieldId);
		_spectrum = spectrum;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.CONTAINED_IN;
	}	
	@Override @SuppressWarnings("unchecked")
	public <V> V getValue() {
		return (V)_spectrum;
	}
	@Override @SuppressWarnings("unchecked")
	public <V> Class<V> getValueType() {
		return (Class<V>)_spectrum.getClass().getComponentType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public void setSpectrumFrom(final Collection<T> spectrum) {	// this is needed in order to store the value type
		if (spectrum == null) throw new IllegalArgumentException();
		_spectrum = (T[])spectrum.toArray();
	}
	@SuppressWarnings("unchecked")
	public void setSpectrumFrom(final Collection<T> spectrum,
								final Class<T> spectrumElementType) {	// this is needed in order to store the value type
		if (spectrum == null) throw new IllegalArgumentException();
		T[] array = (T[])Array.newInstance(spectrumElementType,spectrum.size());
		_spectrum = spectrum.toArray(array);
	}
	public void setSpectrumFrom(final T[] spectrum) {
		_spectrum = spectrum;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContainsQueryClauseBase)) return false;
		
		if (!super.equals(obj)) return false;	// checks field id
		
		ContainsQueryClauseBase<?> otherContained = (ContainsQueryClauseBase<?>)obj;
		return _spectrum != null ? otherContained.getSpectrum() != null ? _arrayEqs(_spectrum,otherContained.getSpectrum())
							    							 			: false
							     : true;		// both null
	}
	private static boolean _arrayEqs(final Object[] a,final Object[] b) {
		if (a.length != b.length) return false;
		boolean outEqs = true;
		for (int i=0; i < a.length; i++) {
			Object aEl = a[i];
			Object bEl = b[i];
			if (!aEl.equals(bEl)) {
				outEqs = false;
				break;
			}
		}
		return outEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getFieldId(),
								this.getSpectrum());	
	}
}
