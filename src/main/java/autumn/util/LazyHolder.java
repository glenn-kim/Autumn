package autumn.util;

import java.util.function.Supplier;

/**
 * Created by infinitu on 14. 12. 24..
 */
public class LazyHolder<T> {
    T hold;
    Supplier<T> func;
    public LazyHolder(Supplier<T> func){
        this.func = func;
    }

    public T get()
    {
        if(hold == null)
            hold = func.get();
        return hold;
    }
}
