package org.ssssssss.magicapi.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.MapOptions.WriteMode;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.map.MapLoader;
import org.redisson.api.map.MapWriter;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.redisson.codec.JsonJacksonCodec;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.redis.model.MagicDynamicRedisClient;
import org.ssssssss.script.annotation.Comment;
import org.ssssssss.script.functions.DynamicAttribute;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Redis 模块
 *
 * @author xuhaiyang
 */
@MagicModule("redis")
public class RedisModule implements DynamicAttribute<RedisModule, RedisModule> {

    private MagicDynamicRedisClient magicDynamicRedisClient;
    private RedissonClient redisson;

    public RedisModule(MagicDynamicRedisClient magicDynamicRedisClient) {
        this.magicDynamicRedisClient = magicDynamicRedisClient;
    }

    public RedisModule(RedissonClient redissonClient) {
        this.redisson = redissonClient;
    }

    /**
     * 获取 RedissonClient 实例
     *
     * @return RedissonClient 实例
     * @throws IllegalStateException RedissonClient 未初始化时抛出
     */
    private RedissonClient getRedissonClient() {
        if (redisson != null) {
            return redisson;
        }
        if (magicDynamicRedisClient != null && !magicDynamicRedisClient.isEmpty()) {
            return magicDynamicRedisClient.getDataSource("");
        }
        throw new IllegalStateException("RedissonClient 未初始化，请先配置 Redis 数据源");
    }

    /**
     * 数据源切换
     *
     * @param key 数据源 key
     * @return RedisModule 实例
     */
    @Override
    @Transient
    public RedisModule getDynamicAttribute(String key) {
        return magicDynamicRedisClient.getRedisModule(key);
    }

    @Comment("获取字符串类型缓存值")
    public String getString(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        RBucket<String> bucket = getRedissonClient().getBucket(redisKey, StringCodec.INSTANCE);
        return bucket.get();
    }

    @Comment("设置字符串类型缓存值")
    public void putString(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                          @Comment(name = "value", value = "存储的字符串信息") String value) {
        RBucket<String> bucket = getRedissonClient().getBucket(redisKey, StringCodec.INSTANCE);
        bucket.set(value);
    }

    @Comment("设置字符串类型缓存值并给值添加过期时间")
    public void putString(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                          @Comment(name = "value", value = "存储的字符串信息") String value,
                          @Comment(name = "expired", value = "存储的字符串过期时间 单位秒（-1：为永久有效）") long expired) {
        RBucket<String> bucket = getRedissonClient().getBucket(redisKey, StringCodec.INSTANCE);
        if (expired != -1) {
            bucket.set(value, expired <= 0 ? 300 : expired, TimeUnit.SECONDS);
        } else {
            bucket.set(value);
        }
    }

    @Comment("当 redisKey 不存在时，设置字符串类型缓存值并给值添加过期时间")
    public boolean putStringIfAbsent(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                     @Comment(name = "value", value = "存储的字符串信息") String value,
                                     @Comment(name = "expired", value = "存储的字符串过期时间 单位秒（-1：为永久有效）") long expired) {
        RBucket<String> bucket = getRedissonClient().getBucket(redisKey, StringCodec.INSTANCE);
        if (expired != -1) {
            return bucket.trySet(value, expired <= 0 ? 300 : expired, TimeUnit.SECONDS);
        }
        return bucket.trySet(value);
    }

    @Comment("当 redisKey 不存在时，设置字符串类型缓存值")
    public boolean putStringIfAbsent(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                     @Comment(name = "value", value = "存储的字符串信息") String value) {
        RBucket<String> bucket = getRedissonClient().getBucket(redisKey, StringCodec.INSTANCE);
        return bucket.trySet(value);
    }

    @Comment("获取对象类型缓存值")
    public <T> T getObject(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                           @Comment(name = "clazz", value = "对象类型") Class<T> clazz) {
        RBucket<T> bucket = getRedissonClient().getBucket(redisKey, JsonJacksonCodec.INSTANCE);
        return bucket.get();
    }

    @Comment("设置对象类型缓存值")
    public <T> void putObject(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                              @Comment(name = "value", value = "存储的对象信息") T value) {
        RBucket<T> bucket = getRedissonClient().getBucket(redisKey, JsonJacksonCodec.INSTANCE);
        bucket.set(value);
    }

    @Comment("设置对象类型缓存值并给值添加过期时间")
    public <T> void putObject(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                              @Comment(name = "value", value = "存储的对象信息") T value,
                              @Comment(name = "expired", value = "存储的对象过期时间 单位秒（-1：为永久有效）") long expired) {
        RBucket<T> bucket = getRedissonClient().getBucket(redisKey, JsonJacksonCodec.INSTANCE);
        if (expired != -1) {
            bucket.set(value, expired <= 0 ? 300 : expired, TimeUnit.SECONDS);
        } else {
            bucket.set(value);
        }
    }

    @Comment("当 redisKey 不存在时，设置对象类型缓存值并给值添加过期时间")
    public <T> boolean putObjectIfAbsent(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                         @Comment(name = "value", value = "存储的对象信息") T value,
                                         @Comment(name = "expired", value = "存储的对象过期时间 单位秒（-1：为永久有效）") long expired) {
        RBucket<T> bucket = getRedissonClient().getBucket(redisKey, JsonJacksonCodec.INSTANCE);
        if (expired != -1) {
            return bucket.trySet(value, expired <= 0 ? 300 : expired, TimeUnit.SECONDS);
        }
        return bucket.trySet(value);
    }

    @Comment("当 redisKey 不存在时，设置对象类型缓存值")
    public <T> boolean putObjectIfAbsent(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                         @Comment(name = "value", value = "存储的对象信息") T value) {
        RBucket<T> bucket = getRedissonClient().getBucket(redisKey, JsonJacksonCodec.INSTANCE);
        return bucket.trySet(value);
    }

    // ==================== Hash 类型操作方法 ====================

    @Comment("获取 Hash 对象")
    public RMap<String, Object> getHash(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        return getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
    }

    @Comment("Hash 设置单个字段")
    public void hashPut(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                        @Comment(name = "field", value = "字段名") String field,
                        @Comment(name = "value", value = "字段值") Object value) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        hash.put(field, value);
    }

    @Comment("Hash 设置多个字段")
    public void hashPutAll(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                           @Comment(name = "map", value = "字段 - 值 Map 对象") Map<String, Object> map) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        hash.putAll(map);
    }

    @Comment("Hash 获取单个字段值")
    public Object hashGet(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                          @Comment(name = "field", value = "字段名") String field) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        return hash.get(field);
    }

    @Comment("Hash 获取多个字段值")
    public Map<String, Object> hashMultiGet(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                            @Comment(name = "fields", value = "字段名集合") Collection<String> fields) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        Map<String, Object> result = new HashMap<>();
        for (String field : fields) {
            result.put(field, hash.get(field));
        }
        return result;
    }

    @Comment("Hash 获取所有字段值")
    public Map<String, Object> hashGetAll(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        return new HashMap<>(hash);
    }

    @Comment("Hash 删除字段")
    public Long hashDelete(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                           @Comment(name = "fields", value = "字段名集合") Collection<String> fields) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        long count = 0;
        for (String field : fields) {
            if (hash.remove(field) != null) {
                count++;
            }
        }
        return count;
    }

    @Comment("Hash 删除单个字段")
    public Long hashDeleteField(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                @Comment(name = "field", value = "字段名") String field) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        return hash.remove(field) != null ? 1L : 0L;
    }

    @Comment("Hash 判断字段是否存在")
    public boolean hashExists(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                              @Comment(name = "field", value = "字段名") String field) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        return hash.containsKey(field);
    }

    @Comment("Hash 获取所有字段名")
    public Set<String> hashFields(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        return hash.keySet();
    }

    @Comment("Hash 获取字段数量")
    public Long hashSize(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        return (long) hash.size();
    }

    @Comment("Hash 设置过期时间")
    public void hashExpire(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                           @Comment(name = "expired", value = "过期时间 单位秒") long expired) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        hash.expire(expired, TimeUnit.SECONDS);
    }

    @Comment("Hash 自增字段值")
    public Long hashIncrement(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                              @Comment(name = "field", value = "字段名") String field,
                              @Comment(name = "delta", value = "自增的值") long delta) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        Number result = (Number) hash.addAndGet(field, delta);
        return result.longValue();
    }

    @Comment("Hash 批量获取字段值（支持模糊匹配）")
    public Map<String, Object> hashGetByPattern(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                                @Comment(name = "pattern", value = "字段名匹配模式，如：user:*") String pattern) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        Set<Entry<String, Object>> entrySet = hash.entrySet(pattern);
        Map<String, Object> result = new HashMap<>();
        for (Entry<String, Object> entry : entrySet) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Comment("Hash 批量删除字段（支持模糊匹配）")
    public Long hashDeleteByPattern(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                    @Comment(name = "pattern", value = "字段名匹配模式，如：user:*") String pattern) {
        RMap<String, Object> hash = getRedissonClient().getMap(redisKey, JsonJacksonCodec.INSTANCE);
        Set<Entry<String, Object>> entrySet = hash.entrySet(pattern);
        int count = 0;
        for (Entry<String, Object> entry : entrySet) {
            hash.remove(entry.getKey());
            count++;
        }
        return (long) count;
    }

    @Comment("计数器自增（+1），并返回计算前的原值\n\n如果 key 不存在则按当前值为 0 计算")
    public long getAndIncrement(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                @Comment(name = "expired", value = "存储的字符串过期时间 单位秒（-1：为永久有效）") long expired) {
        RAtomicLong atomicLong = getRedissonClient().getAtomicLong(redisKey);
        long num = atomicLong.getAndIncrement();
        if (expired != -1) {
            atomicLong.expire(expired <= 0 ? 300 : expired, TimeUnit.SECONDS);
        }
        return num;
    }

    @Comment("计数器累加指定的值，并返回计算前的原值\n\n如果 key 不存在则按当前值为 0 计算")
    public long getAndIncrement(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                @Comment(name = "delta", value = "每次累加的数") long delta,
                                @Comment(name = "expired", value = "存储的字符串过期时间 单位秒（-1：为永久有效）") long expired) {
        RAtomicLong atomicLong = getRedissonClient().getAtomicLong(redisKey);
        long num = atomicLong.getAndAdd(delta);
        if (expired != -1) {
            atomicLong.expire(expired <= 0 ? 300 : expired, TimeUnit.SECONDS);
        }
        return num;
    }

    @Comment("获取 map 存储对象")
    public RMap<String, Object> getMap(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                       @Comment(name = "expired", value = "存储值的过期时间 单位秒（<= 0：为永久有效）") long expired) {
        RMap<String, Object> map = getRedissonClient().getMap(redisKey);
        if (!exists(redisKey) && expired > 0) {
            map.expire(expired, TimeUnit.SECONDS);
        }
        return map;
    }

    @Comment("保存或者更新 Map 数据")
    public void saveOrUpdateMap(@Comment(name = "rmap", value = "redis 的 rmap 对象") RMap<String, Object> rmap,
                                @Comment(name = "mapKey", value = "mapKey") String mapKey,
                                @Comment(name = "mapValue", value = "mapValue 可以是字符串、json 对象等") Object mapValue) {
        rmap.put(mapKey, mapValue);
    }

    @Comment("删除 map 数据")
    public void delMapByMapkey(@Comment(name = "rmap", value = "redis 的 rmap 对象") RMap<String, Object> rmap,
                               @Comment(name = "mapKeyPattern", value = "匹配的正则表达式") String mapKeyPattern) {
        Set<Entry<String, Object>> entrySet = rmap.entrySet(mapKeyPattern);
        for (Entry<String, Object> entry : entrySet) {
            rmap.remove(entry.getKey());
        }
    }

    @Comment("模糊查询，获取 Map 数据")
    public Map<String, Object> getMapByMapkey(@Comment(name = "rmap", value = "redis 的 rmap 对象") RMap<String, Object> rmap,
                                              @Comment(name = "mapKeyPattern", value = "匹配的正则表达式") String mapKeyPattern) {
        Set<Entry<String, Object>> entrySet = rmap.entrySet(mapKeyPattern);
        Map<String, Object> map = new HashMap<>();
        for (Entry<String, Object> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @Comment("获取带有单行数据有效器的 Map 对象")
    public RMapCache<String, Object> getMapCache(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        return getRedissonClient().getMapCache(redisKey);
    }

    @Comment("保存或者更新带有单行数据有效器的 Map 中的数据")
    public void saveOrUpdateMapCache(@Comment(name = "rmapCache", value = "redis 的 rmapCache 对象") RMapCache<String, Object> rmapCache,
                                     @Comment(name = "mapKey", value = "mapKey") String mapKey,
                                     @Comment(name = "mapValue", value = "mapValue 可以是字符串、json 对象等") Object mapValue,
                                     @Comment(name = "expired", value = "存储值的过期时间 单位秒（<= 0：为永久有效）") long expired) {
        if (expired > 0) {
            rmapCache.put(mapKey, mapValue, expired, TimeUnit.SECONDS);
        } else {
            rmapCache.put(mapKey, mapValue);
        }
    }

    @Comment("删除带有单行数据有效器的 Map 中的数据")
    public void delMapCacheByMapkey(@Comment(name = "rmapCache", value = "redis 的 rmapCache 对象") RMapCache<String, Object> rmapCache,
                                    @Comment(name = "mapKeyPattern", value = "匹配的正则表达式") String mapKeyPattern) {
        Set<Entry<String, Object>> entrySet = rmapCache.entrySet(mapKeyPattern);
        for (Entry<String, Object> entry : entrySet) {
            rmapCache.remove(entry.getKey());
        }
    }

    @Comment("模糊查询，获取带有单行数据有效器的 Map 中的数据")
    public Map<String, Object> getMapCacheByMapkey(@Comment(name = "rmapCache", value = "redis 的 rmapCache 对象") RMap<String, Object> rmapCache,
                                                   @Comment(name = "mapKeyPattern", value = "匹配的正则表达式") String mapKeyPattern) {
        Set<Entry<String, Object>> entrySet = rmapCache.entrySet(mapKeyPattern);
        Map<String, Object> map = new HashMap<>();
        for (Entry<String, Object> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @Comment("获取 List<String> 类型的全量数据")
    public RList<Object> getList(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                 @Comment(name = "expired", value = "存储值的过期时间 单位秒（<= 0：为永久有效）") long expired) {
        RList<Object> list = getRedissonClient().getList(redisKey);
        if (!exists(redisKey) && expired > 0) {
            list.expire(expired, TimeUnit.SECONDS);
        }
        return list;
    }

    @Comment("写入 List<Object> 类型数据")
    public void addList(@Comment(name = "rList", value = "rList 对象") RList<Object> rList,
                        @Comment(name = "value", value = "写入数据") Object value) {
        rList.add(value);
    }

    @Comment("删除 List 中的数据")
    public void delList(@Comment(name = "rList", value = "rList 对象") RList<Object> rList,
                        @Comment(name = "valuePattern", value = "匹配字符串状态下的 value，使用正则表达式\n\n如：.*yourPattern.*") String valuePattern) throws JsonProcessingException {
        Pattern regex = Pattern.compile(valuePattern);
        Iterator<Object> iterator = rList.iterator();
        while (iterator.hasNext()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String element = objectMapper.writeValueAsString(iterator.next());
            if (regex.matcher(element).matches()) {
                iterator.remove();
            }
        }
    }

    @Comment("模糊查询 List 中的数据")
    public List<Object> getListByValStr(@Comment(name = "rList", value = "rList 对象") RList<Object> rList,
                                        @Comment(name = "valuePattern", value = "匹配字符串状态下的 value，使用正则表达式\n\n如：.*yourPattern.*") String valuePattern) throws JsonProcessingException {
        Pattern regex = Pattern.compile(valuePattern);
        List<Object> result = new ArrayList<>();
        Iterator<Object> iterator = rList.iterator();
        while (iterator.hasNext()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object elem = iterator.next();
            String element = objectMapper.writeValueAsString(elem);
            if (regex.matcher(element).matches()) {
                result.add(elem);
            }
        }
        return result;
    }

    @Comment("判断缓存是否存在")
    public boolean exists(@Comment(name = "redisKey", value = "redisKey") String redisKey) {
        return getRedissonClient().getBucket(redisKey).isExists();
    }

    @Comment("获取不含 score 的 Set")
    public RSet<Object> getSet(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                               @Comment(name = "expired", value = "value 值的过期时间（<= 0：为永久有效）") long expired) {
        RSet<Object> setObj = getRedissonClient().getSet(redisKey);
        if (expired > 0) {
            setObj.expire(expired, TimeUnit.SECONDS);
        }
        return setObj;
    }

    @Comment("写入不含 score 的 Set<Object> 数据")
    public void addSet(@Comment(name = "rSet", value = "不含 score 的 RSet 对象") RSet<Object> rSet,
                       @Comment(name = "value", value = "写入的字符串信息") Object value) {
        rSet.add(value);
    }

    @Comment("删除不含 score 的 Set<Object> 数据")
    public void delSet(@Comment(name = "rSet", value = "不含 score 的 RSet 对象") RSet<Object> rSet,
                       @Comment(name = "valuePattern", value = "匹配字符串状态下的 value，使用正则表达式\n\n如：.*yourPattern.*") String valuePattern) throws JsonProcessingException {
        Pattern regex = Pattern.compile(valuePattern);
        Iterator<Object> iterator = rSet.iterator();
        while (iterator.hasNext()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String element = objectMapper.writeValueAsString(iterator.next());
            if (regex.matcher(element).matches()) {
                iterator.remove();
            }
        }
    }

    @Comment("模糊查询不含 score 的 Set 中的数据")
    public Set<Object> getSetByValStr(@Comment(name = "rSet", value = "不含 score 的 RSet 对象") RSet<Object> rSet,
                                      @Comment(name = "valuePattern", value = "匹配字符串状态下的 value，使用正则表达式\n\n如：.*yourPattern.*") String valuePattern) throws JsonProcessingException {
        Pattern regex = Pattern.compile(valuePattern);
        Set<Object> result = new HashSet<>();
        Iterator<Object> iterator = rSet.iterator();
        while (iterator.hasNext()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object elem = iterator.next();
            String element = objectMapper.writeValueAsString(elem);
            if (regex.matcher(element).matches()) {
                result.add(elem);
            }
        }
        return result;
    }

    @Comment("获取含有 score 的 Set（默认升序）")
    public RScoredSortedSet<Object> getScoreSet(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                                @Comment(name = "expired", value = "value 值的过期时间（<= 0：为永久有效）") long expired) {
        RScoredSortedSet<Object> setObj = getRedissonClient().getScoredSortedSet(redisKey);
        if (expired > 0) {
            setObj.expire(expired, TimeUnit.SECONDS);
        }
        return setObj;
    }

    @Comment("写入含有 score 的 Set<Object> 数据")
    public void addScoreSet(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSet,
                            @Comment(name = "score", value = "用于排序的 double 类型数据") double score,
                            @Comment(name = "value", value = "写入的字符串信息") Object value) {
        rSet.add(score, value);
    }

    @Comment("删除含有 score 的 Set<Object> 数据")
    public void delScoreSet(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSet,
                            @Comment(name = "valuePattern", value = "匹配字符串状态下的 value，使用正则表达式\n\n如：.*yourPattern.*") String valuePattern) throws JsonProcessingException {
        Pattern regex = Pattern.compile(valuePattern);
        Iterator<Object> iterator = rSet.iterator();
        while (iterator.hasNext()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String element = objectMapper.writeValueAsString(iterator.next());
            if (regex.matcher(element).matches()) {
                iterator.remove();
            }
        }
    }

    @Comment("模糊查询含有 score 的 Set 中的数据")
    public Set<Object> getSetByValStr(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSet,
                                      @Comment(name = "valuePattern", value = "匹配字符串状态下的 value，使用正则表达式\n\n如：.*yourPattern.*") String valuePattern) throws JsonProcessingException {
        Pattern regex = Pattern.compile(valuePattern);
        Set<Object> result = new HashSet<>();
        Iterator<Object> iterator = rSet.iterator();
        while (iterator.hasNext()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object elem = iterator.next();
            String element = objectMapper.writeValueAsString(elem);
            if (regex.matcher(element).matches()) {
                result.add(elem);
            }
        }
        return result;
    }

    @Comment("获取 score 升序后的 Set<Object>")
    public List<Object> getScoreSetByAscScore(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSet) {
        return rSet.stream()
                .sorted((e1, e2) -> Double.compare(rSet.getScore(e1), rSet.getScore(e2)))
                .collect(Collectors.toList());
    }

    @Comment("获取 score 降序后的 Set<Object>")
    public List<Object> getScoreSetByDescScore(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSet) {
        return rSet.stream()
                .sorted((e1, e2) -> Double.compare(rSet.getScore(e2), rSet.getScore(e1)))
                .collect(Collectors.toList());
    }

    @Comment("按照 score 的数值升序排序，后获取区间索引内的全部 Set<Object> 数据")
    public Collection<ScoredEntry<Object>> getAscScoreSetByBetweenScore(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSortedSet,
                                                                        @Comment(name = "startIndex", value = "value 值的起始索引，即：下标") int startIndex,
                                                                        @Comment(name = "endIndex", value = "value 值的结束索引，即：下标；-1：标识到最后") int endIndex) {
        return rSortedSet.entryRange(startIndex, endIndex);
    }

    @Comment("按照 score 的数值降序排序，后获取区间索引内的全部 Set<Object> 数据")
    public Collection<ScoredEntry<Object>> getDescScoreSetByBetweenScore(@Comment(name = "rSet", value = "含有 score 的 RSet 对象") RScoredSortedSet<Object> rSortedSet,
                                                                         @Comment(name = "startIndex", value = "value 值的起始索引，即：下标") int startIndex,
                                                                         @Comment(name = "endIndex", value = "value 值的结束索引，即：下标；-1：标识到最后") int endIndex) {
        return rSortedSet.entryRangeReversed(startIndex, endIndex);
    }

    private final ThreadPoolExecutor threadPoolExecutor;

    static {
    }

    {
        final AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("Redis-Topic-" + num.getAndIncrement());
                    return thread;
                });
    }

    @Comment("向 topic 中写入数据")
    public void publish(@Comment(name = "topicName", value = "topicName") String topicName,
                        @Comment(name = "data", value = "写入 topic 的数据") String data) throws Throwable {
        RMap<String, String> topicNamesObj = getRedissonClient().getMap("topicNames", StringCodec.INSTANCE);
        Set<Entry<String, String>> entrySet = topicNamesObj.entrySet("*___" + topicName + "*");
        int i = 0;
        for (Entry<String, String> entry : entrySet) {
            i++;
        }
        if (i > 0) {
            RTopic topic = getRedissonClient().getTopic(topicName);
            topic.publish(data);
        } else {
            throw new Throwable("CUSTOM:当前 topicName（" + topicName + "）未被监听，请您先启动监听服务！");
        }
    }

    @Comment("取消 topicName 的指定索引订阅")
    public void delTopicListener(@Comment(name = "index", value = "订阅信息的唯一身份索引") String index,
                                 @Comment(name = "topicName", value = "topicName") String topicName) {
        RMap<String, String> topicNamesObj = getRedissonClient().getMap("topicNames", StringCodec.INSTANCE);
        Object lid = topicNamesObj.get(index + "___" + topicName);
        if (lid != null) {
            RTopic topic = getRedissonClient().getTopic(topicName);
            topic.removeListener(Integer.parseInt(lid.toString()));
            topicNamesObj.remove(index + "___" + topicName);
        }
    }

    @Comment("取消 topicName 的全部订阅")
    public void delTopicAllListener(@Comment(name = "topicName", value = "topicName") String topicName) {
        RTopic topic = getRedissonClient().getTopic(topicName);
        topic.removeAllListeners();
        RMap<String, String> topicNamesObj = getRedissonClient().getMap("topicNames", StringCodec.INSTANCE);
        Set<Entry<String, String>> entrySet = topicNamesObj.entrySet("*___" + topicName + "*");
        for (Entry<String, String> entry : entrySet) {
            topicNamesObj.remove(entry.getKey());
        }
    }

    @Comment("订阅 topic")
    public void listenerTopicName(@Comment(name = "index", value = "订阅信息的唯一身份索引") String index,
                                  @Comment(name = "topicName", value = "topicName") String topicName,
                                  @Comment(name = "redisConsumerCallback", value = "回调函数;\n\n如：(topicName,content)->{print(content)}") RedisConsumerCallback redisConsumerCallback) {
        RMap<String, String> topicNamesObj = getRedissonClient().getMap("topicNames", StringCodec.INSTANCE);
        Object lid = topicNamesObj.get(index + "___" + topicName);
        if (lid != null) {
            delTopicListener(index, topicName);
        }
        threadPoolExecutor.execute(() -> {
            RTopic topic = getRedissonClient().getTopic(topicName);
            int idx = topic.addListener(String.class, (charSequence, msg) -> {
                try {
                    redisConsumerCallback.processMessage(topicName, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            topicNamesObj.put(index + "___" + topicName, idx + "");
        });
    }

    @Comment("创建限流器，注意如果名称相同则返回第一次创建的对象")
    public void createRateLimiter(@Comment(name = "name", value = "限流器名称") String name,
                                  @Comment(name = "rateInterval", value = "限速的间隔大小") int rateInterval,
                                  @Comment(name = "rateUnit", value = "限速的单位 (s:秒，m:分，h:小时，d:天)") String rateUnit) {
        RRateLimiter rateLimiter = getRedissonClient().getRateLimiter(name);
        switch (rateUnit) {
            case "s":
                rateLimiter.trySetRate(RateType.OVERALL, rateInterval, 1, RateIntervalUnit.SECONDS);
                break;
            case "m":
                rateLimiter.trySetRate(RateType.OVERALL, rateInterval, 1, RateIntervalUnit.MINUTES);
                break;
            case "h":
                rateLimiter.trySetRate(RateType.OVERALL, rateInterval, 1, RateIntervalUnit.HOURS);
                break;
            case "d":
                rateLimiter.trySetRate(RateType.OVERALL, rateInterval, 1, RateIntervalUnit.DAYS);
                break;
            default:
                break;
        }
    }

    @Comment("删除限流器")
    public boolean deleteRateLimiter(@Comment(name = "name", value = "限流器名称") String name) {
        RRateLimiter rateLimiter = getRedissonClient().getRateLimiter(name);
        return rateLimiter.delete();
    }

    @Comment("根据限流器，调用指定回调逻辑")
    public Object accessLimitedMethod(@Comment(name = "name", value = "限流器名称") String name,
                                      @Comment(name = "rateLimiterCallback", value = "回调函数;\n\n如：()->{...}") RateLimiterCallback rateLimiterCallback) throws Exception {
        RRateLimiter rateLimiter = getRedissonClient().getRateLimiter(name);
        if (rateLimiter.tryAcquire()) {
            return rateLimiterCallback.exec();
        } else {
            return new JsonBean<>(403, "服务过于繁忙，请稍后重试！");
        }
    }

    @Comment("同步数据到缓存中使用")
    public RLocalCachedMap<String, Object> syncData2Cache(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                                          @Comment(name = "keys", value = "()->{获取被加载表全主键集合并返回}") RedisHander.Keys keys,
                                                          @Comment(name = "rowDataByKey", value = "(key)->{根据主键值获取一条数据信息并返回}") RedisHander.RowDataByKey rowDataByKey) throws Exception {
        MapLoader<String, Object> mapLoader = new MapLoader<String, Object>() {
            @Override
            public Iterable<String> loadAllKeys() {
                return keys.getKeys();
            }

            @Override
            public Object load(String key) {
                return rowDataByKey.getRowDataByKey(key);
            }
        };
        LocalCachedMapOptions<String, Object> options = LocalCachedMapOptions.<String, Object>defaults().loader(mapLoader);
        return getRedissonClient().getLocalCachedMap(redisKey, options);
    }

    @Comment("同步数据给外部数据库使用")
    public RMap<String, Object> syncCacheData2External(@Comment(name = "redisKey", value = "redisKey") String redisKey,
                                                       @Comment(name = "insert", value = "(key,val)->{有新的数据 key、value 插入缓存}") RedisHander.Insert insert,
                                                       @Comment(name = "remove", value = "(key)->{有数据被删除，主键是 key}") RedisHander.Remove remove) throws Exception {
        MapWriter<String, Object> mapWriter = new MapWriter<String, Object>() {
            @Override
            public void write(Map<String, Object> map) {
                for (Entry<String, Object> entry : map.entrySet()) {
                    insert.insert(entry.getKey(), entry.getValue());
                }
            }

            @Override
            public void delete(Collection<String> keys) {
                for (String key : keys) {
                    remove.remove(key);
                }
            }
        };
        LocalCachedMapOptions<String, Object> options = LocalCachedMapOptions.<String, Object>defaults()
                .writer(mapWriter)
                .writeMode(WriteMode.WRITE_THROUGH);
        return getRedissonClient().getLocalCachedMap(redisKey, options);
    }

    @Comment("获取所有符合正则表达式的 keys 的缓存数据")
    public Set<String> getKeys(@Comment(name = "prefix", value = "匹配 redisKey 字符串的正则表达式") String prefix) {
        Iterable<String> keysByPattern = getRedissonClient().getKeys().getKeysByPattern(prefix);
        Set<String> keys = new HashSet<>();
        for (String s : keysByPattern) {
            keys.add(s);
        }
        return keys;
    }

    @Comment("获取一把锁")
    public RLock getRedisLock(String key) {
        return getRedissonClient().getLock(key);
    }

    @Comment("尝试获取分布式锁（非阻塞，获取不到立即返回 false）\n\n"
            + "基于 Redisson RLock，获取成功后务必在同一线程调用 unlock(key) 释放。"
            + "leaseTime 超时后锁自动释放，避免持锁线程崩溃导致死锁。")
    public boolean tryLock(@Comment(name = "key", value = "锁键") String key,
                           @Comment(name = "millis", value = "锁过期时间（毫秒），超时自动释放") long millis) {
        RLock lock = getRedissonClient().getLock(key);
        try {
            return lock.tryLock(0, millis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Comment("释放分布式锁\n\n"
            + "只释放当前线程持有的锁，非持有者调用会抛 IllegalMonitorStateException（已捕获忽略）。")
    public boolean unlock(@Comment(name = "key", value = "锁键") String key) {
        RLock lock = getRedissonClient().getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            return true;
        }
        return false;
    }

    @Comment("移除缓存")
    public void remove(String redisKey) {
        getRedissonClient().getBucket(redisKey).delete();
    }
}
