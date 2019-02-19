package com.every.everything.core.interceptor;

import java.io.File;

@FunctionalInterface
public interface FileInterceptor{

    void apply(File file);
}
