package com.jsoniter.demo;

import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.List;

@State(Scope.Thread)
public class ListBinding {
    private TypeLiteral<List<String>> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<List<String>> typeRef;
    private String inputStr;

    private JsonIterator iter;
    private DslJson dslJson;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        inputStr = "['jackson','jsoniter','fastjson']".replace('\'', '"');
        input = inputStr.getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<List<String>>() {
        };
        typeRef = new TypeReference<List<String>>() {
        };
        JacksonAnnotationSupport.enable();
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        dslJson = new DslJson();
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        System.out.println(withJsoniter());
        System.out.println(withJackson());
        System.out.println(withDsljson());
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "ListBinding",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Benchmark
    public void withJsoniterBinding(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJackson(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

    @Benchmark
    public void withDsljson(Blackhole bh) throws IOException {
        bh.consume(withDsljson());
    }

    private List<String> withJsoniter() throws IOException {
        iter.reset(input);
        return iter.read(typeLiteral);
    }

    private List<String> withJackson() throws IOException {
        return jackson.readValue(input, typeRef);
    }

    private List<String> withDsljson() throws IOException {
        return (List<String>) dslJson.deserializeList(String.class, input, input.length);
    }
}
