package org.ssssssss.magicboot.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnExpression("${springdoc.api-docs.enabled:true} && ${plugin.enabled:true}")
public class PluginPerOpenApiController {

    private final ObjectMapper objectMapper;
    private final PluginProperties pluginProperties;

    @GetMapping(value = "/v3/api-docs/plugin/{pluginId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> pluginOpenApi(@PathVariable String pluginId, HttpServletRequest request) {
        String pluginPrefix = PluginOpenApiConfiguration.resolvePluginApiPrefix(pluginProperties, pluginId);
        String pathPrefix = PluginOpenApiConfiguration.normalizePath(pluginPrefix + "/");
        try {
            String sourceUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(request.getContextPath() + "/v3/api-docs/pf4j-plugin-api")
                    .replaceQuery(null)
                    .toUriString();
            String rawJson = new RestTemplate().getForObject(sourceUrl, String.class);
            if (rawJson == null || rawJson.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("{\"message\":\"openapi source is empty\"}");
            }
            ObjectNode root = (ObjectNode) objectMapper.readTree(rawJson);
            ObjectNode filteredPaths = objectMapper.createObjectNode();
            JsonNode pathsNode = root.path("paths");
            if (pathsNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = pathsNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    if (entry.getKey().startsWith(pathPrefix)) {
                        filteredPaths.set(entry.getKey(), entry.getValue());
                    }
                }
            }
            root.set("paths", filteredPaths);
            filterTags(root, filteredPaths);
            ObjectNode info = root.with("info");
            info.put("title", "Plugin OpenAPI - " + pluginId);
            info.put("description", "PF4J plugin API docs for " + pluginId);
            return ResponseEntity.ok(root.toString());
        } catch (Exception e) {
            log.error("生成插件 OpenAPI 失败: pluginId={}", pluginId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"failed to generate plugin openapi\"}");
        }
    }

    private void filterTags(ObjectNode root, ObjectNode filteredPaths) {
        Set<String> usedTags = new HashSet<>();
        Iterator<Map.Entry<String, JsonNode>> pathIterator = filteredPaths.fields();
        while (pathIterator.hasNext()) {
            JsonNode pathItem = pathIterator.next().getValue();
            if (!pathItem.isObject()) {
                continue;
            }
            Iterator<Map.Entry<String, JsonNode>> operationIterator = pathItem.fields();
            while (operationIterator.hasNext()) {
                JsonNode operation = operationIterator.next().getValue();
                JsonNode tagsNode = operation.path("tags");
                if (tagsNode.isArray()) {
                    for (JsonNode tagNode : tagsNode) {
                        if (tagNode.isTextual()) {
                            usedTags.add(tagNode.asText());
                        }
                    }
                }
            }
        }
        JsonNode tagsNode = root.path("tags");
        if (!tagsNode.isArray()) {
            return;
        }
        ArrayNode filteredTags = objectMapper.createArrayNode();
        for (JsonNode tagNode : tagsNode) {
            String name = tagNode.path("name").asText();
            if (usedTags.contains(name)) {
                filteredTags.add(tagNode);
            }
        }
        root.set("tags", filteredTags);
    }
}
