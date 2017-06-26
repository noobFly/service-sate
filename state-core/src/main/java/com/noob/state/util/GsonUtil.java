package com.noob.state.util;

import java.lang.reflect.Type;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonUtil {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(Multimap.class, new JsonSerializer<Multimap>() {

		@Override
		public JsonElement serialize(Multimap obj, Type type, JsonSerializationContext context) {
			return obj != null ? context.serialize(obj.asMap()) : null;
		}
	}).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static String toJson(Object obj) {

		return gson.toJson(obj);
	}

	public static <T> T fromJson(String json, Class<T> c) {
		return gson.fromJson(json, c);
	}

}
