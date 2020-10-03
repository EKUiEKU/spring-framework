/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.junit.jupiter.nested;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit.jupiter.nested.BasicNestedTests.TopLevelConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.INHERIT;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;

/**
 * Integration tests that verify support for {@code @Nested} test classes
 * in conjunction with the {@link SpringExtension} in a JUnit Jupiter environment.
 *
 * <p>To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see ConstructorInjectionNestedTests
 * @see org.springframework.test.context.junit4.nested.NestedTestsWithSpringRulesTests
 */
@SpringJUnitConfig(TopLevelConfig.class)
class BasicNestedTests {

	private static final String FOO = "foo";
	private static final String BAR = "bar";

	@Autowired
	String foo;


	@Test
	void topLevelTest() {
		assertThat(foo).isEqualTo(FOO);
	}


	@Nested
	@SpringJUnitConfig(NestedConfig.class)
	class NestedTests {

		@Autowired(required = false)
		@Qualifier("foo")
		String localFoo;

		@Autowired
		String bar;


		@Test
		void test() {
			// In contrast to nested test classes running in JUnit 4, the foo
			// field in the outer instance should have been injected from the
			// test ApplicationContext for the outer instance.
			assertThat(foo).isEqualTo(FOO);
			assertThat(this.localFoo).as("foo bean should not be present").isNull();
			assertThat(this.bar).isEqualTo(BAR);
		}
	}

	@Nested
	@NestedTestConfiguration(INHERIT)
	class NestedTestCaseWithInheritedConfigTests {

		@Autowired(required = false)
		@Qualifier("foo")
		String localFoo;

		@Autowired
		String bar;


		@Test
		void test() {
			// Since the configuration is inherited, the foo field in the outer instance
			// and the bar field in the inner instance should both have been injected
			// from the test ApplicationContext for the outer instance.
			assertThat(foo).isEqualTo(FOO);
			assertThat(this.localFoo).isEqualTo(FOO);
			assertThat(this.bar).isEqualTo(FOO);
		}


		@Nested
		@NestedTestConfiguration(OVERRIDE)
		@SpringJUnitConfig(NestedConfig.class)
		class DoubleNestedWithOverriddenConfigTests {

			@Autowired(required = false)
			@Qualifier("foo")
			String localFoo;

			@Autowired
			String bar;


			@Test
			void test() {
				// In contrast to nested test classes running in JUnit 4, the foo
				// field in the outer instance should have been injected from the
				// test ApplicationContext for the outer instance.
				assertThat(foo).isEqualTo(FOO);
				assertThat(this.localFoo).as("foo bean should not be present").isNull();
				assertThat(this.bar).isEqualTo(BAR);
			}


			@Nested
			@NestedTestConfiguration(INHERIT)
			class TripleNestedWithInheritedConfigTests {

				@Autowired(required = false)
				@Qualifier("foo")
				String localFoo;

				@Autowired
				String bar;


				@Test
				@Disabled("not currently working")
				void test() {
					assertThat(foo).isEqualTo(FOO);
					assertThat(this.localFoo).as("foo bean should not be present").isNull();
					assertThat(this.bar).isEqualTo(BAR);
				}
			}
		}
	}

	// -------------------------------------------------------------------------

	@Configuration
	static class TopLevelConfig {

		@Bean
		String foo() {
			return FOO;
		}
	}

	@Configuration
	static class NestedConfig {

		@Bean
		String bar() {
			return BAR;
		}
	}

}