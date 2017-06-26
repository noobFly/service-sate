package com.noob.state.utils;

import java.util.Collection;
import java.util.Optional;

public class CommonUtil {

	/**
	 * 获取对象
	 */
	public static <T> T getObjFromOptional(Optional<T> optional) {
		return optional != null && optional.isPresent() ? optional.get() : null;
	}

	public static <T> boolean notEmpty(Collection<T> collection) {
		return collection != null && collection.size() > 0;
	}
}
