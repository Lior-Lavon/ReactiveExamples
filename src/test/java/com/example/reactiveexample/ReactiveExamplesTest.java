package com.example.reactiveexample;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveExamplesTest {

    Person michale = new Person ("Michael", "Weston");
    Person fiona = new Person("Fiona", "Glenanne");
    Person sam = new Person("Sam", "Axe");
    Person jesse = new Person("Jesse", "Porter");

//    Mono -> 0 Or 1 objects
    @Test
    public void monoTest(){
        // create a new person mono
        // Mono.just create a mono object out of the given person
        Mono<Person> personMono = Mono.just(michale);

        // Get the person object out of the mono publisher
        Person person = personMono.block();

        // output name
        log.info(person.sayMyName());
    }

    @Test
    public void monoTransform(){
        // create a new person mono
        Mono<Person> personMono = Mono.just(fiona);

        // Type transformation
        // to trigger this call Block
        PersonCommand command = personMono
                .map(person -> {
                    return new PersonCommand(person);
                }).block();

        log.info(command.sayMyName());
    }

    @Test//(expected = NullPointerException.class)
    public void monoFilter(){
        Mono<Person> personMono = Mono.just(sam);

        // filter out the mono
        Person samEx = personMono
                .filter(person -> person.getFirstName().equalsIgnoreCase("foo"))
                .block();

        log.info(samEx.sayMyName()); // throw NPE
    }

//    Flux -> 0 Or many objects
    @Test
    public void fluxTest(){
        Flux<Person> people = Flux.just(michale, fiona, sam, jesse);

        people.subscribe(person -> {
            log.info(person.sayMyName());
        });
    }

    @Test
    public void fluxTestFilter(){
        Flux<Person> people = Flux.just(michale, fiona, sam, jesse);

        // filter the people Flux for Fiona
        people.filter(person -> person.getFirstName().equalsIgnoreCase(fiona.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelayNoOutput(){
        Flux<Person> people = Flux.just(michale, fiona, sam, jesse);

        // delay 1 second and throw the output
        people.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));

        // this case is outputing before the 1 second is over in testing
    }

    @Test
    public void fluxTestDelay() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michale, fiona, sam, jesse);

        // wait 1 sec, then on complete we will output the latch
        // we will get the output every second
        people.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        // tell the test to wait until the countdown is complete
        countDownLatch.await();
    }

    @Test
    public void fluxTestFilterDelay() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michale, fiona, sam, jesse);

        // wait 1 sec, then on complete we will output the latch
        // filter based on firstName contain 'i'
        // we will get the output every second
        people.delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("i"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        // tell the test to wait until the countdown is complete
        countDownLatch.await();
    }

}
