package com.egs.connection.pool;

public interface ObjectPool<T> {

    T get();

    void set(T t);
}