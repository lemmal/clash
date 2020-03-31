package com.clash.synchronizer;

@FunctionalInterface
public interface ISynchronizerCallable<T> {
    T call();
}
