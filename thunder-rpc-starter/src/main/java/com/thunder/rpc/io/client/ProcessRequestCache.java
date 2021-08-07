package com.thunder.rpc.io.client;

import com.thunder.rpc.io.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessRequestCache {

    private static final Map<String, CompletableFuture<RpcResponse>> PROCESS_RESPONSE = new ConcurrentHashMap<>();

    public static void put(String requestId, CompletableFuture<RpcResponse> future) {
        PROCESS_RESPONSE.put(requestId, future);
    }

    public static void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = PROCESS_RESPONSE.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException("complete request error");
        }
    }
}
