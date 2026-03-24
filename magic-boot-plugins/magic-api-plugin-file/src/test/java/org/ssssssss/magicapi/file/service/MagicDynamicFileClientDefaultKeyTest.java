package org.ssssssss.magicapi.file.service;

import org.dromara.x.file.storage.core.FileStorageService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class MagicDynamicFileClientDefaultKeyTest {

    @Test
    void shouldPreferStorageMarkedAsDefault() {
        MagicDynamicFileClient client = new MagicDynamicFileClient();
        FileStorageService first = mock(FileStorageService.class);
        FileStorageService second = mock(FileStorageService.class);

        client.put("1", "storage-a", "A", first, false);
        client.put("2", "storage-b", "B", second, true);

        assertEquals("storage-b", client.getDefaultKey(), "默认存储应优先使用 isDefault=true 的平台");
    }

    @Test
    void shouldFallbackToExistingStorageWhenNoDefaultFlag() {
        MagicDynamicFileClient client = new MagicDynamicFileClient();
        FileStorageService first = mock(FileStorageService.class);

        client.put("1", "storage-a", "A", first, false);

        assertEquals("storage-a", client.getDefaultKey(), "当没有 isDefault=true 时，应兜底到可用平台");
    }

    @Test
    void shouldRecalculateDefaultAfterDeletingCurrentDefault() {
        MagicDynamicFileClient client = new MagicDynamicFileClient();
        FileStorageService first = mock(FileStorageService.class);
        FileStorageService second = mock(FileStorageService.class);

        client.put("1", "storage-a", "A", first, false);
        client.put("2", "storage-b", "B", second, true);
        client.delete("storage-b");

        assertNotNull(client.getDefaultKey(), "删除默认存储后应重新选择默认平台");
        assertEquals("storage-a", client.getDefaultKey(), "删除默认存储后应回退到剩余可用平台");
    }
}
