package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.AnnotationFormatError;

import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.util.types.Strings;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import tools.jackson.dataformat.xml.XmlAnnotationIntrospector;
import tools.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;

/**
 * The jackson annotation introspector type hierarchy is like:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[JacksonAnnotationIntrospector]			<-- this is where jackson json annotations are detected
 * 			|		|--[JacksonXmlAnnotationIntrospector] 	<-- this is where jackson xml annotations are detected
 * 			|--[JakartaXmlBindAnnotationIntrospector]	<-- this is where jakarta xml annotations are detected
 * 			|
 * 			|--[AnnotationIntrospectorPair]		<-- combine two AnnotationIntrospector instances so Jackson will consult one and then the other when inspecting annotations
 * </pre>
 *
 * when it comes to xml annotation introspection:
 * <pre>
 * 		 [AnnotationIntrospector]
 * 			|
 * 			|--[JakartaXmlBindAnnotationIntrospector] <-- does NOT implements XmlAnnotationIntrospector
 * 			|
 * 			|--[JacksonXmlAnnotationIntrospector] extends JacksonAnnotationIntrospector that also extends AnnotationIntrospector
 * </pre>
 *
 * Here, delegation is used in addition to inheritance:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[MarshallerXmlAnnotationIntrospector]
 * 					|--MarshallerXmlAnnotationIntrospector
 * 							|--(delegates)-> (1) [JacksonXmlAnnotationIntrospector]
 * 										 	 (2) [JakartaXmlBindAnnotationIntrospector]
 * </pre>
 */
@Slf4j
public class MarshallerXmlAnnotationIntrospector
	 extends MarshallerAnnotationIntrospector
  implements XmlAnnotationIntrospector {

	private static final long serialVersionUID = 4120643020074142400L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String JAXB_MARKER_FOR_DEFAULT = "##default";

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// Delegates
	private final JacksonXmlAnnotationIntrospector _delegatedJacksonXmlAnnotationIntrospector;
	private final JakartaXmlBindAnnotationIntrospector _delegatedJakartaXmlBindAnnotationIntrospector;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerXmlAnnotationIntrospector(final ClassgraphScanHandler typeScan,
											   final JacksonXmlAnnotationIntrospector jacksonXmlAnnotationIntrospector,
											   final JakartaXmlBindAnnotationIntrospector jakartaXmlBindAnnotationIntrospector) {
		super(typeScan);
		_delegatedJacksonXmlAnnotationIntrospector = jacksonXmlAnnotationIntrospector;
		_delegatedJakartaXmlBindAnnotationIntrospector = jakartaXmlBindAnnotationIntrospector;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	XML EXTENSIONS
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public String findNamespace(final MapperConfig<?> config,final Annotated ann) {
		String outNameSpace = null;

		// [1] - @FieldMarshall annotated fields
		if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField m = ann.getAnnotation(MarshallField.class);
			outNameSpace = m.namespace();
		}
		// [2] - @TypeMarshall annotated types
		if (Strings.isNullOrEmpty(outNameSpace)
		 && ann.hasAnnotation(MarshallType.class)) {
			MarshallType t = ann.getAnnotation(MarshallType.class);
			outNameSpace = t.namespace();
		}
		// [3] - delegate to other annotation introspectors
		if (Strings.isNullOrEmpty(outNameSpace)) outNameSpace = _delegatedJacksonXmlAnnotationIntrospector.findNamespace(config,ann);
		if (Strings.isNullOrEmpty(outNameSpace)) outNameSpace = _delegatedJakartaXmlBindAnnotationIntrospector.findNamespace(config,ann);
		// [4] - checkings
		if (JAXB_MARKER_FOR_DEFAULT.equals(outNameSpace)) outNameSpace = null;
		if (Strings.isNullOrEmpty(outNameSpace)) outNameSpace = null;

		// [99] - return
		return outNameSpace;
    }
    @Override
	public Boolean isOutputAsAttribute(final MapperConfig<?> config,final Annotated ann) {
		Boolean outAttr = null;

		// [0] - do not mind annotated types
		if (ann instanceof AnnotatedClass) return false;

		// [1] - @FieldMarshall annotated fields
		if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField fmAnn = ann.getAnnotation(MarshallField.class);
			outAttr = fmAnn.whenXml() != null ? fmAnn.whenXml().attr()
										  	  : false;
			if (fmAnn.whenXml() != null
			 && fmAnn.whenXml().asParentElementValue() == true
			 && fmAnn.whenXml().attr() == true) throw new AnnotationFormatError(String.format("Field %s of type %s is set to be serialized as xml attribute and text: both modifiers cannot be set at the same time",
																							  ((AnnotatedField)ann).getAnnotated().getName(),
																							  ((AnnotatedField)ann).getDeclaringClass()));
		}
		// [2] - Delegate to other annotation introspectors
		if (outAttr == null) outAttr = _delegatedJacksonXmlAnnotationIntrospector.isOutputAsAttribute(config,ann);
		if (outAttr == null) outAttr = _delegatedJakartaXmlBindAnnotationIntrospector.isOutputAsAttribute(config,ann);

		// [3] - check
//		if (outAttr != null
//		 && outAttr == true
//		 && ann.getType().isAbstract()
//		 && !ann.getType().isPrimitive()) {
//			AnnotatedField annF = (AnnotatedField)ann;
//			throw new AnnotationFormatError(String.format("Field %s of type %s is defined using an abstract/interface type (%s): it cannot be mapped as an xml attribute",
//				 										  ann.getName(),annF.getDeclaringClass(),annF.getRawType()));
//		}
		// [99] - return
		return outAttr;
    }
    @Override
	public Boolean isOutputAsText(final MapperConfig<?> config,final Annotated ann) {
		Boolean outText = null;

		// [1] - @FieldMarshall annotated
		if (ann instanceof AnnotatedField annF) {
			MarshallField mfAnn = ann.getAnnotation(MarshallField.class);
			if (mfAnn != null
			 && mfAnn.whenXml() != null) {

				outText = mfAnn.whenXml()
							   .asParentElementValue();

				if (mfAnn.whenXml().attr() == true
				 && outText == true) throw new AnnotationFormatError(String.format("Field %s of type %s is set to be serialized as xml attribute and text: both modifiers cannot be set at the same time",
																				   ((AnnotatedField)ann).getAnnotated().getName(),
																				   ((AnnotatedField)ann).getDeclaringClass()));
			}
			else if ((annF.getType().isCollectionLikeType() || annF.getType().isCollectionLikeType())
				  && mfAnn != null
				  && mfAnn.whenCollectionLike() != null
				  && mfAnn.whenCollectionLike().useWrapping() == false) {
				outText = true;
			}
		} else if (ann instanceof AnnotatedParameter annP
			     && ann.hasAnnotation(MarshallFrom.class)) {
			MarshallFrom mfAnn = ann.getAnnotation(MarshallFrom.class);
			// this is the case of a record component
			// ... do not what to do
		}

		// [2] - delegate to other annotation introspectors
		if (outText == null) outText = _delegatedJacksonXmlAnnotationIntrospector.isOutputAsText(config,ann);
		if (outText == null) outText = _delegatedJakartaXmlBindAnnotationIntrospector.isOutputAsText(config,ann);

		// [99] - return
		return outText;
    }
    @Override
	public Boolean isOutputAsCData(final MapperConfig<?> config,final Annotated ann) {
		Boolean outCDATA = null;
		// [1] - @FieldMarshall annotated
		if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField fm = ann.getAnnotation(MarshallField.class);
			outCDATA = fm.escape();
		}
		if (outCDATA == null
		 && ann.hasAnnotation(MarshallField.class)) {
			MarshallField fm = ann.getAnnotation(MarshallField.class);
			outCDATA = fm.escape();
		}
		// [2] - delegate to other annotation introspectors
		if (outCDATA == null) outCDATA = _delegatedJacksonXmlAnnotationIntrospector.isOutputAsCData(config,ann);
		if (outCDATA == null) outCDATA = _delegatedJakartaXmlBindAnnotationIntrospector.isOutputAsCData(config,ann);
		// return
		return outCDATA;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	CLASS / TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PropertyName findRootName(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _findName(config,ac,
				  		 MarshallerXmlNameFor.TYPE_ROOT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	General member (field, method/constructor) annotations
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PropertyName findWrapperName(final MapperConfig<?> config, final Annotated ann) {
		return _findName(config,ann,
						 MarshallerXmlNameFor.PROPERTY_WRAPPER);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZATION
/////////////////////////////////////////////////////////////////////////////////////////
////////// Property annotations
	@Override
	public PropertyName findNameForSerialization(final MapperConfig<?> config,final Annotated ann) {
		return _findName(config,ann,
						 MarshallerXmlNameFor.PROPERTY_SERIALIZATION);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZATION
/////////////////////////////////////////////////////////////////////////////////////////
////////// Property annotations
	@Override
	public PropertyName findNameForDeserialization(final MapperConfig<?> config,final Annotated ann) {
		return _findName(config,ann,
						 MarshallerXmlNameFor.PROPERTY_DESERIALIZATION);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private enum MarshallerXmlNameFor {
		PROPERTY_SERIALIZATION,
		PROPERTY_DESERIALIZATION,
		PROPERTY_WRAPPER,
		TYPE_ROOT;
	}
	private PropertyName _findName(final MapperConfig<?> config,final Annotated ann,
								   final MarshallerXmlNameFor whatFor) {
		if (ann instanceof AnnotatedMethod ) return null;

		PropertyName outName = null;

		// [1] - Use @MarshallField if it's a collection-like field or type, see if an item name is set
		// 1.1 - Type level
		if (ann instanceof AnnotatedClass
		 && ann.hasAnnotation(MarshallField.class)) { // do NOT move!! (problem with aspects inter-type injected fields)) {

			MarshallType mt = ann.getAnnotation(MarshallType.class);

			outName = Strings.isNullOrEmpty(mt.namespace()) ? new PropertyName(mt.as())
														    : new PropertyName(mt.as(),mt.namespace());
		}
		// 1.2 - Properties level
		else if (ann instanceof AnnotatedField
			  && ann.hasAnnotation(MarshallField.class)) {	// do NOT move!! (problem with aspects inter-type injected fields)

			MarshallField fm = ann.getAnnotation(MarshallField.class);

			if (ann.getType().isCollectionLikeType()
			 || ann.getType().isArrayType()) {
				// collection wrapper
				if (whatFor ==  MarshallerXmlNameFor.PROPERTY_WRAPPER) {
					if (fm.whenCollectionLike() != null
					 && fm.whenCollectionLike().useWrapping() == false) {
						outName = PropertyName.NO_NAME;		// DO NOT WRAP
					}
					else if (fm.whenXml() != null
						  && fm.whenXml().asParentElementValue() == true) {
						outName = PropertyName.NO_NAME;		// DO NOT WRAP
					}
					else {
						outName = new PropertyName(fm.as());// WRAP
					}
				// collection's items
				} else if (whatFor == MarshallerXmlNameFor.PROPERTY_SERIALIZATION
						|| whatFor == MarshallerXmlNameFor.PROPERTY_DESERIALIZATION) {
					if (fm.whenXml() != null
						&& !MarshallField.MARKER_FOR_DEFAULT.equals(fm.whenXml().collectionElementName())) {
						// annotated with @MarshallField and explicitly setting the collection element names
						// 		@MarshallField(as="wrapper")
						//				   	   whenXml=@MarshallXmlField(collectionElementName="item"))
						//		@Getter private Collection<MyType> _items;
						outName = PropertyName.construct(fm.whenXml().collectionElementName());
					} else {
						// not explicitly setting the collection element names: use the type name set at the collection's items
						JavaType contentType = ann.getType().getContentType();
						MarshallType mtAnn = contentType.getRawClass().getAnnotation(MarshallType.class);
						if (mtAnn != null) outName = PropertyName.construct(mtAnn.as());
					}
				}
			}
		}
		// 1.2 - Constructor level (record components)
		else if (ann instanceof AnnotatedParameter annP
			  && ann.hasAnnotation(MarshallFrom.class)) {
			MarshallFrom mfAnn = ann.getAnnotation(MarshallFrom.class);
			// this is the case of a record component
			// ... do not what to do
		}

		// [2] - Delegate to other annotation introspectors
		// 2.1 - jackson xml native annotations
		if (outName == null
		 || outName.equals(PropertyName.USE_DEFAULT)) {
			outName = _delegateFindName(config,ann,
										whatFor,
										_delegatedJacksonXmlAnnotationIntrospector);
		}
		// 2.2 - jakarta xml annotations
		if (outName == null
		 || outName.equals(PropertyName.USE_DEFAULT)) {
			outName = _delegateFindName(config,ann,
										whatFor,
										_delegatedJakartaXmlBindAnnotationIntrospector);
		}
		// 2.3 - finally the marshaller
		if (outName == null
		 || outName.equals(PropertyName.USE_DEFAULT)) {
			outName = switch (whatFor) {
							case PROPERTY_SERIALIZATION -> super.findNameForSerialization(config,ann);
							case PROPERTY_DESERIALIZATION -> super.findNameForDeserialization(config,ann);
							case TYPE_ROOT -> super.findRootName(config,(AnnotatedClass)ann);
							case PROPERTY_WRAPPER -> super.findWrapperName(config,ann);
					   };
		}
		// [3] - default
		if (outName == null) outName = PropertyName.USE_DEFAULT;

		// [99] - Return
		return outName;
	}
	private static PropertyName _delegateFindName(final MapperConfig<?> config,final Annotated ann,
												  final MarshallerXmlNameFor whatFor,
												  final AnnotationIntrospector delegatedAnnotationIntrospector) {
		return switch (whatFor) {
					case PROPERTY_SERIALIZATION -> delegatedAnnotationIntrospector.findNameForSerialization(config,ann);
					case PROPERTY_DESERIALIZATION -> delegatedAnnotationIntrospector.findNameForDeserialization(config,ann);
					case TYPE_ROOT -> delegatedAnnotationIntrospector.findRootName(config,(AnnotatedClass)ann);
					case PROPERTY_WRAPPER -> delegatedAnnotationIntrospector.findWrapperName(config,ann);
			   };
	}
}
