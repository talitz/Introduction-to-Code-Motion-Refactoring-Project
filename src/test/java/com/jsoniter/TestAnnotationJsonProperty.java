package com.jsoniter;

import com.jsoniter.annotation.JsonMissingProperties;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.fuzzy.StringIntDecoder;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestAnnotationJsonProperty extends TestCase {

    public static class TestObject1 {
        @JsonProperty(from = {"field-1"})
        public int field1;
    }

    public void test_rename() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field-1': 100}".replace('\'', '"'));
        TestObject1 obj = iter.read(TestObject1.class);
        assertEquals(100, obj.field1);
    }



    public static class TestObject2 {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        TestObject2 obj = iter.read(TestObject2.class);
        assertEquals(Arrays.asList("field1"), obj.missingProperties);
    }

    public static class TestObject3 {
        @JsonProperty(decoder = StringIntDecoder.class)
        public int field1;
    }

    public void test_property_decoder() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"100\"}");
        TestObject3 obj = iter.read(TestObject3.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject4 {
        @JsonProperty(decoder = StringIntDecoder.class)
        public Integer field1;
    }

    public void test_integer_property_decoder() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"100\"}");
        TestObject4 obj = iter.read(TestObject4.class);
        assertEquals(Integer.valueOf(100), obj.field1);
    }

    public static class TestObject5 {
        @JsonProperty(from = {"field_1", "field-1"})
        public int field1;
    }

    public void test_bind_from_multiple_names() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field-1\": 100, \"field-1\": 101}");
        TestObject5 obj = iter.read(TestObject5.class);
        assertEquals(101, obj.field1);
    }

    public static class TestObject6 {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties_not_missing() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        TestObject6 obj = iter.read(TestObject6.class);
        assertNull(obj.missingProperties);
        assertEquals(100, obj.field1);
    }

    public static class TestObject7 {
        @JsonProperty(implementation = LinkedList.class)
        public List<Integer> values;
    }

    public void test_specify_property() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"values\": [100]}");
        TestObject7 obj = iter.read(TestObject7.class);
        assertEquals(Arrays.asList(100), obj.values);
        assertEquals(LinkedList.class, obj.values.getClass());
    }
}
