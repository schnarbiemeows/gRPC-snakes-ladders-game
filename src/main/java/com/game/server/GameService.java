package com.game.server;

import com.models.Die;
import com.models.GameServiceGrpc;
import com.models.GameState;
import com.models.Player;
import io.grpc.stub.StreamObserver;

public class GameService extends GameServiceGrpc.GameServiceImplBase {

    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {
        Player client = Player.newBuilder()
                .setName("client")
                .setPosition(0)
                .build();
        Player server = Player.newBuilder()
                .setName("server")
                .setPosition(0)
                .build();
        return new GameTurnsRequest(client,server,responseObserver);
    }
}
