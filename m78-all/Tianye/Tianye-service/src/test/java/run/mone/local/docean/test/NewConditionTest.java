package run.mone.local.docean.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.fsm.bo.NewCondition;
import run.mone.local.docean.fsm.bo.Operator;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @author wmin
 * @date 2024/9/5
 */
public class NewConditionTest {

    @Test
    void testEvaluatePrimitiveEquals() {
        JsonElement value = new JsonPrimitive("test");
        JsonElement otherValue = new JsonPrimitive("test");
        NewCondition condition = new NewCondition(value, Operator.EQUALS, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluatePrimitiveLength() {
        JsonElement value = new JsonPrimitive("test");
        JsonElement otherValue = new JsonPrimitive("1");
        NewCondition condition = new NewCondition(value, Operator.STRING_LENGTH_GREATER_THAN, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluatePrimitiveNotEquals() {
        JsonElement value = new JsonPrimitive("test");
        JsonElement otherValue = new JsonPrimitive("not_test");
        NewCondition condition = new NewCondition(value, Operator.NOT_EQUALS, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluatePrimitiveGreaterThan() {
        JsonElement value = new JsonPrimitive(10);
        JsonElement otherValue = new JsonPrimitive(5);
        NewCondition condition = new NewCondition(value, Operator.GREATER_THAN, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluatePrimitiveLessThan() {
        JsonElement value = new JsonPrimitive(5);
        JsonElement otherValue = new JsonPrimitive(10);
        NewCondition condition = new NewCondition(value, Operator.LESS_THAN, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluatePrimitiveContains() {
        JsonElement value = new JsonPrimitive("hello world");
        JsonElement otherValue = new JsonPrimitive("world");
        NewCondition condition = new NewCondition(value, Operator.CONTAINS, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluatePrimitiveIsEmpty() {
        JsonElement value = new JsonPrimitive("");
        NewCondition condition = new NewCondition(value, Operator.IS_EMPTY, null);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateArrayLengthEquals() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(1));
        array.add(new JsonPrimitive(2));
        JsonElement otherValue = new JsonPrimitive(2);
        NewCondition condition = new NewCondition(array, Operator.ARRAY_LENGTH_EQUALS, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateArrayLengthGreaterThan() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(1));
        array.add(new JsonPrimitive(2));
        JsonElement otherValue = new JsonPrimitive(1);
        NewCondition condition = new NewCondition(array, Operator.ARRAY_LENGTH_GREATER_THAN, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateArrayLengthLessThan() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(1));
        JsonElement otherValue = new JsonPrimitive(2);
        NewCondition condition = new NewCondition(array, Operator.ARRAY_LENGTH_LESS_THAN, otherValue);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateObjectEquals() {
        JsonObject obj1 = new JsonObject();
        obj1.addProperty("key", "value");
        JsonObject obj2 = new JsonObject();
        obj2.addProperty("key", "value");
        NewCondition condition = new NewCondition(obj1, Operator.EQUALS, obj2);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateObjectNotEquals() {
        JsonObject obj1 = new JsonObject();
        obj1.addProperty("key", "value1");
        JsonObject obj2 = new JsonObject();
        obj2.addProperty("key", "value2");
        NewCondition condition = new NewCondition(obj1, Operator.NOT_EQUALS, obj2);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateObjectIsEmpty() {
        JsonObject obj = new JsonObject();
        NewCondition condition = new NewCondition(obj, Operator.IS_EMPTY, null);
        assertTrue(condition.evaluate());
    }

    @Test
    void testEvaluateObjectIsNotEmpty() {
        JsonObject obj = new JsonObject();
        obj.addProperty("key", "value");
        NewCondition condition = new NewCondition(obj, Operator.IS_NOT_EMPTY, null);
        assertTrue(condition.evaluate());
    }
}
