package com.example.kafka.serializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(AvroSerializer.class);

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            org.apache.avro.io.BinaryEncoder encoder = 
                org.apache.avro.io.EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);
            org.apache.avro.io.DatumWriter<T> datumWriter = 
                new org.apache.avro.specific.SpecificDatumWriter<>(data.getSchema());
            datumWriter.write(data, encoder);
            encoder.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Error serializing Avro message", e);
            throw new RuntimeException("Error serializing Avro message", e);
        }
    }
}
