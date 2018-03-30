package cc.test.messagestore;

import cc.test.Server;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.session.ServerSession;

import java.util.*;


/**
 * Created by sungshil on 2018/3/24.
 */
public class MessageStore extends StateMachine {
    private int designatedLeader;
    private Server server;
    private Set<ServerSession> listeners = new HashSet<>();
    private Commit<MsgPut> last_commit = null;

    public MessageStore(Server server, int designatedLeader){
        this.server = server;
        this.designatedLeader = designatedLeader;
    }

    public void listen(Commit<MsgListen> commit){
        listeners.add(commit.session());
        commit.release();
    }

    public void put(Commit<MsgPut> commit) {
        //Commit<Put> put = storage.put(commit.operation().key, commit);
        last_commit = commit;

        listeners.forEach(session -> {
            session.publish("msgReceived", commit.operation().message);
        });

        server.MessageHandler(last_commit.operation().message);
    }

    public Object get(Commit<MsgGet> commit) {
        try {
            Commit<MsgPut> put = last_commit;
            return put == null ? null : put.operation().message;
        } finally {
            commit.release();
        }
    }
}