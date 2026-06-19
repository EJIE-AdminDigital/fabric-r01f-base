package r01f.rest;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import lombok.extern.slf4j.Slf4j;



/**
 * Event listeners OPTIONALLY allow users to receive and process a predefined set of events that occur during an application lifecycle (such as application initialization, application destroy)
 * as well as request processing lifecycle events (request started, resource method finished, exception thrown, etc.).
 * This feature is always enabled in Jersey server runtime and is leveraged by the other monitoring features.
 *
 * See : https://eclipse-ee4j.github.io/jersey.github.io/documentation/3.0.0/user-guide.html#d0e16262
 *
 * [] Warning : Don't be confused with WebListeners !!!! : Servletlistener/WebListener  and Jersey are not linked to the same system. As an alternative to WebListeners, you can use Jersey's Event Listeners.
 *           You can implement an ApplicationEventListener where you would be able to initialization and clean up in the same way you would in the servlet listener. You would be able to inject your services into Jersey's
 *
 * [] In order to register the ApplicationListener do it at the final RESTResourceConfigBase impl class with the  :
 *
 *		  	public Set<Class<? extends ApplicationEventListener >> getApplicationListeners() {
				Set<Class<? extends ApplicationEventListener>> outMappers = super.getApplicationListeners();
				outMappers.add(MyApplicationListener.class);// ...class extendingRESTApplicationListenerBase
				return outMappers;
			}
 *
 * </pre>
 */
@Slf4j
public abstract class RESTApplicationListenerBase
  implements ApplicationEventListener {
/////////////////////////////////////////////////////////////////////////////////////////
//  ON EVENT IMPL
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onEvent(final ApplicationEvent event) {

			switch (event.getType()) {

		          case INITIALIZATION_FINISHED:
		        	      log.warn("###################################################################################");
			              log.warn("Application  was initialized.");
			              log.warn("###################################################################################");
			              _onInitializationFinished(event);
			              break;
			       case DESTROY_FINISHED:
			    	     log.warn("###################################################################################");
			    	     log.warn("Application was destoyed.");
			    	     log.warn("###################################################################################");
			    	     _onDestroyFinished(event);
			             break;
					case INITIALIZATION_APP_FINISHED: // Initialization of jersey application is finished
						                             // ....BUT the server might not be started and ready yet to serve requests (this will beindicated by the INITIALIZATION_FINISHED event).
						_onInitializationAppFinished(event);
						break;
					case INITIALIZATION_START:
						_onInitializationStart(event);
						break;
					case RELOAD_FINISHED:
						_onReloadFinished(event);
						break;
					default:
						break;
			 }

	}

	@Override
	public RequestEventListener onRequest(final RequestEvent requestEvent) {
		return null; // do nothing by default.
	}
/////////////////////////////////////////////////////////////////////////////////////////
// ON APPLICATION EVENT ACTIONS  (...override if needed )
/////////////////////////////////////////////////////////////////////////////////////////
	protected void _onInitializationFinished(final ApplicationEvent event) {
		// override if needed
    }

	protected void _onInitializationAppFinished(final ApplicationEvent event) {
		// override if needed
    }

	protected void _onInitializationStart(final ApplicationEvent event) {
		// override if needed
    }

	protected void _onReloadFinished(final ApplicationEvent event) {
		// override if needed
    }
	protected void _onDestroyFinished(final ApplicationEvent event) {
		// override if needed
    }
}
