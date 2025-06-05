package com.curso.reactive.sec04.helper;

import java.util.function.Consumer;

import com.curso.reactive.common.Util;

import reactor.core.publisher.FluxSink;

public class NameGenerator implements Consumer<FluxSink<String>> {

    private FluxSink<String> sink;

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        this.sink = stringFluxSink;
    }

    public void generate(){
        this.sink.next(Util.faker().name().firstName());
    }

}
