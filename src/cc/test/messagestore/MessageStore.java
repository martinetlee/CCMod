package cc.test.messagestore;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.session.ServerSession;

import java.util.*;

/**
 * Created by sungshil on 2018/3/19.
 */
public class MessageStore extends StateMachine {
    private Map<Object, Commit> storage = new HashMap<>();

    private Set<ServerSession> listeners = new HashSet<>();

    private Commit<MsgPut> last_commit = null;


    public MessageStore(){

    }

    public void put(Commit<MsgPut> commit) {
        //Commit<Put> put = storage.put(commit.operation().key, commit);
        last_commit = commit;
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