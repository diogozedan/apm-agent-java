/*-
 * #%L
 * Elastic APM Java agent
 * %%
 * Copyright (C) 2018 Elastic and contributors
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
package co.elastic.apm.report.serialize;

import co.elastic.apm.configuration.CoreConfiguration;
import com.dslplatform.json.JsonWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


class DslJsonSerializerTest {

    private DslJsonSerializer serializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        serializer = new DslJsonSerializer(new CoreConfiguration());
        objectMapper = new ObjectMapper();
    }

    @Test
    void serializeTags() {
        assertSoftly(softly -> {
            softly.assertThat(serializeTags(Map.of(".**", "foo.bar"))).isEqualTo(toJson(Map.of("___", "foo.bar")));
            softly.assertThat(serializeTags(Map.of("foo.bar", "baz"))).isEqualTo(toJson(Map.of("foo_bar", "baz")));
            softly.assertThat(serializeTags(Map.of("foo.bar.baz", "qux"))).isEqualTo(toJson(Map.of("foo_bar_baz", "qux")));
            softly.assertThat(serializeTags(Map.of("foo*bar*baz", "qux"))).isEqualTo(toJson(Map.of("foo_bar_baz", "qux")));
            softly.assertThat(serializeTags(Map.of("foo\"bar\"baz", "qux"))).isEqualTo(toJson(Map.of("foo_bar_baz", "qux")));
        });
    }

    @Test
    void testLimitStringValueLength() throws IOException {
        StringBuilder longValue = new StringBuilder(DslJsonSerializer.MAX_VALUE_LENGTH + 1);
        for (int i = 0; i < DslJsonSerializer.MAX_VALUE_LENGTH + 1; i++) {
            longValue.append('0');
        }

        serializer.jw.writeByte(JsonWriter.OBJECT_START);
        serializer.writeField("stringBuilder", longValue);
        serializer.writeField("string", longValue.toString());
        serializer.writeLastField("lastString", longValue.toString());
        serializer.jw.writeByte(JsonWriter.OBJECT_END);
        final JsonNode jsonNode = objectMapper.readTree(serializer.jw.toString());
        assertThat(jsonNode.get("stringBuilder").textValue()).hasSize(DslJsonSerializer.MAX_VALUE_LENGTH).endsWith("…");
        assertThat(jsonNode.get("string").textValue()).hasSize(DslJsonSerializer.MAX_VALUE_LENGTH).endsWith("…");
        assertThat(jsonNode.get("lastString").textValue()).hasSize(DslJsonSerializer.MAX_VALUE_LENGTH).endsWith("…");
    }

    private String toJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeTags(Map<String, String> tags) {
        serializer.serializeTags(tags);
        final String jsonString = serializer.jw.toString();
        serializer.jw.reset();
        return jsonString;
    }
}
