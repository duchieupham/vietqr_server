package com.vietqr.org.util;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TestEnvironmentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // Kiểm tra điều kiện cho môi trường kiểm thử (ví dụ: nếu là máy chủ test)
        // Thay thế điều kiện kiểm tra theo nhu cầu cụ thể của bạn.
        return !EnvironmentUtil.isProduction();
    }
}
