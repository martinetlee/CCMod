package cc.test.commons;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;

import java.util.*;

/**
 * Created by sungshil on 2018/3/19.
 */
public class KeyValueStore extends StateMachine {
    private Map<Object, Commit> storage = new HashMap<>();

    public KeyValueStore(){

    }

    public Object put(Commit<Put> commit) {
        Commit<Put> put = storage.put(commit.operation().key, commit);
        return put == null ? null : put.operation().value;
    }

    public Object get(Commit<Get> commit) {
        try {
            Commit<Put> put = storage.get(commit.operation().key);
            return put == null ? null : put.operation().value;
        } finally {
            commit.release();
        }
    }

    public Object delete(Commit<Delete> commit) {
        Commit<Put> put = null;
        try {
            put = storage.remove(commit.operation().key);
            return put == null ? null : put.operation().value;
        } finally {
            if (put != null)
                put.release();
            commit.release();
        }
    }
}