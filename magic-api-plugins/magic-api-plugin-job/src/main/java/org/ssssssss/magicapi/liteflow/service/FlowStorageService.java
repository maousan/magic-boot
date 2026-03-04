package org.ssssssss.magicapi.liteflow.service;

import java.io.IOException;
import java.util.List;

/**
 * Flow storage service interface for LiteFlow definitions and components.
 * Provides basic CRUD operations and in-memory refresh capabilities.
 */
public interface FlowStorageService {

    // Root locations for flow definitions and component scripts
    String FLOW_ROOT = "data/magic-api/liteflow/flow/";
    String COMPONENT_ROOT = "data/magic-api/liteflow/component/";

    // Flow definitions
    void createFlow(String fileName, String content) throws IOException;
    String readFlow(String fileName) throws IOException;
    void updateFlow(String fileName, String content) throws IOException;
    void deleteFlow(String fileName) throws IOException;
    List<String> listFlows() throws IOException;

    // Component scripts
    void createComponent(String fileName, String content) throws IOException;
    String readComponent(String fileName) throws IOException;
    void updateComponent(String fileName, String content) throws IOException;
    void deleteComponent(String fileName) throws IOException;
    List<String> listComponents() throws IOException;

    // Force refresh of in-memory cache
    void refresh() throws IOException;
}
