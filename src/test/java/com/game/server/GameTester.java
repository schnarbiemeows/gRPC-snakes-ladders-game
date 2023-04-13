package com.game.server;

import com.models.Die;
import com.models.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameTester {

    private GameServiceGrpc.GameServiceStub gameServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().build();
        this.gameServiceStub = GameServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void testGame() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        GameStateStreamingResponse response = new GameStateStreamingResponse(latch);
        StreamObserver<Die> dieStreamObserver = this.gameServiceStub.roll(response);
        response.setDieStreamObserver(dieStreamObserver);
        response.roll();
        latch.await();
    }
}
