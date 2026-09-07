package r01f.eventbus;

/**
 * ARCHITECTURAL COMPARISON: [FABRIC] EventBus Spring Implementation Strategies
 * 
 * =================================================================================================
 * 1. EventBusSpringImpl (Global /  Spring Approach)
 * =================================================================================================
 * - Underlying Engine : Delegated directly to org.springframework.context.ApplicationEventPublisher
 * - Scope             : GLOBAL to the entire Spring ApplicationContext. Any Spring bean annotated
 *                       with @EventListener matching the event type will receive the payload.
 * - Listener Lifecycle: AUTOMATIC. Spring scans, manages, and registers @Component beans at startup.
 * - Registration      : The register(listener) method is unused/no-op because Spring manages
 *                       listeners automatically via IoC container lifecycle.
 * - Thread Execution  : SYNCHRONOUS by default. Async processing must be configured on a per-listener
 *                       basis using Spring's @Async annotation on receiver methods.
 * - Best Use Case     : Standard application events, modular monoliths, and cross-cutting domain
 *                       events where listeners are standard Spring managed beans.
 * 
 * =================================================================================================
 * 2. EventBusIsolatedImpl (Isolated / Ad-Hoc Multicaster Approach)
 * =================================================================================================
 * - Underlying Engine : Private org.springframework.context.event.SimpleApplicationEventMulticaster
 * - Scope             : ISOLATED / PRIVATE to this specific instance. Events published here NEVER
 *                       leak into the global Spring event ecosystem.
 * - Listener Lifecycle: MANUAL. Listeners can be POJOs or non-Spring managed instances.
 * - Registration      : REQUIRED via explicit calls to eventBus.register(listener) in code.
 * - Thread Execution  : CONTROLLED AT BUS LEVEL. Execution mode (sync vs async) is determined by
 *                       configuring a TaskExecutor on the internal SimpleApplicationEventMulticaster.
 * - Best Use Case     : Dedicated ad-hoc services, strict subsystem isolation, isolated pipelines,
 *                       or dynamic runtime listener registration without Spring IoC coupling.
 * =================================================================================================
 */
public interface IsSpringEventBus 
		extends EventBus {
	//

}
