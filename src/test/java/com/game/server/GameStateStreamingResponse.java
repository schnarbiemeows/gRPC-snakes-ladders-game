package com.game.server;

import com.models.Die;
import com.models.GameState;
import com.models.Player;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class GameStateStreamingResponse implements StreamObserver<GameState> {

    private StreamObserver<Die> gameTurnsRequestStreamObserver;
    private CountDownLatch latch;

    public GameStateStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(GameState gameState) {
        List<Player> playerList = gameState.getPlayerList();
        playerList.forEach(rec -> System.out.println(rec.getName() + " : " + rec.getPosition()));
        boolean anyWinner = playerList.stream().anyMatch(rec -> rec.getPosition() == 100);
        if(anyWinner) {
            System.out.println("We have a winner!");
            this.gameTurnsRequestStreamObserver.onCompleted();
        } else {
            this.roll();
        }
        System.out.println("---------------------------------------------------------------------------");
    }

    @Override
    public void onError(Throwable throwable) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        this.latch.countDown();
    }

    public void setDieStreamObserver(StreamObserver<Die> observer) {
        this.gameTurnsRequestStreamObserver = observer;
    }
    public void roll() {
        int die = ThreadLocalRandom.current().nextInt(1,7);
        Die dieObj = Die.newBuilder()
                .setValue(die)
                .build();
        this.gameTurnsRequestStreamObserver.onNext(dieObj);
    }
}
