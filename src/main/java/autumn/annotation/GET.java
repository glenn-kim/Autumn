package autumn.annotation;

import java.lang.annotation.*;

/**
 * Created by infinitu on 14. 10. 31..
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GET{
    public String value();
}