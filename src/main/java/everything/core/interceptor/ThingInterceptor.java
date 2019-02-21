package everything.core.interceptor;

import everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {
    void applay(Thing thing);
}
