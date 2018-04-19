package cc.test.example2.messagestore;

import io.atomix.copycat.Command;

/**
 * Created by sungshil on 2018/3/19.
 */
public class MsgPut implements Command<Object> {
    public Object message;

    public MsgPut(Object message) {
        this.message = message;
    }
}
