package com.timmytime.predictoranalysisplayers;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class FluxTest {

    Consumer<Long> listener;
    Consumer<Long> listener2;

    ConnectableFlux<Object> publish = Flux.create(fluxSink ->
            listener = (t) -> fluxSink.next(t)
    ).publish();

    Flux<Long> testing = Flux.create(fluxSink ->
            listener2 = (t) -> fluxSink.next(t));




    @Test
    public void test(){

        testing.publish();
        publish.connect();

        List<Long> first = new ArrayList<>();
        List<Long> second = new ArrayList<>();


        testing.subscribe(first::add);
        testing.subscribe(second::add);

        //publish.subscribe(System.out::println);



        listener.accept(1L);
        listener.accept(2L);
        listener.accept(3L);

        listener2.accept(4L);
        listener2.accept(5L);
        listener2.accept(6L);


        first.stream().forEach(f -> {
            System.out.println("first "+f);
        });
        second.stream().forEach(f -> {
            System.out.println("second "+f);
        });



    }

}
