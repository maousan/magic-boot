package org.ssssssss.magicapi.utils;

import org.apache.commons.lang3.StringUtils;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.core.model.PathMagicEntity;

import java.util.Objects;

public final class ScriptIdUtils {

	private ScriptIdUtils() {
	}

	public static String resolve(PathMagicEntity info) {
		return resolve(info, "");
	}

	public static String resolve(PathMagicEntity info, String prefix) {
		if (info instanceof ApiInfo apiInfo) {
			return resolveApi(apiInfo, prefix);
		}
		return resolveFunction(info);
	}

	public static String resolveApi(ApiInfo info, String prefix) {
		if (StringUtils.isNotBlank(info.getId())) {
			return info.getId();
		}
		String method = Objects.toString(info.getMethod(), "GET").toUpperCase();
		String path = PathUtils.replaceSlash(StringUtils.defaultIfBlank(prefix, "") + "/" + Objects.toString(info.getPath(), ""));
		return method + ":" + path;
	}

	public static String resolveFunction(PathMagicEntity info) {
		if (StringUtils.isNotBlank(info.getId())) {
			return info.getId();
		}
		return "FUNCTION:" + Objects.toString(info.getPath(), Objects.toString(info.getName(), "unknown"));
	}
}
