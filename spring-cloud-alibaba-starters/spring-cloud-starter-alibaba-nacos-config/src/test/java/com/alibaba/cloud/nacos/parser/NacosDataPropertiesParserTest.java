/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.nacos.parser;

import org.junit.Test;

import java.util.Map;

public class NacosDataPropertiesParserTest {

	String properties = "name=yihaomen-aaa\n" + "address=wuhan\n" + "\n" + "#App\n"
			+ "app.menus[0].title=Home\n" + "app.menus[0].name=Home\n"
			+ "app.menus[0].path=/\n" + "app.menus[1].title=Login\n"
			+ "app.menus[1].name=Login\n" + "app.menus[1].path=/login\n" + "\n"
			+ "app.compiler.timeout=5\n" + "app.compiler.output-folder=/temp/\n" + "\n"
			+ "app.error=/error/\n" + "\n" + "school=hangzhoudianzi \\\n" + "university";

	@Test
	public void test_properties_parser() throws Exception {
		AbstractNacosDataParser parser = new NacosDataPropertiesParser();
		Map<String, Object> map = parser.doParse(properties);
		System.out.println(map);
	}

}