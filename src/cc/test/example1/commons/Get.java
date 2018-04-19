package cc.test.example1.commons;

import io.atomix.copycat.Query;

/**
 * Created by sungshil on 2018/3/19.
 */
public class Get implements Query<Object> {
    public Object key;

    public Get(Object key) {
        this.key = key;
    }
}