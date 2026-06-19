package r01f.rest;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;

import com.google.common.collect.Sets;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import lombok.extern.slf4j.Slf4j;
import r01f.util.types.collections.CollectionUtils;


/**
 * Rest Resource referenced at {@link org.glassfish.jersey.server.ResourceConfig} (Jersey   is in use)
 *  see {@link https://stackoverflow.com/questions/45625925/what-exactly-is-the-resourceconfig-class-in-jersey-2/45627178#45627178 }
 * in order to load the REST resources and the request / response received / sent objects mappers / exception mappers
 * See also {@link RESTResourceConfigBootstrapBase } with DI Boostraping logic.
 *
 * <pre>
 *   NOTE: REST Resource should be defined in WEB-INF/web.xml
 * </pre>
 */
@Slf4j
public abstract class RESTResourceConfigBase
			  extends ResourceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTResourceConfigBase() {
		// rest Resources
		Set<Class<? extends RESTResource>> restResourceTypes = this.getRESTResourceTypes();
		if (CollectionUtils.hasData(restResourceTypes)) {
			restResourceTypes.stream()
							 .forEach(r -> {
									 		log.warn("Registering [ Jersey 3 Resource  {} ]", r.getCanonicalName());
											this.register(r);
									 });
		}
		// Request received objects mappers: transforms Java->XML for REST methods body types
		Set<Class<? extends MessageBodyReader<?>>> reqReceivedTypesMappers = this.getRequestReceivedBodyTypesMappers();
		if (CollectionUtils.hasData(reqReceivedTypesMappers)) {
			  reqReceivedTypesMappers.stream()
									 .forEach(r -> {
										 		log.warn("Registering [ Jersey 3 Request Type ] {} ]", r.getCanonicalName());
										 		this.register(r);
										  	 });
		}


		// Request received objects mappers: transforms Java->XML for REST methods path param types
		Set<Class<? extends ParamConverterProvider>> reqReceivedQueryMappers = this.getRequestReceivedQueryPathParamsTypesMappers();
		if (CollectionUtils.hasData(reqReceivedQueryMappers)) {
			reqReceivedQueryMappers.stream()
									 .forEach(r -> {
										 		log.warn("Registering [ Jersey 3 Request Query Param Converter Path  Type ] {} ]", r.getCanonicalName());
										 		this.register(r);
										  	 });
		}


		// Response sent objects mappers: transforms Java->XML,JSON, etx... for REST methods return types
		Set<Class<? extends MessageBodyWriter<?>>> respSentTypesMappers = this.getResponseSentBodyTypesMappers();
		if (CollectionUtils.hasData(respSentTypesMappers)) {
			 respSentTypesMappers.stream()
								 .forEach(r -> {
									 			log.warn("Registering [  Jersey 3 Response Type  {} ]", r.getCanonicalName());
												this.register(r);
										  });
		}
		// Exception Mappers
		Set<Class<? extends ExceptionMapper<?>>> expsMappers = this.getExceptionsMappers();
		if (CollectionUtils.hasData(expsMappers)) {
			   expsMappers.stream()
			   				.forEach(r -> {
		   								log.warn("Registering [ Jersey 3  Exception Mapper Type  {} ]", r.getCanonicalName());
										this.register(r);
									});
		}

		// Filters
		Set<Class<? extends RESTFilter>> filters = this.getFilterTypes();
		if (CollectionUtils.hasData(filters)) {
				filters.stream()
			   				.forEach(r -> {
			   							log.warn("Registering [ Jersey 3  Filter  {} ]", r.getCanonicalName());
										this.register(r);
									});
		}

		// Application Listeners
		Set<Class<? extends ApplicationEventListener>> applicationListners = this.getApplicationListeners();
		if (CollectionUtils.hasData(applicationListners)) {
			applicationListners.stream()
			   						.forEach(r -> {
			   							log.warn("Registering [ Jersey 3  Application Listener  {} ]", r.getCanonicalName());
										this.register(r);
									});
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the {@link RESTResource} implementing types
	 */
	public abstract Set<Class<? extends RESTResource>> getRESTResourceTypes();

	/**
	 * @return the {@link MessageBodyReader} types that maps request received objects
	 */
	public Set<Class<? extends MessageBodyReader<?>>> getRequestReceivedBodyTypesMappers() {
		Set<Class<? extends MessageBodyReader<?>>> outMappers = Sets.newHashSet();
		return outMappers;
	}

	/**
	 * @return the {@link ParamConverterProvider} types that maps request (query params) received objects
	 */
	public Set<Class<? extends ParamConverterProvider>> getRequestReceivedQueryPathParamsTypesMappers() {
		Set<Class<? extends ParamConverterProvider>> outMappers = Sets.newHashSet();
		return outMappers;
	}
	/**
	 * @return the {@link MessageBodyWriter} types that maps response sent objects
	 */
	public Set<Class<? extends MessageBodyWriter<?>>> getResponseSentBodyTypesMappers() {
		Set<Class<? extends MessageBodyWriter<?>>> outMappers = Sets.newHashSet();
		return outMappers;
	}
	/**
	 * @return the {@link ExceptionMapper} types that maps exceptions
	 */
	public Set<Class<? extends ExceptionMapper<?>>> getExceptionsMappers() {
		Set<Class<? extends ExceptionMapper<?>>> outMappers = Sets.newHashSet();
		return outMappers;
	}
    /**
	 * @return the {@link RESTFilter} types that maps exceptions
	 */
	public Set<Class<? extends RESTFilter >> getFilterTypes() {
		Set<Class<? extends RESTFilter>> outMappers = Sets.newHashSet();
		return outMappers;
	}
	 /**
	 * @return the {@link ApplicationEventListener} types that maps ApplicationListeners
	 */
	public Set<Class<? extends ApplicationEventListener >> getApplicationListeners() {
		Set<Class<? extends ApplicationEventListener>> outMappers = Sets.newHashSet();
		return outMappers;
	}
}
