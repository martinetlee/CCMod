package cc.test.example1.commons;

import io.atomix.copycat.Command;

/**
 * Created by sungshil on 2018/3/19.
 */
public class Put implements Command<Object> {
    public Object key;
    public Object value;

    public Put(Object key, Object value) {
        this.key = key;
        this.value = value;
    }
}