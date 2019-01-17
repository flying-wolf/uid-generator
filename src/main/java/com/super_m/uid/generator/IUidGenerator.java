package com.super_m.uid.generator;

public interface IUidGenerator<E> {
    E nextId();
    String parseId(E id);
}
