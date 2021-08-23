package com.tea.ilearn.net;

import static rxhttp.wrapper.utils.Converter.convert;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.TypeParser;

@Parser(name = "Response", wrappers = {List.class})
public class ResponseParser<T> extends TypeParser<T> {

    protected ResponseParser() { super(); }
    public ResponseParser(Type type) { super(type); }

    @Override
    public T onParse(okhttp3.Response response) throws IOException {
        final Type type = ParameterizedTypeImpl.get(Response.class, types);
        Response<T> data = convert(response, type);
        T t = data.getData();
        if (data.getCode() != EduKG.successCode || t == null) {
            throw new ParseException(String.valueOf(data.getCode()), data.getMsg(), response);
        }
        return t;
    }
}
