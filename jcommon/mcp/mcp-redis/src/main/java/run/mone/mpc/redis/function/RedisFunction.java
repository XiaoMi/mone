
package run.mone.mpc.redis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Data
@Slf4j
public class RedisFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "redisOperation";

    private String desc = "Redis operations including string operations, list operations, set operations, hash operations, and key operations";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["set", "get", "del", "exists", "expire", "ttl", "lpush", "rpush", "lpop", "rpop", "lrange", "sadd", "srem", "smembers", "sismember", "hset", "hget", "hdel", "hgetall", "keys"],
                        "description":"The operation to perform on Redis"
                    },
                    "key": {
                        "type": "string",
                        "description":"The key to operate on"
                    },
                    "value": {
                        "type": ["string", "array", "object"],
                        "description":"The value to set or push"
                    },
                    "field": {
                        "type": "string",
                        "description":"The field name for hash operations"
                    },
                    "start": {
                        "type": "integer",
                        "description":"The start index for list range operation"
                    },
                    "end": {
                        "type": "integer",
                        "description":"The end index for list range operation"
                    },
                    "seconds": {
                        "type": "integer",
                        "description":"The number of seconds for expiration"
                    },
                    "pattern": {
                        "type": "string",
                        "description":"The pattern for keys operation"
                    }
                },
                "required": ["operation", "key"]
            }
            """;

    private JedisPool jedisPool;

    public RedisFunction() {
        this.jedisPool = new JedisPool("localhost", 6379);
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String key = (String) arguments.get("key");

        log.info("operation: {} key: {}", operation, key);

        try (Jedis jedis = jedisPool.getResource()) {
            String result = switch (operation) {
                case "set" -> setOperation(jedis, key, (String) arguments.get("value"));
                case "get" -> getOperation(jedis, key);
                case "del" -> delOperation(jedis, key);
                case "exists" -> existsOperation(jedis, key);
                case "expire" -> expireOperation(jedis, key, (Integer) arguments.get("seconds"));
                case "ttl" -> ttlOperation(jedis, key);
                case "lpush", "rpush" -> listPushOperation(jedis, operation, key, (List<String>) arguments.get("value"));
                case "lpop", "rpop" -> listPopOperation(jedis, operation, key);
                case "lrange" -> lrangeOperation(jedis, key, (Integer) arguments.get("start"), (Integer) arguments.get("end"));
                case "sadd" -> saddOperation(jedis, key, (List<String>) arguments.get("value"));
                case "srem" -> sremOperation(jedis, key, (List<String>) arguments.get("value"));
                case "smembers" -> smembersOperation(jedis, key);
                case "sismember" -> sismemberOperation(jedis, key, (String) arguments.get("value"));
                case "hset" -> hsetOperation(jedis, key, (Map<String, String>) arguments.get("value"));
                case "hget" -> hgetOperation(jedis, key, (String) arguments.get("field"));
                case "hdel" -> hdelOperation(jedis, key, (List<String>) arguments.get("field"));
                case "hgetall" -> hgetallOperation(jedis, key);
                case "keys" -> keysOperation(jedis, (String) arguments.get("pattern"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String setOperation(Jedis jedis, String key, String value) {
        return jedis.set(key, value);
    }

    private String getOperation(Jedis jedis, String key) {
        return jedis.get(key);
    }

    private String delOperation(Jedis jedis, String key) {
        return String.valueOf(jedis.del(key));
    }

    private String existsOperation(Jedis jedis, String key) {
        return String.valueOf(jedis.exists(key));
    }

    private String expireOperation(Jedis jedis, String key, int seconds) {
        return String.valueOf(jedis.expire(key, seconds));
    }

    private String ttlOperation(Jedis jedis, String key) {
        return String.valueOf(jedis.ttl(key));
    }

    private String listPushOperation(Jedis jedis, String operation, String key, List<String> values) {
        long result = operation.equals("lpush") ? jedis.lpush(key, values.toArray(new String[0])) : jedis.rpush(key, values.toArray(new String[0]));
        return String.valueOf(result);
    }

    private String listPopOperation(Jedis jedis, String operation, String key) {
        return operation.equals("lpop") ? jedis.lpop(key) : jedis.rpop(key);
    }

    private String lrangeOperation(Jedis jedis, String key, int start, int end) {
        return jedis.lrange(key, start, end).toString();
    }

    private String saddOperation(Jedis jedis, String key, List<String> values) {
        return String.valueOf(jedis.sadd(key, values.toArray(new String[0])));
    }

    private String sremOperation(Jedis jedis, String key, List<String> values) {
        return String.valueOf(jedis.srem(key, values.toArray(new String[0])));
    }

    private String smembersOperation(Jedis jedis, String key) {
        return jedis.smembers(key).toString();
    }

    private String sismemberOperation(Jedis jedis, String key, String value) {
        return String.valueOf(jedis.sismember(key, value));
    }

    private String hsetOperation(Jedis jedis, String key, Map<String, String> hash) {
        return String.valueOf(jedis.hset(key, hash));
    }

    private String hgetOperation(Jedis jedis, String key, String field) {
        return jedis.hget(key, field);
    }

    private String hdelOperation(Jedis jedis, String key, List<String> fields) {
        return String.valueOf(jedis.hdel(key, fields.toArray(new String[0])));
    }

    private String hgetallOperation(Jedis jedis, String key) {
        return jedis.hgetAll(key).toString();
    }

    private String keysOperation(Jedis jedis, String pattern) {
        Set<String> keys = jedis.keys(pattern);
        return keys.toString();
    }
}
