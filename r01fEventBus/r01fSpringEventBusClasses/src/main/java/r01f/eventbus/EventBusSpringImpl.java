package r01f.eventbus;

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
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public EventBusSpringImpl(final ApplicationEventPublisher publisher) {
        _publisher = publisher;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void post(final Event event) {      
        _publisher.publishEvent(event);
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
