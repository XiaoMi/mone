package run.mone.moon.utils;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GsonUtil {

	private static final Gson gson;

	/**
	 * 工具类构造方法私有化
	 */
	private GsonUtil() {
	}

	static {
		gson = new GsonBuilder()
//				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) //支持下划线自动转驼峰
				.addSerializationExclusionStrategy(new GsonExclusionStrategy())
				.addDeserializationExclusionStrategy(new GsonExclusionStrategy())
				.setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
				.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
				.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {
					@Override
					public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
							throws JsonParseException {
						if (json.getAsString().isEmpty()) {
							return null;
						} else {
							return json.getAsInt();
						}
					}
				}).registerTypeAdapter(Long.class, new JsonDeserializer<Long>() {
					@Override
					public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
							throws JsonParseException {
						if (json.getAsString().isEmpty()) {
							return null;
						} else {
							return json.getAsLong();
						}
					}
				}).create();
	}

	public static String toJson(Object o) {
		if (o == null) {
			return null;
		}
		return gson.toJson(o);
	}

	public static JsonElement toJsonTree(Object o) {
		return gson.toJsonTree(o);
	}

	public static JsonElement toJsonTree(Object o, Type type) {
		return gson.toJsonTree(o, type);
	}

	public static <T> T fromJson(String jsonStr, Class<T> clazz) {
		return gson.fromJson(jsonStr, clazz);
	}

	public static <T> T fromJson(byte[] jsonBytes, Class<T> clazz) {
		return fromJson(new String(jsonBytes), clazz);
	}

	public static <T> T fromJson(String jsonStr, Type type) {
		return gson.fromJson(jsonStr, type);
	}

	public static <T> T fromJson(byte[] jsonBytes, Type type) {
		return fromJson(new String(jsonBytes), type);
	}

	/**
	 * 将json反序列化转成list
	 *
	 * @param json json 字符串
	 * @param cls  需要转化为列表类型的class
	 * @param <T>  元素类型
	 * @return T类型对象列表
	 */
	public static <T> List<T> fromListJson(String json, Class<T> cls) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		List<T> list = new ArrayList<>();
		JsonElement jsonElement = JsonParser.parseString(json);
		if (jsonElement == null) {
			return null;
		}
		JsonArray array = jsonElement.getAsJsonArray();
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, cls));
		}
		return list;
	}

	public static <T> List<T> formatJsonList(String json, Class<T[]> clazz) {
		T[] array = gson.fromJson(json, clazz);
		return Arrays.asList(array);
	}

}
