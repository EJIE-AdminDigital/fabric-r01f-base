package r01f.model.facets.view;

import r01f.patterns.reactive.Observer;

public interface LazyLoadedViewObserver 
         extends Observer {
	
	public void onLazyLoadedDataNeeded();
}
