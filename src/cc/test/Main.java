package cc.test;

import cc.test.commons.Delete;
import cc.test.commons.Get;
import cc.test.commons.KeyValueStore;
import cc.test.commons.Put;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Main {
    public static int s_id;

    public static void main(String[] args) {

        // CCTest3
        //  This is modified from:
	    //      https://jaxenter.com/create-distributed-datastore-10-minutes-131243.html
        //
        //  Using copycat version 1.2.8  (newer than official "getting started")
        //
        //  Other references:
        //      http://atomix.io/copycat/docs/getting-started/
        //      http://kriszhang.com/atomix/
        s_id = 0;

        System.out.println("My ink is too thin, my paper's to white;");
        System.out.println("I followed the course, from chaos to art.");
/*
        // Building a server
        CopycatServer server = startServer("Initial Server");

        server.bootstrap().thenAccept(srvr -> {
            System.out.println(srvr + " has bootstrapped a cluster");
        });
*/
        // Bootstrap the cluster
        Address clusterAddress = new Address("localhost", 5000);
//        server.join(clusterAddress).thenAccept(srvr -> System.out.println(srvr + " has joined the cluster"));

        // Building a server
        CopycatServer server = startServer("Initial Server");

        server.bootstrap().thenAccept(srvr -> {
            System.out.println(srvr + " has bootstrapped a cluster");
        });
        server.join(clusterAddress).thenAccept(srvr -> System.out.println(srvr + " has joined the cluster"));


        CopycatServer server1 = startServer("server 1");
        CopycatServer server2 = startServer("server 2");
        CopycatServer server3 = startServer("server 3");

        server1.join(clusterAddress).thenAccept(srvr -> System.out.println(srvr+" has joined the cluster"));
        server2.join(clusterAddress).thenAccept(srvr -> System.out.println(srvr+" has joined the cluster"));
        server3.join(clusterAddress).thenAccept(srvr -> System.out.println(srvr+" has joined the cluster"));

        System.out.println("Waiting for initial server.");

        try {
            sleep(5000);
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }




        try {
            sleep(5000);
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
        CopycatClient client = CopycatClient.builder()
                .withTransport(
                        NettyTransport.builder()
                        .withThreads(2)
                        .build()
                )
                .build();

        // Address clusterAddress = new Address("123.456.789.0", 5000);
        System.out.println("Client connecting to server...");
        CompletableFuture<CopycatClient> cf = client.connect(clusterAddress);
        System.out.print("Client ready, joining now...");

        cf.join();

        CopycatClient client1 = CopycatClient.builder()
                .withTransport(
                        NettyTransport.builder()
                                .withThreads(2)
                                .build()
                )
                .build();

        clusterAddress = new Address("localhost", 5000);
        // Address clusterAddress = new Address("123.456.789.0", 5000);
        System.out.println("Client connecting to server...");
        CompletableFuture<CopycatClient> cf1 = client1.connect(clusterAddress);
        System.out.print("Client ready, joining now...");

        cf1.join();




        CompletableFuture<Object> future = client.submit(new Put("foo", "Hello world!"));
        try {
            Object result = future.get();
        }catch(Exception e){
            System.out.println("shit..... client dead");
            System.out.println(e.getStackTrace());
        }
        client.submit(new Get("foo")).thenAccept(result -> System.out.println("foo is: " + result));

        client1.submit(new Get("foo")).thenAccept(result -> System.out.println("in client1, foo is: " + result));


        client.submit(new Delete("foo")).thenRun(() -> System.out.println("foo has been deleted"));


        System.out.println(server.name() + " : " + StateString(server.state()));
        System.out.println(server1.name() + " : " + StateString(server1.state()));
        System.out.println(server2.name() + " : " + StateString(server2.state()));
        System.out.println(server3.name() + " : " + StateString(server3.state()));



        System.out.println("This is the longing, and this is the book.");

        try {
            sleep(10000);
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
        System.exit(0);
    }

    public static CopycatServer startServer(String ServerName){
        System.out.println("Starting server '" + ServerName + "'");
        // Building a server
        Address address = new Address("localhost", (5000+s_id) );
        s_id++;
        CopycatServer.Builder builder = CopycatServer.builder(address);

        builder.withStateMachine(KeyValueStore::new);
        //builder.withStateMachine(new KeyValueStore());


        builder.withTransport(NettyTransport.builder()
                .withThreads(4)
                .build());

        builder.withName(ServerName)
                .withStorage(Storage.builder()
                    .withDirectory(new File("logs"))
                    .withStorageLevel(StorageLevel.DISK)
                    .build()
                );

        return builder.build();
    }


    public static String StateString(CopycatServer.State css){
        switch(css){
            case LEADER:
                return "Leader";
            case CANDIDATE:
                return "Candidate";
            case PASSIVE:
                return "Passive";
            case FOLLOWER:
                return "Follower";
            case INACTIVE:
                return "Inactive";
            default:
                return "Reserve";
        }
    }
}
