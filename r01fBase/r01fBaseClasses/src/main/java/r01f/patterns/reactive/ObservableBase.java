package r01f.patterns.reactive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A base implementation for the {@link Observable} interface
 */
public abstract class ObservableBase 
	       implements Observable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final List<Observer> _observers = new ArrayList<>();

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <O extends Observer> void addObserver(final O observer) {
		if (observer == null) throw new IllegalArgumentException();
		if (_observers.contains(observer)) return;
		_observers.add(observer);
	}
	public boolean hasObservers() {
		return _observers != null && _observers.size() > 0;
	}
	public boolean hasObserver(final Class<? extends Observer> observerType) {
		boolean outContainsObserver = false;
		if (_observers != null && _observers.size() > 0) {
			for (Observer obs : _observers) {
				if (obs.getClass() == observerType) {
					outContainsObserver = true;
					break;
				}
			}
		}
		return outContainsObserver;
	}
	public Collection<Observer> allObservers() {
		return _observers;
	}
	/**
	 * Returns observers of the provided type
	 * The default impl for this method is _filterObserversOfType() BUT it's not used here 
	 * because GWT does NOT supports isAssignableFrom
	 * @param observerType
	 * @return
	 */
	public abstract <O extends Observer> Collection<Observer> observersOfType(final Class<O> observerType);
	
	
	
	protected static <O extends Observer> Collection<Observer> _filterObserversOfType(final Collection<Observer> observers,
																					  final Class<O> observerType) {
		Collection<Observer> outObservers = null;
		if (observers != null && observers.size() > 0) {
			outObservers = observers.stream()
									.filter(obs -> obs.getClass().isAssignableFrom(observerType))
									.toList();
		}
		return outObservers;
		
	}
}
