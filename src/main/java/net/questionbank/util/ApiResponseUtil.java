package net.questionbank.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseUtil<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponseUtil<T> success(String message, T data) {
        return ApiResponseUtil.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseUtil<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponseUtil<T> error(String message) {
        return ApiResponseUtil.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
