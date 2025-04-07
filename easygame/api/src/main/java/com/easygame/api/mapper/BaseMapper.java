package com.easygame.api.mapper;

public interface BaseMapper<A,D> {
    D toDto(A action);
}
