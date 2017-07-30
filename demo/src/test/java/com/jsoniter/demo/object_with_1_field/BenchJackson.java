package com.jsoniter.demo.object_with_1_field;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
Benchmark            Mode  Cnt        Score        Error  Units
BenchJackson.deser  thrpt    5  8159400.294 ± 689022.762  ops/s
BenchJackson.ser    thrpt    5  9580832.217 ± 469534.662  ops/s
 */
@State(Scope.Thread)
public class BenchJackson {

    private ObjectMapper objectMapper;
    private TypeReference<TestObject> typeReference;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] testJSON;
    private TestObject testObject;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new AfterburnerModule());
        typeReference = new TypeReference<TestObject>() {
        };
        byteArrayOutputStream = new ByteArrayOutputStream();
        testJSON = TestObject.createTestJSON();
        testObject = TestObject.createTestObject();
    }

    @Benchmark
    public void ser(Blackhole bh) throws IOException {
        byteArrayOutputStream.reset();
        objectMapper.writeValue(byteArrayOutputStream, testObject);
        bh.consume(byteArrayOutputStream);
    }


    @Benchmark
    public void deser(Blackhole bh) throws IOException {
        bh.consume(objectMapper.readValue(testJSON, typeReference));
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(new String[]{
                "object_with_1_field.BenchJackson",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
