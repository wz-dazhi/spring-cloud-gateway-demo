package com.example.demo;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.stereotype.Component;

/**
 * @author: dazhi
 * @version: 1.0
 */
@Component
public class XmlHttpMessageWriter extends EncoderHttpMessageWriter<Object> {

    public XmlHttpMessageWriter(Encoder<Object> encoder) {
        super(encoder);
    }

    public XmlHttpMessageWriter() {
        super(new Jaxb2XmlEncoder());
    }

}
