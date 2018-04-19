package cc.test.example1.commons;

import io.atomix.copycat.Command;

/**
 * Created by sungshil on 2018/3/19.
 */
public class Delete implements Command<Object> {
    public Object key;

    public Delete(Object key) {
        this.key = key;
    }
}
