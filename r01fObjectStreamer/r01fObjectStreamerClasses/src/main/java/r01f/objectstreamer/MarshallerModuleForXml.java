package r01f.objectstreamer;

import java.util.Collection;

import r01f.objectstreamer.annotationintrospector.MarshallerXmlAnnotationIntrospector;
import r01f.reflection.scanner.ClassgraphScanHandler;
import tools.jackson.core.util.VersionUtil;
import tools.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import tools.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
public class MarshallerModuleForXml
	 extends MarshallerModuleBase {

	private static final long serialVersionUID = 526390669822422298L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String NAME = "r01.marshallerXmlModule";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerModuleForXml(final Collection<JacksonSerializerAndDeserializerPair<?>> serAndDeserPairs,
								  final ClassgraphScanHandler typeScan) {
		super(NAME,VersionUtil.versionFor(MarshallerModuleForXml.class),
			  serAndDeserPairs,
			  typeScan);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setupModule(final SetupContext context) {
		super.setupModule(context);

		// Append AFTER other introspectors (instead of before) since
		// explicit annotations should have precedence
		context.appendAnnotationIntrospector(new MarshallerXmlAnnotationIntrospector(_typeScan,
																					 new JacksonXmlAnnotationIntrospector(),
																					 new JakartaXmlBindAnnotationIntrospector()));
	}
}
