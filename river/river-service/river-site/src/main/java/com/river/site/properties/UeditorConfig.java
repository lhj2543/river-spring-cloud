package com.river.site.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UeditorConfig {

    @Value("${ueditor.attach.url:/attach}")
    public  String ueditor_attach_url;

    @Value("${ueditor.attach.path:C:/lhj/ftp/lhj-blog}")
    public  String ueditor_attach_path;

    @Value("${ueditor.congif.path:C:/lhj/release/lhj-blog}")
    public  String ueditor_config_path;

}
