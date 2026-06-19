package r01f.yaml.merge;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import r01f.types.Path;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class YamlMerger {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String COMBINE_ID_PROPERTY_NAME = "combine_id";
	private static final YAMLMapper YAML_MAPPER = new YAMLMapper(new YAMLFactory());
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final YAMLMapper _yamlMapper;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public YamlMerger() {
		this(YAML_MAPPER);
	}
	public YamlMerger(final YAMLMapper yamlMapper) {
		_yamlMapper = yamlMapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public JsonNode merge(final String fileSrc1,final String fileSrc2) throws IOException {
		return this.merge(new ByteArrayInputStream(fileSrc1.getBytes()),new ByteArrayInputStream(fileSrc2.getBytes()));
	}
	public JsonNode mergeFiles(final Path file1Path,final Path file2Path) throws IOException {
		return this.merge(new FileInputStream(file1Path.asAbsoluteString()),new FileInputStream(file2Path.asAbsoluteString()));
	}
	public JsonNode merge(final InputStream is1, final InputStream is2) throws IOException {
		JsonNode node1 = Yamls.using(_yamlMapper)
							  .readYaml(is1);
		JsonNode node2 = Yamls.using(_yamlMapper)
							  .readYaml(is2);
		return this.merge(node1,node2);
	}
	public JsonNode merge(final JsonNode node1,final JsonNode node2) {
		return _merge(node1,node2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MERGE
/////////////////////////////////////////////////////////////////////////////////////////
	private JsonNode _merge(final JsonNode... nodes) {
		JsonNode accumNode = _yamlMapper.createObjectNode();

		if (nodes == null) return accumNode;

		for (JsonNode updateNode : nodes) {
			if (updateNode == null) continue;
			_recursiveMerge(accumNode,updateNode);	// updateNode.deepCopy()
		}
		return accumNode;
	}
	private void _recursiveMerge(final JsonNode mainNode,final JsonNode updateNode) {
		if (updateNode == null || updateNode.isMissingNode()) {
			return;
		} else if (!updateNode.isObject()
				|| mainNode == null || !mainNode.isObject()) {
			return;
		}
		updateNode.propertyStream()
				  .forEach(entry -> {
								String updateNodeField = entry.getKey();
								JsonNode currUpdateNode = entry.getValue();
								JsonNode matchingMainNode = mainNode.path(updateNodeField);

								if (currUpdateNode.isObject()
								 && matchingMainNode.isObject()) {
									// Recursive case
									_recursiveMerge(matchingMainNode,currUpdateNode);

								} else if (currUpdateNode.isArray()
										&& matchingMainNode.isArray()) {
									// Array
									ArrayNode currMainArrayNode = (ArrayNode)matchingMainNode;
									ArrayNode currUpdateArrayNode = (ArrayNode)currUpdateNode;

									_recursiveMerge(currMainArrayNode,currUpdateArrayNode);

								} else if (mainNode.isObject()) {
									// A property of the main node
									ObjectNode mainObjNode = ((ObjectNode)mainNode);

									mainObjNode.replace(updateNodeField,currUpdateNode);
								}
				  		   });
	}
	private void _recursiveMerge(final ArrayNode mainArrayNode,final ArrayNode updateArrayNode) {
		ArrayNode mergedArrayNode = _yamlMapper.createArrayNode();

		// Index the ObjectNodes in the main and update arrays by their "combine.id" property
		// and list the nodes without a "combine.id" property
		Map<String,ObjectNode> mainArrayNodeIndexedByCombineId = _indexObjectNodesByCombineId(mainArrayNode);
		List<JsonNode> mainArrayNodesWithoutCombineId = _listNodesWithoutCombineId(mainArrayNode);

		Map<String,ObjectNode> updateArrayNodeIndexedByCombineId = _indexObjectNodesByCombineId(updateArrayNode);
		List<JsonNode> updateArrayNodesWithoutCombineId = _listNodesWithoutCombineId(updateArrayNode);


		// Add the nodes with a matching "combine.id" property from the mainArrayNodesWithoutCombineId to the merged array
		mainArrayNodeIndexedByCombineId.entrySet()
									   .stream()
									   .forEach(mainObjNodeEntry -> {
													 String combineId = mainObjNodeEntry.getKey();
													 ObjectNode mainObjNode = mainObjNodeEntry.getValue();

													 // does the update array contains a node with the same "combine.id"?
													 ObjectNode updateObjNodeWithSameCombineId = updateArrayNodeIndexedByCombineId.get(combineId);
													 if (updateObjNodeWithSameCombineId != null) {
														 // merge
														 _recursiveMerge(mainObjNode,updateObjNodeWithSameCombineId);
														 // ... and add to the merged array
														 mergedArrayNode.add(mainObjNode);
														 // remove the update node so that it is not added again
														 updateArrayNodeIndexedByCombineId.remove(combineId);
													 } else {
														 // just add the main node to the merged array
														 mergedArrayNode.add(mainObjNode);
													 }
									   			});
		// at this point the updateArrayNodeIndexedByCombineId contains only the nodes that do not have a matching "combine.id" in the main array
		updateArrayNodeIndexedByCombineId.values()
									   	 .stream()
									   	 .forEach(updateObjNode -> {
													 mergedArrayNode.add(updateObjNode);	// add the update node to the merged array
									   	  		  });
		// Add the nodes without a "combine.id" property from the main array and updated array to the merged array
		mergedArrayNode.addAll(mainArrayNodesWithoutCombineId);		// add the main nodes without a "combine.id" property
		mergedArrayNode.addAll(updateArrayNodesWithoutCombineId);	// add the update nodes without a "combine.id" property

		// finally replace the main array with the merged array
		mainArrayNode.removeAll();	// remove all the nodes from the main array
		mainArrayNode.addAll(mergedArrayNode);	// add the merged array nodes to the main array
	}
	/**
	 * Indexes the ObjectNodes in the given ArrayNode by their "combine.id" property
	 * @param arrayNode
	 * @return
	 */
	private Map<String,ObjectNode> _indexObjectNodesByCombineId(final ArrayNode arrayNode) {
		if (arrayNode == null) return Map.of();	// create an unmodifiable map

		// Index the object nodes by their "combine.id" property
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(arrayNode.elements().iterator(),
																		Spliterator.ORDERED),
			 			  			false)
							 .filter(node -> {
							  			if (!node.isObject()) return false;
							  			ObjectNode objNode = (ObjectNode)node;
							  			JsonNode combineIdNode = objNode.path(COMBINE_ID_PROPERTY_NAME);
							  			return combineIdNode != null && !combineIdNode.isMissingNode()
	  												? true
	  												: false;
						  		  	 })
							 .map(ObjectNode.class::cast)
							 .collect(Collectors.toMap(objNode -> objNode.get(COMBINE_ID_PROPERTY_NAME).asText(null),
									 				   objNode -> objNode));
	}
	private static List<JsonNode> _listNodesWithoutCombineId(final ArrayNode arrayNode) {
		if (arrayNode == null) return List.of();	// create an unmodifiable list

		// List the object nodes without a "combine.id" property
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(arrayNode.elements().iterator(),
																		Spliterator.ORDERED),
			 			  			false)
							 // remove the nodes that have a "combine.id" property
							 .filter(node -> {
							  			if (!node.isObject()) return false;
							  			ObjectNode objNode = (ObjectNode)node;
							  			JsonNode combineIdNode = objNode.get(COMBINE_ID_PROPERTY_NAME);

							  			return combineIdNode == null
	  												? true
	  												: false;
						  		  })
							 // collect the remaining nodes
							 .collect(Collectors.toList());
	}
	/**
	 * Finds an ObjectNode in the given ArrayNode that has a "combine.id" property
	 * @param arrayNode
	 * @param combineId
	 * @return
	 */
	private static ArrayObjectNodeWithIndex _findObjectNodeWithCombineId(final ArrayNode arrayNode,final String combineId) {
		if (arrayNode == null) return null;

		ArrayObjectNodeWithIndex outResult = null;
		for (int i=0; i < arrayNode.size(); i++) {
			JsonNode node = arrayNode.get(i);
			if (node.isObject()) {
				ObjectNode objNode = (ObjectNode)node;
				JsonNode combineIdNode = objNode.get(COMBINE_ID_PROPERTY_NAME);
				if (combineIdNode != null && combineId.equals(combineIdNode.asText(null))) {
					outResult = new ArrayObjectNodeWithIndex(i,objNode);
					break;	// found it!
				}
			}
		}
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static record ArrayObjectNodeWithIndex(int index,ObjectNode objectNode) {
		// nothing
	}
}
