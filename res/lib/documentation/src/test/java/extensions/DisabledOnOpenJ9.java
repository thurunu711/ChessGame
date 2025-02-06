/*
 * Copyright 2015-2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@DisabledIfSystemProperty(named = "java.vm.vendor", matches = ".*OpenJ9.*")
public @interface DisabledOnOpenJ9 {
}
