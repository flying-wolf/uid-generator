package com.super_m.uid.factory;

import com.super_m.uid.generator.IUidGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Key generator factory.
 *
 * @author zhangliang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UidGeneratorFactory {

    /**
     * Create key generator.
     *
     * @param keyGeneratorClass key generator class
     * @return key generator instance
     */
    public static IUidGenerator createUidGenerator(final Class<? extends IUidGenerator> keyGeneratorClass) {
        try {
            return keyGeneratorClass.newInstance();
        } catch (final InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException(String.format("Class %s should have public privilege and no argument constructor", keyGeneratorClass.getName()));
        }
    }
}

