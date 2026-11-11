package r01f.objectstreamer;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.internal.R01F;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.MarshallerModuleBase.JacksonSerializerAndDeserializerPair;
import r01f.oids.CommonOIDs.IsAppCode;
import r01f.patterns.IsBuilder;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;
import r01f.util.types.datetime.DateTimeFormatUtils;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.xml.XmlFactory;
import tools.jackson.dataformat.xml.XmlMapper;

/**
 * Builds a {@link Marshaller}
 * <pre class="brush:java">
 * 		Marshaller marshaller = MarshallerBuilder.findTypesToMarshallAt(AppCode.forId("xxx"))
 * 												 .build();
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MarshallerBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	// BEWARE: JSON specification states, that only valid encodings are UTF-8, UTF-16 and UTF-32.
	//		   No other encodings (like Latin-1) can be used
	private static final Charset DEFAULT_MARSHALLER_CHARSET = Charset.forName(R01F.ENCODING_UTF_8);	// Charset.defaultCharset();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static MarshallerBuilderModulesStep findTypesToMarshallAt(final IsAppCode... appCodes) {
		return MarshallerBuilder.findTypesToMarshallAtJavaPackages(appCodes != null ? Stream.of(appCodes)
																							.map(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
																							.collect(Collectors.toSet())
																				    : Sets.<JavaPackage>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAt(final Collection<IsAppCode> appCodes) {
		return MarshallerBuilder.findTypesToMarshallAtJavaPackages(appCodes != null ? appCodes.stream()
																							  .map(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
																							  .collect(Collectors.toSet())
																				    : Sets.<JavaPackage>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAtJavaPackages(final JavaPackage... javaPackages) {
		return new MarshallerBuilderModulesStep(CollectionUtils.hasData(javaPackages) ? Sets.<JavaPackage>newLinkedHashSet(Lists.newArrayList(javaPackages))
																					  : Sets.<JavaPackage>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAtJavaPackages(final Collection<JavaPackage> javaPackages) {
		return new MarshallerBuilderModulesStep(Sets.newLinkedHashSet(javaPackages));
	}
	public static MarshallerImpl build() {
		return MarshallerBuilder.findTypesToMarshallAtJavaPackages(Sets.<JavaPackage>newLinkedHashSet())
								.noModules()
								.build();
	}
	public static MarshallerImpl build(final Charset defaultCharset) {
		return MarshallerBuilder.findTypesToMarshallAtJavaPackages(Sets.<JavaPackage>newLinkedHashSet())
								.noModules()
								.build(defaultCharset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public final record MarshallerBuilderModulesStep(Set<JavaPackage> javaPackages) {
		public MarshallerBuilderBuildStep registerJacksonModules(final JacksonModule... modules) {
			return MarshallerBuilderBuildStep.from(javaPackages,
												   null,
												   CollectionUtils.hasData(modules) ? Sets.newLinkedHashSet(Lists.newArrayList(modules))
														  						    : Sets.<SimpleModule>newLinkedHashSet());
		}
		public MarshallerBuilderBuildStep registerMarshallerModules(final MarshallerModule... marshallerModules) {
			return MarshallerBuilderBuildStep.from(javaPackages,
												   CollectionUtils.hasData(marshallerModules) ? Sets.newLinkedHashSet(Lists.newArrayList(marshallerModules))
														  									  : Sets.<MarshallerModule>newLinkedHashSet(),
												   null);
		}
		public MarshallerBuilderBuildStep registerMarshallerModules(final Set<? extends MarshallerModule> marshallerModules) {
			return MarshallerBuilderBuildStep.from(javaPackages,
												   marshallerModules,
												   null);
		}
		public MarshallerBuilderBuildStep registerModules(final Set<? extends MarshallerModule> marshallerModules,
														  final Set<? extends JacksonModule> jacksonModules) {
			return MarshallerBuilderBuildStep.from(javaPackages,
												   marshallerModules,
												   jacksonModules);
		}
		public MarshallerBuilderBuildStep noModules() {
			return MarshallerBuilderBuildStep.from(javaPackages,
												   Sets.<MarshallerModule>newLinkedHashSet(),
												   Sets.<JacksonModule>newLinkedHashSet());
		}
		public MarshallerImpl build() {
			return this.noModules()
					   .build();
		}
		public MarshallerImpl build(final Charset defaultCharset) {
			return this.noModules()
					   .build(defaultCharset);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public record MarshallerBuilderBuildStep(ClassgraphScanHandler typeScanner,
											 Collection<JacksonSerializerAndDeserializerPair<?>> jacksonSerializerAndDeserializerPairs,
											 Set<? extends MarshallerModule> marshallerModules,Set<? extends JacksonModule> jacksonModules) {
		public static MarshallerBuilderBuildStep from(final Set<JavaPackage> javaPackages,
										  			  final Set<? extends MarshallerModule> marshallerModules,final Set<? extends JacksonModule> jacksonModules) {
			// BEWARE!!!
			// TypeScan is used but scanning is VERY SLOW so try to DO IT JUST ONCE!!!!
			// ... to do so, reuse the type scanner

			// [0] - Create the [type scanner]
			// ensure that the r01f is listed in the java packages that contains objects to be marshalled
			Set<JavaPackage> theJavaPackages = CollectionUtils.hasData(javaPackages) ? javaPackages
																	  	 			 : Sets.<JavaPackage>newLinkedHashSet();
			if (!theJavaPackages.contains(JavaPackage.of(R01FAppCodes.R01_APP_CODE))) theJavaPackages.add(JavaPackage.of(R01FAppCodes.R01_APP_CODE));
			ClassgraphScanHandler typeScanner = new ClassgraphScanHandler(theJavaPackages);

			// [1] - Create the jackson annotation introspector and serializer & deserializer pairs
			Collection<JacksonSerializerAndDeserializerPair<?>> jacksonSerializerAndDeserializerPairs = MarshallerModuleBase.buildJacksonSerializerAndDeSerializersUsing(typeScanner);

			// [2] - assign
			return new MarshallerBuilderBuildStep(typeScanner,
												  jacksonSerializerAndDeserializerPairs,
												  marshallerModules,jacksonModules);
		}
		////////// MARSHALLER
		public MarshallerImpl build() {
			return this.build(DEFAULT_MARSHALLER_CHARSET);
		}
		public MarshallerImpl build(final Charset defaultCharset) {
			Stopwatch sw = Stopwatch.createStarted();

			// create the mapper builders
			JsonMapper jsonMapper = this.buildJsonMapper();
			XmlMapper xmlMapper = this.buildXmlMapper();

			log.info("[marshaller]: initialized in {} milis",sw.elapsed(TimeUnit.MILLISECONDS));

			return new MarshallerImpl(jsonMapper,xmlMapper,
									  defaultCharset);
		}
		////////// JSON
		public JsonMapper.Builder buildJsonMapperBuilder(final JsonFactory jsonFactory) {	
			
			// [1] - Create the mapper builder
			JsonMapper.Builder outBuilder = JsonMapper.builder(jsonFactory)
												   	  .addModule(new MarshallerModuleForJson(this.jacksonSerializerAndDeserializerPairs,
																	  				   		 this.typeScanner));
			// [2] - add modules
			outBuilder = _mapperBuilderWithModules(outBuilder,
												   marshallerModules,jacksonModules);
			// [3] - default config
			return _mapperBuilderWithDefaultConfig(outBuilder);
		}
		public JsonMapper buildJsonMapper() {
			return this.buildJsonMapperBuilder(_buildJsonFactory())
					   .build();
		}
		////////// XML
		public XmlMapper.Builder buildXmlMapperBuilder(final XmlFactory xmlFactory) {
			// [1] - Create the mapper builder
			XmlMapper.Builder outBuilder = XmlMapper.builder(xmlFactory)
												    // Add the marshaller module
											   	   .addModule(new MarshallerModuleForXml(this.jacksonSerializerAndDeserializerPairs,
																	  				     this.typeScanner));
			// [2] - add modules
			outBuilder = _mapperBuilderWithModules(outBuilder,
												   marshallerModules,jacksonModules);
			// [3] - default config
			return _mapperBuilderWithDefaultConfig(outBuilder);
		}
		public XmlMapper.Builder buildXmlMapperBuilder() {
			return this.buildXmlMapperBuilder(_buildXmlFactory());
		}
		public XmlMapper buildXmlMapper(final XmlFactory xmlFactory) {
			return this.buildXmlMapperBuilder(xmlFactory)
					   .build();
		}
		public XmlMapper buildXmlMapper() {
			// [1] - Create the XmlFactory  (use namespaces when serializing/deserializing XML)
			XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
			xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE,true);

			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
			xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES,true);

			XmlFactory xmlFactory = XmlFactory.builder()
											  .xmlInputFactory(xmlInputFactory)
											  .xmlOutputFactory(xmlOutputFactory)
											  .build();
			// [2] - build the mapper
			return this.buildXmlMapper(xmlFactory);
		}
		
//////////////////////////////////////////////////////////////////////
///   PRIVATE METHODS
//////////////////////////////////////////////////////////////////////
		private static JsonFactory _buildJsonFactory() {
			StreamWriteConstraints writeConstraints = StreamWriteConstraints.builder()
																	        .maxNestingDepth(Integer.MAX_VALUE)
																	        .build();

			StreamReadConstraints readConstraints = StreamReadConstraints.builder()
																        .maxNestingDepth(Integer.MAX_VALUE)
																        .build();
			JsonFactory factory = JsonFactory.builder()
											.streamWriteConstraints(writeConstraints)
											.streamReadConstraints(readConstraints)
										.build();
			return factory;
		}		
		
		
		private static XmlFactory _buildXmlFactory() {
			StreamWriteConstraints writeConstraints = StreamWriteConstraints.builder()
																	        .maxNestingDepth(Integer.MAX_VALUE)
																	        .build();

			StreamReadConstraints readConstraints = StreamReadConstraints.builder()
																	        .maxNestingDepth(Integer.MAX_VALUE)
																	        .build();
			XmlFactory factory = XmlFactory.builder()
											.streamWriteConstraints(writeConstraints)
											.streamReadConstraints(readConstraints)
										.build();
			return factory;
		}		
		
		
		@SuppressWarnings("unchecked")
		private static <B extends MapperBuilder<?,B>> B _mapperBuilderWithModules(final MapperBuilder<?,?> builder,
																		   		  final Set<? extends MarshallerModule> marshallerModules,final Set<? extends JacksonModule> jacksonModules) {
			return (B)builder.addModules(marshallerModules != null ? marshallerModules.stream()
					   			  											 	      .filter(mod -> mod instanceof JacksonModule)
					   			  											 	      .map(JacksonModule.class::cast)
					   			  											 	      .toArray(JacksonModule[]::new)
												   			  	   : new JacksonModule[0])
							 .addModules(jacksonModules != null ? jacksonModules.stream()
												   			  					.toArray(JacksonModule[]::new)
												   			    : new JacksonModule[0]);
		}
		@SuppressWarnings("unchecked")
		private static <B extends MapperBuilder<?,B>> B _mapperBuilderWithDefaultConfig(final MapperBuilder<?,?> builder) {
			// visibility: use only fields
			builder.changeDefaultVisibility(visibilityChecker -> {
												// use only field annotations
												return visibilityChecker.withVisibility(PropertyAccessor.ALL, 	Visibility.NONE)
																 		.withVisibility(PropertyAccessor.FIELD,	Visibility.ANY)
																 		.withVisibility(PropertyAccessor.CREATOR, Visibility.ANY)
																 		;
																		//.withVisibility(PropertyAccessor.GETTER,Visibility.NONE)
																 		//.withVisibility(PropertyAccessor.SETTER,Visibility.NONE)
																 		//.withVisibility(PropertyAccessor.CREATOR,Visibility.NONE)
										   });
			// Pretty print
			builder.enable(SerializationFeature.INDENT_OUTPUT);

			// Default date format
			builder.defaultDateFormat(new SimpleDateFormat(DateTimeFormatUtils.FORMAT_PATTERN_FOR_ISO_INSTANT))
				   .defaultTimeZone(TimeZone.getDefault());

			// ------ Features: https://github.com/FasterXML/jackson-databind/wiki/Mapper-Features

			// include only not-null
			builder.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY));	// NON_NULL + other

			// problems with lombok's noargs constructors: https://github.com/FasterXML/jackson-databind/issues/1197
			builder.configure(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES,true);
			builder.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);    
			
			// do not fail on null for primitives
			builder.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,false);

			// do NOT ignore final on field declarations
			builder.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS,false);

			// determines how transient modifier for fields is handled:
			//		- if disabled, it is only taken to mean exclusion of the field as accessor
			//		- if true, removal of the whole property.
			builder.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER,true);

			// polymorphic
			builder.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,false);
			builder.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS,false);

			// do NOT fail on unknown properties
			// case:
			//		a) An object with a polymorphic field:
			//					@MarshallType(as="qualifiedClause")
			//					@Accessors(prefix="_")
			//					public static class QualifiedQueryClause<Q extends QueryClause> {
			//						@MarshallField(as="clause")
			//						@Getter private final Q _clause;
			//
			//						@MarshallField(as="occur",
			//									   whenXml=@MarshallXmlField(attr=true))
			//						@Getter private final QueryClauseOccur _occur;
			//					}
			//		a) The serialized representation includes a typeId:
			//				{
			//				  "clause" : {
			//				    "typeId" : "hasDataClause",
			//				    "forMetaData" : "theValue"
			//				  },
			//				  "occur" : "MUST"
			//				}
			//		b) The serialization works if the json stream is readed as:
			//				QualifiedQueryClause<?> clause = mapper.readValue(json,
			//						  			   							  QualifiedQueryClause.class); <-- NO "hint" about the real type of _clause field
			//		   ... since there's NO hint about the QualifiedQueryClause's _clause field, the "typeId"
			//			   is NEEDED to know the real type of the _clause field
			//		c) BUT if the json stream is readed as:
			//				QualifiedQueryClause<HasDataQueryClause> clause = mapper.readValue(json,
			//						  			   							  				   new TypeReference<QualifiedQueryClause<HasDataQueryClause>>() { /* empty */ });
			//		   ... now the deserializer "knows" that the QualifiedQueryClause's _clause field has HasDataQueryClause type
			//		   ... so when the deserializer finds the "typeId" property, FAILS:
			//					com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "typeId"
			//			   meaning that HasDataQueryClause does NOT have a "typeId" field
			//			   (the deserializer does NOT need the "typeId" field to guess the _clause field's type since it's known beforehand)
			builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

			return (B)builder;
		}
	}
}
