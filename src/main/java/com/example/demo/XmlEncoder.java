package com.example.demo;

import jakarta.xml.bind.*;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: dazhi
 * @version: 1.0
 */
public class XmlEncoder extends Jaxb2XmlEncoder {

    @Override
    public boolean canEncode(ResolvableType elementType, MimeType mimeType) {
        if (!super.canEncode(elementType, mimeType)) {
            return Map.class.isAssignableFrom(elementType.toClass());
        }
        return false;
    }

    @Override
    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory,
                                  ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

        if (!Map.class.isAssignableFrom(value.getClass())) {
            return super.encodeValue(value, bufferFactory, valueType, mimeType, hints);
        }

        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn);
                return Hints.getLogPrefix(hints) + "Encoding [" + formatted + "]";
            });
        }

        boolean release = true;
        DataBuffer buffer = bufferFactory.allocateBuffer(1024);
        try {
            OutputStream outputStream = buffer.asOutputStream();
//            Class<?> clazz = LinkedHashMap.class;
//            Marshaller marshaller = initMarshaller(clazz);
//            marshaller.marshal(value, outputStream);
            outputStream.write("<a>hello</a>".getBytes());
            outputStream.flush();
            release = false;
            return buffer;
        }
//        catch (MarshalException ex) {
//            throw new EncodingException("Could not marshal " + value.getClass() + " to XML", ex);
//        }
//        catch (JAXBException ex) {
//            throw new CodecException("Invalid JAXB configuration", ex);
//        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (release) {
                DataBufferUtils.release(buffer);
            }
        }
    }

}
