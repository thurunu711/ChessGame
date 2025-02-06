/*
 * Copyright 2015-2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.testkit.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.rootCause;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.PreconditionViolationException;

/**
 * Tests for {@link TestExecutionResultConditions}.
 *
 * @since 1.11
 */
class TestExecutionResultConditionsTests {

	private static final String EXPECTED = "expected";

	private static final String UNEXPECTED = "unexpected";

	private static final Condition<Throwable> rootCauseCondition = rootCause(message(EXPECTED));

	@Test
	void rootCauseFailsForNullThrowable() {
		assertThatExceptionOfType(PreconditionViolationException.class)//
				.isThrownBy(() -> rootCauseCondition.matches(null))//
				.withMessage("Throwable must not be null");
	}

	@Test
	void rootCauseFailsForThrowableWithoutCause() {
		Throwable throwable = new Throwable();

		assertThatExceptionOfType(PreconditionViolationException.class)//
				.isThrownBy(() -> rootCauseCondition.matches(throwable))//
				.withMessage("Throwable does not have a cause");
	}

	@Test
	void rootCauseMatchesForDirectCauseWithExpectedMessage() {
		RuntimeException cause = new RuntimeException(EXPECTED);
		Throwable throwable = new Throwable(cause);

		assertThat(rootCauseCondition.matches(throwable)).isTrue();
	}

	@Test
	void rootCauseDoesNotMatchForDirectCauseWithDifferentMessage() {
		RuntimeException cause = new RuntimeException(UNEXPECTED);
		Throwable throwable = new Throwable(cause);

		assertThat(rootCauseCondition.matches(throwable)).isFalse();
	}

	@Test
	void rootCauseMatchesForRootCauseWithExpectedMessage() {
		RuntimeException rootCause = new RuntimeException(EXPECTED);
		RuntimeException intermediateCause = new RuntimeException("intermediate cause", rootCause);
		Throwable throwable = new Throwable(intermediateCause);

		assertThat(rootCauseCondition.matches(throwable)).isTrue();
	}

	@Test
	void rootCauseDoesNotMatchForRootCauseWithDifferentMessage() {
		RuntimeException rootCause = new RuntimeException(UNEXPECTED);
		RuntimeException intermediateCause = new RuntimeException("intermediate cause", rootCause);
		Throwable throwable = new Throwable(intermediateCause);

		assertThat(rootCauseCondition.matches(throwable)).isFalse();
	}

	@Test
	void rootCauseMatchesForRootCauseWithExpectedMessageAndSingleLevelRecursiveCauseChain() {
		RuntimeException rootCause = new RuntimeException(EXPECTED);
		Throwable throwable = new Throwable(rootCause);
		rootCause.initCause(throwable);

		assertThat(rootCauseCondition.matches(throwable)).isTrue();
	}

	@Test
	void rootCauseDoesNotMatchForRootCauseWithDifferentMessageAndSingleLevelRecursiveCauseChain() {
		RuntimeException rootCause = new RuntimeException(UNEXPECTED);
		Throwable throwable = new Throwable(rootCause);
		rootCause.initCause(throwable);

		assertThat(rootCauseCondition.matches(throwable)).isFalse();
	}

	@Test
	void rootCauseMatchesForRootCauseWithExpectedMessageAndDoubleLevelRecursiveCauseChain() {
		RuntimeException rootCause = new RuntimeException(EXPECTED);
		Exception intermediateCause = new Exception("intermediate cause", rootCause);
		Throwable throwable = new Throwable(intermediateCause);
		rootCause.initCause(throwable);

		assertThat(rootCauseCondition.matches(throwable)).isTrue();
	}

	@Test
	void rootCauseDoesNotMatchForRootCauseWithDifferentMessageAndDoubleLevelRecursiveCauseChain() {
		RuntimeException rootCause = new RuntimeException(UNEXPECTED);
		Exception intermediateCause = new Exception("intermediate cause", rootCause);
		Throwable throwable = new Throwable(intermediateCause);
		rootCause.initCause(throwable);

		assertThat(rootCauseCondition.matches(throwable)).isFalse();
	}

}
