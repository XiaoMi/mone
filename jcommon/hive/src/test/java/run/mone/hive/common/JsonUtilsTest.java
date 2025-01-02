package run.mone.hive.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JsonUtils} class.
 */
public class JsonUtilsTest {
    @Test
    public void testExtractValueSimpleObject() {
        String json = "{\"name\":\"John\",\"age\":30}";
        JsonObject jsonObject = JsonUtils.gson.fromJson(json, JsonObject.class);

        JsonElement nameElement = JsonUtils.extractValue(jsonObject, "name");
        assertEquals("John", nameElement.getAsString());

        JsonElement ageElement = JsonUtils.extractValue(jsonObject, "age");
        assertEquals(30, ageElement.getAsInt());
    }

    @Test

    public void testExtractValueNestedObject() {
        String json = "{\"person\":{\"name\":\"Alice\",\"address\":{\"city\":\"New York\"}}}";
        JsonObject jsonObject = JsonUtils.gson.fromJson(json, JsonObject.class);

        JsonElement nameElement = JsonUtils.extractValue(jsonObject, "person.name");
        assertEquals("Alice", nameElement.getAsString());

        JsonElement cityElement = JsonUtils.extractValue(jsonObject, "person.address.city");
        assertEquals("New York", cityElement.getAsString());
    }

    @Test
    public void testExtractValueArray() {
        String json = "{\"numbers\":[1,2,3],\"friends\":[{\"name\":\"Bob\"},{\"name\":\"Charlie\"}]}";
        JsonObject jsonObject = JsonUtils.gson.fromJson(json, JsonObject.class);

        JsonElement secondNumber = JsonUtils.extractValue(jsonObject, "numbers[1]");
        assertEquals(2, secondNumber.getAsInt());

        JsonElement firstFriendName = JsonUtils.extractValue(jsonObject, "friends[0].name");
        assertEquals("Bob", firstFriendName.getAsString());
    }

    @Test
    public void testExtractValueNotFound() {
        String json = "{\"data\":\"test\"}";
        JsonObject jsonObject = JsonUtils.gson.fromJson(json, JsonObject.class);

        JsonElement notExistElement = JsonUtils.extractValue(jsonObject, "nonexistent");
        assertNull(notExistElement);

        JsonElement deepNotExistElement = JsonUtils.extractValue(jsonObject, "data.nonexistent");
        assertNull(deepNotExistElement);
    }

    @Test
    public void testExtractValueInvalidExpression() {
        String json = "{\"key\":\"value\"}";
        JsonObject jsonObject = JsonUtils.gson.fromJson(json, JsonObject.class);

        assertThrows(IllegalArgumentException.class, () -> {
            JsonUtils.extractValue(jsonObject, "key[0]");
        });

        assertThrows(NumberFormatException.class, () -> {
            JsonUtils.extractValue(jsonObject, "[invalid]");
        });
    }
}