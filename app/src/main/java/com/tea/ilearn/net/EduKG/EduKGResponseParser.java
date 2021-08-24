package com.tea.ilearn.net.EduKG;

import com.tea.ilearn.net.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.TypeParser;
import rxhttp.wrapper.utils.Converter;

@Parser(name = "EduKGResponse", wrappers = {List.class})
public class EduKGResponseParser<T> extends TypeParser<T> {

    protected EduKGResponseParser() { super(); }
    public EduKGResponseParser(Type type) { super(type); }

    @Override
    public T onParse(okhttp3.Response rawResponse) throws IOException {
        Response<T> response = Converter.convertTo(rawResponse, Response.class, types);;
        T data = response.getData();
        if (response.getCode() != EduKG.successCode || data == null) {
            throw new ParseException(
                    String.valueOf(response.getCode()), response.getMsg(), rawResponse
            );
        }
        return data;
    }
}