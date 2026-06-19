package r01f.objectstreamer;

import java.util.Collection;

import r01f.objectstreamer.annotationintrospector.MarshallerAnnotationIntrospector;
import r01f.reflection.scanner.ClassgraphScanHandler;
import tools.jackson.core.util.VersionUtil;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
public class MarshallerModuleForJson
	 extends MarshallerModuleBase {

	private static final long serialVersionUID = 526390669822422298L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String NAME = "r01.marshallerJsonModule";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerModuleForJson(final Collection<JacksonSerializerAndDeserializerPair<?>> serAndDeserPairs,
								   final ClassgraphScanHandler typeScan) {
		super(NAME,VersionUtil.versionFor(MarshallerModuleForJson.class),
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
		context.appendAnnotationIntrospector(new MarshallerAnnotationIntrospector(_typeScan));
	}
}
