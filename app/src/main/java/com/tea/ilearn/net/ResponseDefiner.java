package com.tea.ilearn.net;

import io.reactivex.rxjava3.core.Observable;
import rxhttp.wrapper.param.RxHttpNoBodyParam;

public interface ResponseDefiner {
    Observable<?> define(RxHttpNoBodyParam p);
}
