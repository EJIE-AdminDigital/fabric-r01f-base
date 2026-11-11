package r01f.eventbus;

import java.util.concurrent.Executor;

import org.springframework.context.ApplicationEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventBusSpringImpl 
  implements EventBus,
  			 IsSpringEventBus {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final ApplicationEventPublisher _publisher;
    private final Executor _executor; //optional for async model.
    private final boolean _isAsync;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public EventBusSpringImpl(final ApplicationEventPublisher publisher) {
        this(publisher,null);
    }
    /**
     * Constructor .
     * With Executor (ThreadPool) willbea async.
     */
    public EventBusSpringImpl(final ApplicationEventPublisher publisher, final Executor executor) {
        _publisher = publisher;
        _executor = executor;
        _isAsync = (executor != null);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void post(final Event event) {      
        if (_isAsync) {  
            _executor.execute(() -> {
			                log.trace("Pubish event {} async thead {}", 
			                        event.getClass().getSimpleName(), Thread.currentThread().getName());
			               _publisher.publishEvent(event);
            });
        } else {
            // sync (main / http execution thread)
            _publisher.publishEvent(event);
        }
    }
    @Override
    public void register(final EventBusListener listener) {
        // In Spring, listeners are registered automatically 
    	// when they are annotated with @Component and their methods with @EventListener.
    	// An explicit 'register' action is not required at runtime 
    	// if they are Spring beans, but it is kept to comply with the interface.
        log.warn("SpringEventBus: Listener {} registred  by Spring.",
        		  listener.getClass().getSimpleName());
    }

}
