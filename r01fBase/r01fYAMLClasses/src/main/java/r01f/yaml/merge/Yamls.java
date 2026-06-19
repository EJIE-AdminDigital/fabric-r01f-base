package r01f.yaml.merge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

/**
 * Utility class for YAML serialization and deserialization operations using Jackson YAML processor.
 * This class provides a fluent interface for working with YAML data through static factory methods
 * and a support class that handles the actual YAML operations.
 *
 * Features:
 * - Minimizes quotes in YAML output
 * - Disables YAML document start marker (---)
 * - Provides methods for serializing objects to YAML format
 *
 * Usage example:
 * ```java
 * YamlSupport yamlSupport = Yamls.utils();
 * // Serialize an object to YAML string
 * String yamlString = yamlSupport.serializeToYamlString(myObject);
 * // Write YAML to an output stream
 * yamlSupport.serializeToYaml(myObject, outputStream);
 * ```
 */
public abstract class Yamls {
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private Yamls() {
		// Prevent instantiation
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new YamlSupport instance with default configuration:
     * - Minimizes quotes in output
     * - Disables YAML document start marker
     *
     * @return a new {@link YamlSupport} instance with default configuration
     */
    public static YamlSupport utils() {
        YAMLFactory yamlFactory = YAMLFactory.builder()
        									 .enable(YAMLWriteFeature.MINIMIZE_QUOTES)
        									 .enable(YAMLWriteFeature.WRITE_DOC_START_MARKER)
        									 .build();
        return new YamlSupport(YAMLMapper.builder(yamlFactory)
        								 .build());
    }
    /**
     * Creates a YamlSupport instance using a custom ObjectMapper.
     * This allows for customized YAML serialization behavior.
     *
     * @param yamlMapper the custom {@link ObjectMapper} to use for YAML operations
     * @return a new {@link YamlSupport} instance using the provided mapper
     * @throws IllegalArgumentException if yamlMapper is null
     */
    public static YamlSupport using(final YAMLMapper yamlMapper) {
		if (yamlMapper == null) throw new IllegalArgumentException("ObjectMapper cannot be null");
		return new YamlSupport(yamlMapper);
    }

    /**
     * Creates a YamlSupport instance using a custom YAMLFactory.
     * This allows for customized YAML generation behavior.
     *
     * @param yamlFactory the custom {@link YAMLFactory} to use for YAML operations
     * @return a new {@link YamlSupport} instance using the provided factory
     * @throws IllegalArgumentException if yamlFactory is null
     */
    public static YamlSupport using(final YAMLFactory yamlFactory) {
		if (yamlFactory == null) throw new IllegalArgumentException("YAMLFactory cannot be null");
		ObjectMapper yamlMapper = new ObjectMapper(yamlFactory);
		return new YamlSupport(yamlMapper);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  YAML SUPPORT CLASS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Class that provides YAML serialization operations.
	 * This class wraps a Jackson ObjectMapper configured for YAML processing
	 * and provides convenient methods for YAML operations.
	 */
	public static class YamlSupport {
    	private final ObjectMapper _yamlMapper;

    	public YamlSupport(final ObjectMapper yamlMapper) {
			_yamlMapper = yamlMapper;
		}
    	////////// YAMLS READ / WRITE METHODS
		/**
		 * Writes a JsonNode to an OutputStream as YAML
		 * @param node
		 * @param os
		 * @throws IOException
		 */
		public void writeYaml(final JsonNode node,final OutputStream os) throws IOException {
			Yamls.writeYaml(_yamlMapper,
						    node,os);
		}
		/**
		 * Reads YAML from an InputStream and returns it as a JsonNode
		 * @param is
		 * @return
		 * @throws IOException
		 */
		public JsonNode readYaml(final InputStream is) throws IOException {
			return Yamls.readYaml(_yamlMapper,
								  is);
		}
    	////////// YAMLS SERIALIZATION METHODS
	    /**
	     * Serializes an object to YAML and writes it to the provided output stream
	     * @param obj The object to serialize
	     * @param os The output stream to write to
	     * @throws IOException if there's an error writing the YAML
	     */
	    public <T> void serializeToYaml(final T obj,final OutputStream os) throws IOException {
	        Yamls.serializeToYaml(_yamlMapper,
	        					  obj,os);
	    }
	    /**
	     * Serializes an object to YAML and writes it to the provided writer
	     * @param obj The object to serialize
	     * @param writer The writer to write to
	     * @throws IOException if there's an error writing the YAML
	     */
	    public <T> void serializeToYaml(final T obj,final Writer writer) throws IOException {
	        Yamls.serializeToYaml(_yamlMapper,
	        					  obj,writer);
	    }
	    /**
	     * Serializes an object to a YAML string
	     * @param obj The object to serialize
	     * @return The YAML string representation of the object
	     * @throws IOException if there's an error writing the YAML
	     */
	    public <T> String serializeToYamlString(final T obj) throws IOException {
	        return Yamls.serializeToYamlString(_yamlMapper,
	        							  	   obj);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Writes a JsonNode to an OutputStream as YAML
	 * @param yamlMapper
	 * @param node
	 * @param os
	 * @throws IOException
	 */
	public static void writeYaml(final ObjectMapper yamlMapper,
							  	 final JsonNode node,final OutputStream os) throws IOException {
		yamlMapper.writeValue(os,node);
	}
	/**
	 * Reads YAML from an InputStream and returns it as a JsonNode
	 * @param yamlMapper
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static JsonNode readYaml(final ObjectMapper yamlMapper,
									final InputStream is) throws IOException {
		return yamlMapper.readTree(is);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Serializes an object to YAML and writes it to the provided output stream
     * @param yamlMapper
     * @param obj The object to serialize
     * @param os The output stream to write to
     * @throws IOException if there's an error writing the YAML
     */
    public static <T> void serializeToYaml(final ObjectMapper yamlMapper,
    									   final T obj,final OutputStream os) throws IOException {
        yamlMapper.writeValue(os,obj);
    }
    /**
     * Serializes an object to YAML and writes it to the provided writer
     * @param yamlMapper
     * @param obj The object to serialize
     * @param writer The writer to write to
     * @throws IOException if there's an error writing the YAML
     */
    public static <T> void serializeToYaml(final ObjectMapper yamlMapper,
    									   final T obj,final Writer writer) throws IOException {
        yamlMapper.writeValue(writer, obj);
    }
    /**
     * Serializes an object to a YAML string
     * @param yamlMapper
     * @param obj The object to serialize
     * @return The YAML string representation of the object
     * @throws IOException if there's an error writing the YAML
     */
    public static <T> String serializeToYamlString(final ObjectMapper yamlMapper,
    											   final T obj) throws IOException {
        return yamlMapper.writeValueAsString(obj);
    }
}