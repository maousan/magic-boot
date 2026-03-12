package org.ssssssss.magicboot.modules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anyline.service.AnylineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.ssssssss.script.annotation.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典翻译模块
 * 用于对 List<Map<String, Object>> 结构的数据进行字典翻译
 * 支持从数据库查询字典项，自动翻译字段
 */
@Component
@MagicModule("trans")
public class TranslateModule {

    private static final Logger logger = LoggerFactory.getLogger(TranslateModule.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private AnylineService anylineService;

    /**
     * 字典缓存
     */
    private final Map<String, List<DictItem>> dictCache = new ConcurrentHashMap<>();

    /**
     * 根据字典类型查询字典项的 SQL
     */
    private static final String SQL_GET_DICT_ITEMS =
            "SELECT items.value, items.label " +
            "FROM sys_dict_items items " +
            "INNER JOIN sys_dict d ON d.id = items.dict_id " +
            "WHERE d.type = #{type} AND d.is_del = 0 AND items.is_del = 0 " +
            "ORDER BY items.sort";

    /**
     * 翻译数据列表（使用 JSON 配置)
     *
     * @param dataList   数据列表
     * @param configJson JSON 配置字符串
     * @return 翻译后的数据列表
     */
    @Comment("翻译数据列表（使用JSON配置）")
    public List<Map<String, Object>> translate(
            @Comment("数据列表") List<Map<String, Object>> dataList,
            @Comment("JSON配置字符串") String configJson) {
        if (dataList == null || dataList.isEmpty() || configJson == null || configJson.trim().isEmpty()) {
            return dataList;
        }

        try {
            // 解析 JSON 配置
            Map<String, FieldConfig> configMap = OBJECT_MAPPER.readValue(
                    configJson,
                    new TypeReference<>() {
                    }
            );

            return translateInternal(dataList, configMap);
        } catch (Exception e) {
            logger.error("字典翻译失败", e);
            return dataList;
        }
    }

    /**
     * 翻译数据列表(使用 Map 配置)
     *
     * @param dataList  数据列表
     * @param configMap 配置 Map
     * @return 翻译后的数据列表
     */
    @Comment("翻译数据列表（使用Map配置)")
    public List<Map<String, Object>> translate(
            @Comment("数据列表") List<Map<String, Object>> dataList,
            @Comment("配置Map") Map<String, Object> configMap) {
        if (dataList == null || dataList.isEmpty() || configMap == null || configMap.isEmpty()) {
            return dataList;
        }

        try {
            // 将 Map 转换为 FieldConfig 对象
            Map<String, FieldConfig> fieldConfigMap = OBJECT_MAPPER.convertValue(
                    configMap,
                    new TypeReference<Map<String, FieldConfig>>() {}
            );

            return translateInternal(dataList, fieldConfigMap);
        } catch (Exception e) {
            logger.error("字典翻译失败", e);
            return dataList;
        }
    }

    /**
     * 根据字典类型获取字典项列表(带缓存)
     *
     * @param dictType 字典类型
     * @return 字典项列表
     */
    @Comment("根据字典类型获取字典项列表")
    public List<Map<String, Object>> getDictItems(
            @Comment("字典类型") String dictType) {
        if (dictType == null || dictType.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // 先从缓存获取
            List<DictItem> cachedItems = dictCache.get(dictType);
            if (cachedItems != null) {
                logger.debug("从缓存获取字典项: {}", dictType);
                return convertDictItemsToMap(cachedItems);
            }

            // 使用 AnylineService 查询
            Map<String, Object> params = new HashMap<>();
            params.put("type", dictType);
            List<Map<String, Object>> items = anylineService.maps(SQL_GET_DICT_ITEMS, params);

            if (items.isEmpty()) {
                logger.warn("未找到字典项: {}", dictType);
                return new ArrayList<>();
            }

            // 转换并缓存
            List<DictItem> dictItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Object value = item.get("value");
                String label = (String) item.get("label");
                dictItems.add(new DictItem(value, label));
            }

            dictCache.put(dictType, dictItems);
            logger.info("加载字典缓存: {} -> {} 项", dictType, dictItems.size());

            return items;
        } catch (Exception e) {
            logger.error("查询字典项失败: {}", dictType, e);
            return new ArrayList<>();
        }
    }

    /**
     * 翻译单个值
     *
     * @param dictType 字典类型
     * @param value    原始值
     * @return 翻译后的文本。未找到返回 null
     */
    @Comment("翻译单个值")
    public String translateValue(
            @Comment("字典类型") String dictType,
            @Comment("原始值") Object value) {
        if (dictType == null || value == null) {
            return null;
        }

        try {
            // 获取字典项(带缓存)
            List<DictItem> dictItems = getDictItemsCached(dictType);

            if (dictItems.isEmpty()) {
                logger.warn("未找到字典项: {}", dictType);
                return null;
            }

            // 查找匹配的值
            String sourceValueStr = String.valueOf(value);
            for (DictItem item : dictItems) {
                if (item.value != null) {
                    String configValueStr = String.valueOf(item.value);
                    if (sourceValueStr.equals(configValueStr)) {
                        return item.label;
                    }
                }
            }

            logger.debug("未找到匹配的字典项: {} -> {}", dictType, value);
            return null;
        } catch (Exception e) {
            logger.error("翻译值失败: {} -> {}", dictType, value, e);
            return null;
        }
    }

    /**
     * 根据字典类型自动翻译字段
     *
     * @param dataList 数据列表
     * @param fieldName 字段名
     * @param dictType  字典类型
     * @return 翻译后的数据列表
     */
    @Comment("根据字典类型自动翻译字段")
    public List<Map<String, Object>> translateByDictType(
            @Comment("数据列表") List<Map<String, Object>> dataList,
            @Comment("字段名") String fieldName,
            @Comment("字典类型") String dictType) {
        if (dataList == null || dataList.isEmpty() || fieldName == null || dictType == null) {
                return dataList;
        }

        try {
            // 获取字典项(带缓存)
            List<DictItem> dictItems = getDictItemsCached(dictType);

            if (dictItems.isEmpty()) {
                logger.warn("未找到字典项: {}", dictType);
                return dataList;
            }

            // 构建配置
            FieldConfig fieldConfig = new FieldConfig();
            fieldConfig.target = fieldName + "Name";
            fieldConfig.dicts = dictItems;

            Map<String, FieldConfig> configMap = new java.util.HashMap<>();
            configMap.put(fieldName, fieldConfig);

            return translateInternal(dataList, configMap);
        } catch (Exception e) {
            logger.error("自动翻译失败: {} -> {}", fieldName, dictType, e);
            return dataList;
        }
    }

    /**
     * 清除字典缓存
     */
    @Comment("清除字典缓存")
    public void clearCache() {
        int size = dictCache.size();
        dictCache.clear();
        logger.info("字典缓存已清除， 原缓存数量: {}", size);
    }

    /**
     * 刷新指定字典类型的缓存
     *
     * @param dictType 字典类型
     */
    @Comment("刷新指定字典类型的缓存")
    public void refreshCache(@Comment("字典类型") String dictType) {
        if (dictType != null) {
            List<DictItem> removed = dictCache.remove(dictType);
            if (removed != null) {
                logger.info("字典缓存已刷新: {} -> 移除 {} 项", dictType, removed.size());
            } else {
                logger.info("字典缓存已刷新: {} -> 无缓存", dictType);
            }
        }
    }

    /**
     * 内部翻译方法
     */
    private List<Map<String, Object>> translateInternal(
            List<Map<String, Object>> dataList,
            Map<String, FieldConfig> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            return dataList;
        }

        // 遍历数据列表进行翻译
        for (Map<String, Object> data : dataList) {
            if (data == null) {
                continue;
            }
            translateRow(data, configMap);
        }

        return dataList;
    }
    /**
     * 翻译单行数据
     */
    private void translateRow(Map<String, Object> data, Map<String, FieldConfig> configMap) {
        for (Map.Entry<String, FieldConfig> entry : configMap.entrySet()) {
            String sourceField = entry.getKey();
            FieldConfig config = entry.getValue();

            // 检查源字段是否存在
            if (!data.containsKey(sourceField)) {
                continue;
            }

            Object sourceValue = data.get(sourceField);
            if (sourceValue == null) {
                continue;
            }

            // 确定目标字段名
            String targetField = (config.target != null && !config.target.trim().isEmpty())
                    ? config.target
                    : sourceField + "Name";

            // 根据字典配置进行翻译
            String translatedText = translateValueByConfig(sourceValue, config.dicts);
            if (translatedText != null) {
                data.put(targetField, translatedText);
            }
        }
    }
    /**
     * 根据字典配置翻译值
     */
    private String translateValueByConfig(Object sourceValue, List<DictItem> dictConfig) {
        if (dictConfig == null || dictConfig.isEmpty()) {
            return null;
        }

        // 统一转为 String 进行比较，支持类型模糊匹配
        String sourceValueStr = String.valueOf(sourceValue);

        for (DictItem item : dictConfig) {
            if (item == null || item.value == null) {
                continue;
            }

            // 统一转为 String 进行比较
            String configValueStr = String.valueOf(item.value);
            if (sourceValueStr.equals(configValueStr)) {
                return item.label;
            }
        }

        return null;
    }
    /**
     * 获取字典项(带缓存,返回 DictItem 列表)
     */
    private List<DictItem> getDictItemsCached(String dictType) {
        if (dictType == null || dictType.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 先从缓存获取
        List<DictItem> cachedItems = dictCache.get(dictType);
        if (cachedItems != null) {
                return cachedItems;
        }

        // 从数据库查询
        Map<String, Object> params = new HashMap<>();
        params.put("type", dictType);
        List<Map<String, Object>> items = anylineService.maps(SQL_GET_DICT_ITEMS, params);

        if (items.isEmpty()) {
            logger.warn("未找到字典项: {}", dictType);
            return new ArrayList<>();
        }

        // 转换并缓存
        List<DictItem> dictItems = new ArrayList<>();
        for (Map<String, Object> item : items) {
            Object value = item.get("value");
            String label = (String) item.get("label");
            dictItems.add(new DictItem(value, label));
        }

        dictCache.put(dictType, dictItems);
        logger.info("加载字典缓存: {} -> {} 项", dictType, dictItems.size());

        return dictItems;
    }
    /**
     * 将 DictItem 列表转换为 Map 列表
     */
    private List<Map<String, Object>> convertDictItemsToMap(List<DictItem> dictItems) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (DictItem item : dictItems) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("value", item.value);
            map.put("label", item.label);
            result.add(map);
        }
        return result;
    }

    /**
     * 字段配置
     */
    public static class FieldConfig {
        public String target;
        public List<DictItem> dicts;
    }

    /**
     * 字典项(对外暴露,可在脚本中使用)
     */
    public static class DictItem {
        public Object value;
        public String label;

        public DictItem() {
        }

        public DictItem(Object value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}
