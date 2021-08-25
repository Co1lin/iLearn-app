package com.tea.ilearn.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.TypeParser;
import rxhttp.wrapper.utils.Converter;

@Parser(name = "Response", wrappers = {List.class})
public class ResponseParser<T> extends TypeParser<T> {

    protected ResponseParser() { super(); }
    public ResponseParser(Type type) { super(type); }

    @Override
    public T onParse(okhttp3.Response rawResponse) throws IOException {
        Response<T> response = Converter.convertTo(rawResponse, Response.class, types);;
        T data = response.getData();
        if (response.getCode() / 100 != 2 || data == null) {
            throw new ParseException(
                    String.valueOf(response.getCode()), response.getMsg(), rawResponse
            );
        }
        return data;
    }
}