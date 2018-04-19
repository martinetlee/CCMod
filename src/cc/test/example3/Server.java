package cc.test.example3;

import cc.test.example3.messagestore.MessageStore;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;

public class Server {
    public static String serverName;
    public static Address serverAddr;
    public static Address clusterAddr;
    public static CopycatServer ccServerInstance;
    public static boolean MAIN_SERVER = false;

    public static void main(String[] args) {
        // ./Server identifier server_ip server_port --> main server that bootstraps the cluster
        // ./Server identifier server_ip server_port cluster_ip cluster_port --> normal server


        // ./Server identifier server_ip server_port cluster_config_file
        if(args.length != 3 ){
            System.out.println("Usage:");
            System.out.println("./Server identifier server_ip cluster_config_file");
            System.out.println(args[0] + "/" + args[1] +"/"+ args[2]);
            System.exit(0);
        }

        // Let all the servers run on a default port 5987
        final int all_port = 5987;

        // Parsing Arguments
        serverName = args[0];
        String server_ip = args[1];
        String clusterFile = args[2];

        // Parse the clusterFile, the first entry is the main server.
        // Currently, we don't actually need to loop through the file,
        // but the structure is left for future change to test "cluster setting"

        try (BufferedReader br = new BufferedReader(new FileReader(clusterFile))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if(count == 0){
                    clusterAddr = new Address(line, all_port);
                }
                count++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        serverAddr = new Address(server_ip, all_port);

        if( serverAddr.host().equals(clusterAddr.host()) )
            MAIN_SERVER = true;
        else{
            System.out.println(serverAddr.host() + " is not the cluster: " + clusterAddr.host() );
        }

        // Starting server
        ccServerInstance = startServer();

        if( MAIN_SERVER ){
            clusterAddr = serverAddr;
            ccServerInstance.bootstrap().thenAccept(srvr -> {
                System.out.println(srvr + " has bootstrapped a cluster");
            });
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
