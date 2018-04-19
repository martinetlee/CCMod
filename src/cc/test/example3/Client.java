package cc.test.example3;

import cc.test.example3.messagestore.MsgGet;
import cc.test.example3.messagestore.MsgListen;
import cc.test.example3.messagestore.MsgPut;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * Created by sungshil on 2018/3/22.
 */
public class Client {
    public static String serverName;
    public static Address clusterAddr;

    public static void main(String[] args) {
        serverName = args[0];
        String server_ip = args[1];
        int server_port = 0;
        try {
            server_port = Integer.parseInt(args[2]);
        }catch(NumberFormatException e){
            System.out.println("port parse error : " + args[2] );
            System.exit(0);
        }
        clusterAddr = new Address(server_ip, server_port);

        CopycatClient client = CopycatClient.builder()
                .withTransport(
                        NettyTransport.builder()
                                .withThreads(2)
                                .build()
                )
                .build();

        // Address clusterAddress = new Address("123.456.789.0", 5000);
        System.out.println("Client connecting to server...");
        CompletableFuture<CopycatClient> cf = client.connect(clusterAddr);

        System.out.print("Client ready, joining now...");
        cf.join();


        client.onEvent("msgReceived", event -> {
            System.out.println("Server has received: "+event );
        });

        client.submit( new MsgListen() );

        System.out.println("Joined, awaiting commands");
//        System.out.println("Usage: (1) put [key] [val] (2) get [key] (3) del [key] (4) exit ");
        System.out.println("Usage: (1) put [message] (2) get (3) exit ");
        while(true) {
            Scanner scanner = new Scanner( System.in );
            String command_line = scanner.nextLine();
            String[] cmds = command_line.split("\\s+");

	    System.out.println(command_line);
	    System.out.println(cmds[0]);

	    if(cmds[0].equals("put")){
	        System.out.println("client received put command");
	        CompletableFuture<Object> future = client.submit(new MsgPut(cmds[1]));
//	        CompletableFuture<Object> future = client.submit(new Put(cmds[1], cmds[2]));
	    }
	    else if(cmds[0].equals("get")){
	        System.out.println("client received get command");
	        client.submit(new MsgGet()).thenAccept(result -> System.out.println(cmds[1] + " is: " + result));
//	        client.submit(new Get(cmds[1])).thenAccept(result -> System.out.println(cmds[1] + " is: " + result));
	    }
/*	    else if(cmds[0].equals("del")) {
	       System.out.println("client received del command"); 
	       client.submit(new Delete(cmds[1])).thenRun(() -> System.out.println(cmds[1] + " has been deleted"));
	    }
*/	    else if(cmds[0].equals("exit") ){
	       System.out.println("client received exit command"); 
	       break;
	    }
	    else{
//	        System.out.println("Usage: (1) put [key] [val] (2) get [key] (3) del [key] (4) exit ");
            System.out.println("Usage: (1) put [message] (2) get (3) exit ");
	    }

/*            try {
                Object result = future.get();
            } catch (Exception e) {
                System.out.println("shit..... client dead");
                System.out.println(e.getStackTrace());
            }*/
        }
        System.out.println("bye!");
        System.exit(0);
    }
}
