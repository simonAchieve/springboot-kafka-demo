package com.example.kafka.serializer;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(AvroDeserializer.class);
    
    private Class<T> targetType;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (configs.containsKey("value.deserializer.type")) {
            try {
                targetType = (Class<T>) Class.forName((String) configs.get("value.deserializer.type"));
            } catch (ClassNotFoundException e) {
                logger.error("Error loading target type class", e);
                throw new RuntimeException("Error loading target type class", e);
            }
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            T result = targetType.getDeclaredConstructor().newInstance();
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<T> datumReader = new SpecificDatumReader<>(result.getSchema());
            return datumReader.read(null, decoder);
        } catch (Exception e) {
            logger.error("Error deserializing Avro message", e);
            throw new RuntimeException("Error deserializing Avro message", e);
        }
    }
}
