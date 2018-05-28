/*-
 * #%L
 * Elastic APM Java agent
 * %%
 * Copyright (C) 2018 the original author or authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package co.elastic.apm.api;

/**
 * If the agent is active, it injects the implementation from
 * co.elastic.apm.plugin.api.SpanInstrumentation
 * into this class.
 * <p>
 * Otherwise, this class is a noop.
 * </p>
 */
class SpanImpl implements Span {

    // co.elastic.apm.impl.transaction.Span
    private final Object span;

    SpanImpl(Object span) {
        this.span = span;
    }

    @Override
    public void setName(String name) {
        // co.elastic.apm.plugin.api.SpanInstrumentation$SetNameInstrumentation.setName
    }

    @Override
    public void setType(String type) {
        // co.elastic.apm.plugin.api.SpanInstrumentation$SetTypeInstrumentation.setType
    }

    @Override
    public void end() {
        // co.elastic.apm.plugin.api.SpanInstrumentation$EndInstrumentation.end
    }

}