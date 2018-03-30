package cc.test;

import cc.test.messagestore.MessageStore;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.StateMachineExecutor;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.catalyst.transport.*;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import cc.test.commons.*;

import static java.lang.Thread.sleep;

public class Server {
    public static String serverName;
    public static Address serverAddr;
    public static Address clusterAddr;
    public static CopycatServer ccServerInstance;
    public static boolean MAIN_SERVER;

    public static void main(String[] args) {
        // ./Server identifier server_ip server_port --> main server that bootstraps the cluster
        // ./Server identifier server_ip server_port cluster_ip cluster_port --> normal server

        if(args.length != 3 && args.length != 5){
            System.out.println("Usage:");
            System.out.println("(1) ./Server identifier server_ip server_port --> main server that bootstraps the cluster");
            System.out.println("(2) ./Server identifier server_ip server_port cluster_ip cluster_port --> normal server");
            System.exit(0);
        }

        //  Parsing Arguments

        MAIN_SERVER = false;
        if(args.length == 3)
            MAIN_SERVER = true;

        serverName = args[0];
        String server_ip = args[1];
        int server_port = 0;
        String cluster_ip = args[1];
        int cluster_port = 0;

        try {
            server_port = Integer.parseInt(args[2]);
        }catch(NumberFormatException e){
            System.out.println("port parse error : " + args[2] );
            System.exit(0);
        }

        if( !MAIN_SERVER ) {
            cluster_ip = args[3];
            try {
                cluster_port = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                System.out.println("port parse error : " + args[4]);
            }
        }

        serverAddr = new Address(server_ip, server_port);

        // Starting server
        ccServerInstance = startServer();

        if( MAIN_SERVER ){
            clusterAddr = serverAddr;
            ccServerInstance.bootstrap().thenAccept(srvr -> {
                System.out.println(srvr + " has bootstrapped a cluster");
            });
        }
        else{
            clusterAddr = new Address(cluster_ip, cluster_port);
        }

        ccServerInstance.join(clusterAddr).thenAccept(srvr -> System.out.println(srvr + " has joined the cluster"));

        try {
            sleep(10000);
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
//        System.exit(0);
    }


    public static CopycatServer startServer(){
        System.out.println("Server [" + serverName + "] is starting now.");
        // Building a server
        CopycatServer.Builder builder = CopycatServer.builder(serverAddr);

        Supplier<StateMachine> i = () -> new MessageStore( new Server() , 1);

        builder.withStateMachine(i);

        builder.withTransport(NettyTransport.builder()
                .withThreads(4)
                .build());

        builder.withName(serverName)
                .withStorage(Storage.builder()
                        .withDirectory(new File("/tmp/logs/"+serverName))
                        .withStorageLevel(StorageLevel.DISK)
                        .build()
                );

        return builder.build();
    }

    public void MessageHandler(Object message){
        System.out.println("Handling message now:");
        System.out.println(message);
    }

}
