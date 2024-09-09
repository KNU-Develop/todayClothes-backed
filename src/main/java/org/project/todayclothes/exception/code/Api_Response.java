package org.project.todayclothes.exception.code;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Api_Response<T> {
    private int code;
    private String message;
    private T result;
}
