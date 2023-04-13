package com.game.server;

import com.models.Die;
import com.models.GameState;
import com.models.Player;
import java.util.concurrent.ThreadLocalRandom;
import io.grpc.stub.StreamObserver;

public class GameTurnsRequest implements StreamObserver<Die> {

    private Player client;
    private Player server;
    private StreamObserver<GameState> gameStateStreamObserver;

    public GameTurnsRequest(Player client, Player server, StreamObserver<GameState> gameStateStreamObserver) {
        this.client = client;
        this.server = server;
        this.gameStateStreamObserver = gameStateStreamObserver;
    }

    @Override
    public void onNext(Die die) {
        this.client = getNewPlayerPosition(this.client,die.getValue());
        if(this.client.getPosition() != 100) {
            this.server = this.getNewPlayerPosition(this.server,
                    ThreadLocalRandom.current().nextInt(1,7));
        }
        this.gameStateStreamObserver.onNext(this.getGameState());
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
    }

    private GameState getGameState() {
        return GameState.newBuilder()
                .addPlayer(this.client)
                .addPlayer(this.server)
                .build();
    }

    private Player getNewPlayerPosition(Player client, int dieValue) {
        int position = client.getPosition()+dieValue;
        position = SnakesAndLaddersMap.getPosition(position);
        if(position<=100) {
            client = client.toBuilder()
                    .setPosition(position)
                    .build();
        }
        return client;
    }

}
