package com.xcheng.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.SkipCallbackExecutor;

public final class DownloadCallAdapterFactory extends CallAdapter.Factory {
    public static final CallAdapter.Factory INSTANCE = new DownloadCallAdapterFactory();

    private DownloadCallAdapterFactory() {
    }

    /**
     * Extract the raw class type from {@code type}. For example, the type representing
     * {@code List<? extends Runnable>} returns {@code List.class}.
     */
    public static Class<?> getRawType(Type type) {
        return CallAdapter.Factory.getRawType(type);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != DownloadCall.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("DownloadCall return type must be parameterized as DownloadCall<Foo> or DownloadCall<? extends Foo>");
        }
        //支持SkipCallbackExecutor
        final Executor executor = Utils.isAnnotationPresent(annotations, SkipCallbackExecutor.class)
                ? null
                : retrofit.callbackExecutor();
        return new CallAdapter<ResponseBody, DownloadCall<?>>() {
            @Override
            public Type responseType() {
                return ResponseBody.class;
            }

            @Override
            public DownloadCall<ResponseBody> adapt(retrofit2.Call<ResponseBody> call) {
                return new RealDownloadCall<>(executor != null ? executor : OptionalExecutor.get(), call);
            }
        };
    }
}
