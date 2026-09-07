package r01f.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.annotation.AnnotatedElementUtils;

import lombok.extern.slf4j.Slf4j;
import r01f.eventbus.annotations.EventListenerSubscriber;

/**
 *   Sample [ Bootstrap]
 *   ------------------------------------------------: 
	    @Bean
	    @EventBusFor(MY_SERVICE_FQN)
	    public EventBus myServiceAsyncEventBus() {
	    
	        // 1. ThreadPoolTaskExecutor
	        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	        executor.setCorePoolSize(2);
	        executor.setMaxPoolSize(5);
	        executor.setQueueCapacity(100);
	        executor.setThreadNamePrefix("Bus-MyService-");
	        executor.initialize();
	
	        // 2. Asign SimpleApplicationEventMulticaster [ Without setTaskExecutor , the e multicaster will be sync
	        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
	        multicaster.setTaskExecutor(executor);
	        	
	        // 3.Return
	        return new EventBusIsolatedAsyncImpl(multicaster);
	    }
	 if the subscriber listner it not  for crud services, must call the register  [ if is crud nothing to do with fabric ]
	 		this._busInstance.register(listener);
	 
 */
@Slf4j
public class EventBusSpringIsolatedImpl 
  implements EventBus, 
  			 IsSpringEventBus  {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final SimpleApplicationEventMulticaster _eventMulticaster;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public EventBusSpringIsolatedImpl(final SimpleApplicationEventMulticaster eventMulticaster) {
    	_eventMulticaster = eventMulticaster;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void post(final Event event) {      
    	_eventMulticaster.multicastEvent(new PayloadApplicationEvent<>(this, event));
    }
    @Override
    public void register(final EventBusListener listener) {    	
    	final List<Method> subscriberMethods = new ArrayList<>();
        for (Method method : listener.getClass().getDeclaredMethods()) {          
            if (method.getParameterCount() == 1 
                    && AnnotatedElementUtils.hasAnnotation(method, EventListenerSubscriber.class)) {                
                method.setAccessible(true); 
                subscriberMethods.add(method);
            }
        }

        if (subscriberMethods.isEmpty()) {
            log.warn(" listener {} has no method annoted with @EventListenerSubscriber.", 
                     listener.getClass().getSimpleName());
            return;
        }
        // 2. Register  ApplicationListener into  multicaster 
        _eventMulticaster.addApplicationListener(applicationEvent -> {
		            if (applicationEvent instanceof PayloadApplicationEvent<?> payloadEvent) {
		                Object payload = payloadEvent.getPayload();
		
		                if (payload instanceof Event customEvent) {
		                    // [Invoke]
		                    for (Method method : subscriberMethods) {
		                        Class<?> targetEventType = method.getParameterTypes()[0];
		                        if (targetEventType.isAssignableFrom(customEvent.getClass())) {
		                            try {
		                                method.invoke(listener,
		                                			  customEvent);
		                            } catch (final Throwable  e) {
		                                log.error("error listener {}#{}", 
			                                          customEvent.getClass().getSimpleName(), 
			                                          listener.getClass().getSimpleName(), 
			                                          method.getName(), e);
		                            }
		                        }
		                    }
		                }
		            }
		        });
        log.info("Listener {} registrado con éxito en el EventBus privado.", 
                 listener.getClass().getSimpleName());
    }

}
