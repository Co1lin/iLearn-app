package com.tea.ilearn.net;

import io.reactivex.rxjava3.core.Observable;
import rxhttp.wrapper.param.RxHttp;

public interface ResponseDefiner {
    Observable<?> define(RxHttp p);
}
