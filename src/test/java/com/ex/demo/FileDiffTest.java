package com.ex.demo;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.map.MapUtil;

public class FileDiffTest {

	public static void main(String[] args) throws IOException {

		File refDir = new File("C:\\Users\\Administrator\\Desktop\\v1.0");
		File currentDir = new File("C:\\\\Users\\\\Administrator\\\\Desktop\\\\v1.1");

		Map<String, List<String>> increments = FileDiffUtils.calculateIncrements(refDir, currentDir);
		MapUtil.defaultIfEmpty(increments, Collections.emptyMap()).forEach((k, v) -> {
			System.out.println(k + "：" + v);
		});
	}
}
