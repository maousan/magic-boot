package org.ssssssss.magicapi.springdoc.entity;

import org.junit.Test;
import static org.junit.Assert.*;

public class OpenApiProviderTest {

    @Test
    public void testGenerateCamelCaseOperationId() {
        // 测试各种场景
        assertEquals("getApiUserList",
                generateCamelCaseOperationId("GET", "/api/user/list"));

        assertEquals("postApiUserById",
                generateCamelCaseOperationId("POST", "/api/user/{id}"));

        assertEquals("deleteApiUser",
                generateCamelCaseOperationId("DELETE", "/api/user"));

        assertEquals("putApiUserInfoByName",
                generateCamelCaseOperationId("PUT", "/api/user/info/{name}"));

        assertEquals("getApiV1Users",
                generateCamelCaseOperationId("GET", "/api/v1/users"));

        assertEquals("postApiV2AdminConfig",
                generateCamelCaseOperationId("POST", "/api/v2/admin/config"));
    }
}
