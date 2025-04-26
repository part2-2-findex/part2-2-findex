package com.part2.findex.syncjob.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ClientIpResolver {
    public String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes.getRequest().getRemoteAddr();
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}