package com.easygame.api.configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class XssModule extends SimpleModule {
    public XssModule() {
        this.addSerializer(String.class, new XssStringJsonSerializer());
    }
}
