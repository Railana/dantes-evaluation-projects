/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.config;

import java.util.Locale;

import org.junit.Test;

import org.quicktheories.core.Gen;
import org.quicktheories.generators.SourceDSL;

import static org.apache.cassandra.config.DataStorageSpec.DataStorageUnit.BYTES;
import static org.apache.cassandra.config.DataStorageSpec.DataStorageUnit.GIBIBYTES;
import static org.apache.cassandra.config.DataStorageSpec.DataStorageUnit.KIBIBYTES;
import static org.apache.cassandra.config.DataStorageSpec.DataStorageUnit.MEBIBYTES;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.quicktheories.QuickTheory.qt;

public class DataStorageSpecTest
{
    private static final long MAX_LONG_CONFIG_VALUE = Long.MAX_VALUE - 1;
    @Test
    public void testConversions()
    {
        Integer magicNumber47591002 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47591002, new DataStorageSpec.LongBytesBound("10B").toBytes(), () -> "Add assertion message");
        Integer magicNumber47842032 = 10240; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47842032, new DataStorageSpec.LongBytesBound("10KiB").toBytes(), () -> "Add assertion message");
        assertEquals(10485760, new DataStorageSpec.LongBytesBound("10MiB").toBytes(), () -> "Add assertion message");
        Integer magicNumber67782673 = 1073741824; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber67782673, new DataStorageSpec.LongBytesBound("1GiB").toBytes(), () -> "Add assertion message");

        Integer magicNumber50691616 = 1024; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber50691616, new DataStorageSpec.LongMebibytesBound("1GiB").toMebibytes(), () -> "Add assertion message");
        Integer magicNumber27476930 = 10485760; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber27476930, new DataStorageSpec.LongMebibytesBound("10MiB").toBytes(), () -> "Add assertion message");
        Integer magicNumber64911194 = 10240; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber64911194, new DataStorageSpec.LongMebibytesBound("10MiB").toKibibytes(), () -> "Add assertion message");
        assertEquals(1024 * 1024 * 1024, new DataStorageSpec.IntBytesBound("1GiB").toBytes(), () -> "Add assertion message");
        Integer magicNumber70009703 = 10240; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber70009703, new DataStorageSpec.IntKibibytesBound("10MiB").toKibibytes(), () -> "Add assertion message");
        Integer magicNumber85625166 = 1024; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber85625166, new DataStorageSpec.IntMebibytesBound("1GiB").toMebibytes(), () -> "Add assertion message");

        Integer magicNumber73275864 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber73275864, new DataStorageSpec.LongBytesBound(10, BYTES).toBytes(), () -> "Add assertion message");
        assertEquals(10240, new DataStorageSpec.LongBytesBound(10, KIBIBYTES).toBytes(), () -> "Add assertion message");
        assertEquals(10485760, new DataStorageSpec.LongBytesBound(10, MEBIBYTES).toBytes(), () -> "Add assertion message");
        assertEquals(1073741824, new DataStorageSpec.LongBytesBound(1, GIBIBYTES).toBytes(), () -> "Add assertion message");

        assertEquals(1024, new DataStorageSpec.LongMebibytesBound(1, GIBIBYTES).toMebibytes(), () -> "Add assertion message");
        assertEquals(1024 * 1024, new DataStorageSpec.LongMebibytesBound(1, GIBIBYTES).toKibibytes(), () -> "Add assertion message");
        assertEquals(10485760, new DataStorageSpec.LongMebibytesBound(10, MEBIBYTES).toBytes(), () -> "Add assertion message");
        assertEquals(10240, new DataStorageSpec.IntKibibytesBound(10, MEBIBYTES).toKibibytes(), () -> "Add assertion message");
        assertEquals(1024, new DataStorageSpec.IntMebibytesBound(1, GIBIBYTES).toMebibytes(), () -> "Add assertion message");
    }

    @Test
    public void testOverflowingConversion()
    {
        assertThatThrownBy(() -> new DataStorageSpec.IntBytesBound("2147483648B")).isInstanceOf(IllegalArgumentException.class)
                                                                                  .hasMessageContaining("Invalid data storage: 2147483648B. " +
                                                                                                        "It shouldn't be more than 2147483646 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntBytesBound(2147483648L)).isInstanceOf(IllegalArgumentException.class)
                                                                                .hasMessageContaining("Invalid data storage: 2147483648 bytes. " +
                                                                                                      "It shouldn't be more than 2147483646 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntBytesBound("2147483648KiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                    .hasMessageContaining("Invalid data storage: 2147483648KiB. " +
                                                                                                          "It shouldn't be more than 2147483646 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntBytesBound("35791395MiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                  .hasMessageContaining("Invalid data storage: 35791395MiB. " +
                                                                                                        "It shouldn't be more than 2147483646 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntBytesBound("34954GiB")).isInstanceOf(IllegalArgumentException.class)
                                                                               .hasMessageContaining("Invalid data storage: 34954GiB. " +
                                                                                                     "It shouldn't be more than 2147483646 in bytes");

        assertThatThrownBy(() -> new DataStorageSpec.IntKibibytesBound("2147483648B")).isInstanceOf(IllegalArgumentException.class)
                                                                                      .hasMessageContaining("Invalid data storage: 2147483648B " +
                                                                                                            "Accepted units:[KIBIBYTES, MEBIBYTES, GIBIBYTES]");
        assertThatThrownBy(() -> new DataStorageSpec.IntKibibytesBound(2147483648L, BYTES)).isInstanceOf(IllegalArgumentException.class)
                                                                                           .hasMessageContaining("Invalid data storage: 2147483648B " +
                                                                                                                 "Accepted units:[KIBIBYTES, MEBIBYTES, GIBIBYTES]");
        assertThatThrownBy(() -> new DataStorageSpec.IntKibibytesBound("2147483648KiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                        .hasMessageContaining("Invalid data storage: 2147483648KiB. " +
                                                                                                              "It shouldn't be more than 2147483646 in kibibytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntKibibytesBound("35791395MiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                      .hasMessageContaining("Invalid data storage: 35791395MiB. " +
                                                                                                            "It shouldn't be more than 2147483646 in kibibytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntKibibytesBound("34954GiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                   .hasMessageContaining("Invalid data storage: 34954GiB. " +
                                                                                                         "It shouldn't be more than 2147483646 in kibibytes");

        assertThatThrownBy(() -> new DataStorageSpec.IntMebibytesBound("2147483648B")).isInstanceOf(IllegalArgumentException.class)
                                                                                      .hasMessageContaining("Invalid data storage: 2147483648B " +
                                                                                                            "Accepted units:[MEBIBYTES, GIBIBYTES]");
        assertThatThrownBy(() -> new DataStorageSpec.IntMebibytesBound("2147483648MiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                        .hasMessageContaining("Invalid data storage: 2147483648MiB. " +
                                                                                                              "It shouldn't be more than 2147483646 in mebibytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntMebibytesBound(2147483648L, MEBIBYTES)).isInstanceOf(IllegalArgumentException.class)
                                                                                               .hasMessageContaining("Invalid data storage: 2147483648 mebibytes. " +
                                                                                                                     "It shouldn't be more than 2147483646 in mebibytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntMebibytesBound("2097152GiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                     .hasMessageContaining("Invalid data storage: 2097152GiB. " +
                                                                                                           "It shouldn't be more than 2147483646 in mebibytes");
        assertThatThrownBy(() -> new DataStorageSpec.IntMebibytesBound(2147483648L)).isInstanceOf(IllegalArgumentException.class)
                                                                                    .hasMessageContaining("Invalid data storage: 2147483648 mebibytes." +
                                                                                                          " It shouldn't be more than 2147483646 in mebibytes");

        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE + "B")).isInstanceOf(IllegalArgumentException.class)
                                                                                          .hasMessageContaining("Invalid data storage: 9223372036854775807B. " +
                                                                                                                "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE, BYTES)).isInstanceOf(IllegalArgumentException.class)
                                                                                           .hasMessageContaining("Invalid data storage: 9223372036854775807 bytes. " +
                                                                                                                 "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE)).isInstanceOf(IllegalArgumentException.class)
                                                                                    .hasMessageContaining("Invalid data storage: 9223372036854775807 bytes. " +
                                                                                                          "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE + "KiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                            .hasMessageContaining("Invalid data storage: 9223372036854775807KiB. " +
                                                                                                                  "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("9223372036854775MiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                  .hasMessageContaining("Invalid data storage: 9223372036854775MiB. " +
                                                                                                        "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("9223372036854775GiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                           .hasMessageContaining("Invalid data storage: 9223372036854775GiB. " +
                                                                                                                 "It shouldn't be more than 9223372036854775806 in bytes");

        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound(Long.MAX_VALUE + "B")).isInstanceOf(IllegalArgumentException.class)
                                                                                              .hasMessageContaining("Invalid data storage: 9223372036854775807B " +
                                                                                                                    "Accepted units:[MEBIBYTES, GIBIBYTES]");
        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound(Long.MAX_VALUE, BYTES)).isInstanceOf(IllegalArgumentException.class)
                                                                                              .hasMessageContaining("Invalid data storage: 9223372036854775807B " +
                                                                                                                    "Accepted units:[MEBIBYTES, GIBIBYTES]");
        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound(Long.MAX_VALUE + "KiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                                .hasMessageContaining("Invalid data storage: 9223372036854775807KiB " +
                                                                                                                      "Accepted units:[MEBIBYTES, GIBIBYTES]");
        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound(Long.MAX_VALUE + "MiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                                .hasMessageContaining("Invalid data storage: 9223372036854775807MiB. " +
                                                                                                                      "It shouldn't be more than 9223372036854775806 in mebibytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound("9223372036854775555GiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                                  .hasMessageContaining("Invalid data storage: 9223372036854775555GiB. " +
                                                                                                                        "It shouldn't be more than 9223372036854775806 in mebibytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound(Long.MAX_VALUE)).isInstanceOf(IllegalArgumentException.class)
                                                                                        .hasMessageContaining("Invalid data storage: 9223372036854775807 mebibytes." +
                                                                                                              " It shouldn't be more than 9223372036854775806 in mebibytes");
    }

    @Test
    public void testFromSymbol()
    {
        assertEquals(DataStorageSpec.DataStorageUnit.fromSymbol("B"), BYTES, () -> "Add assertion message");
        assertEquals(DataStorageSpec.DataStorageUnit.fromSymbol("KiB"), DataStorageSpec.DataStorageUnit.KIBIBYTES, () -> "Add assertion message");
        assertEquals(DataStorageSpec.DataStorageUnit.fromSymbol("MiB"), DataStorageSpec.DataStorageUnit.MEBIBYTES, () -> "Add assertion message");
        assertEquals(DataStorageSpec.DataStorageUnit.fromSymbol("GiB"), DataStorageSpec.DataStorageUnit.GIBIBYTES, () -> "Add assertion message");
        assertThatThrownBy(() -> DataStorageSpec.DataStorageUnit.fromSymbol("n"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported data storage unit: n");
    }

    @Test
    public void testInvalidInputs()
    {
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("10")).isInstanceOf(IllegalArgumentException.class)
                                                                          .hasMessageContaining("Invalid data storage: 10");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("-10bps")).isInstanceOf(IllegalArgumentException.class)
                                                                              .hasMessageContaining("Invalid data storage: -10bps");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("-10b")).isInstanceOf(IllegalArgumentException.class)
                                                                            .hasMessageContaining("Invalid data storage: -10b");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(-10, BYTES)).isInstanceOf(IllegalArgumentException.class)
                                                                                .hasMessageContaining("Invalid data storage: value must be non-negative");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("10HG")).isInstanceOf(IllegalArgumentException.class)
                                                                            .hasMessageContaining("Invalid data storage: 10HG");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound("9223372036854775809B")
                                 .toBytes()).isInstanceOf(NumberFormatException.class)
                                            .hasMessageContaining("For input string: \"9223372036854775809\"");

        assertThatThrownBy(() -> new DataStorageSpec.IntKibibytesBound("10B")).isInstanceOf(IllegalArgumentException.class)
                                                                              .hasMessageContaining("Invalid data storage: 10B Accepted units");
        assertThatThrownBy(() -> new DataStorageSpec.IntMebibytesBound("10B")).isInstanceOf(IllegalArgumentException.class)
                                                                              .hasMessageContaining("Invalid data storage: 10B Accepted units");

        assertThatThrownBy(() -> new DataStorageSpec.LongMebibytesBound("10B")).isInstanceOf(IllegalArgumentException.class)
                                                                               .hasMessageContaining("Invalid data storage: 10B Accepted units");
    }

    @Test
    public void testValidUnits()
    {
        assertEquals(10240L, new DataStorageSpec.IntBytesBound("10KiB").toBytes(), () -> "Add assertion message");
        assertEquals(10L, new DataStorageSpec.IntKibibytesBound("10KiB").toKibibytes(), () -> "Add assertion message");
        assertEquals(10L, new DataStorageSpec.IntMebibytesBound("10MiB").toMebibytes(), () -> "Add assertion message");
    }

    @Test
    public void testInvalidForConversion()
    {
       //just test the cast to Int
//        assertEquals(Integer.MAX_VALUE, new DataStorageSpec.LongBytesBound("9223372036854775806B").toBytesAsInt());

        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE + "B")).isInstanceOf(IllegalArgumentException.class)
                                                                                          .hasMessageContaining("Invalid data storage: 9223372036854775807B. " +
                                                                                                                "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE + "KiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                            .hasMessageContaining("Invalid data storage: 9223372036854775807KiB. " +
                                                                                                                  "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE-5L + "MiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                               .hasMessageContaining("Invalid data storage: 9223372036854775802MiB. " +
                                                                                                                     "It shouldn't be more than 9223372036854775806 in bytes");
        assertThatThrownBy(() -> new DataStorageSpec.LongBytesBound(Long.MAX_VALUE-5L + "GiB")).isInstanceOf(IllegalArgumentException.class)
                                                                                               .hasMessageContaining("Invalid data storage: 9223372036854775802GiB. " +
                                                                                                                     "It shouldn't be more than 9223372036854775806 in bytes");
    }

    @Test
    public void testEquals()
    {
        assertEquals(new DataStorageSpec.LongBytesBound("10B"), new DataStorageSpec.LongBytesBound("10B"), () -> "Add assertion message");

        assertEquals(new DataStorageSpec.LongBytesBound.LongBytesBound("10KiB"), new DataStorageSpec.LongBytesBound.LongBytesBound("10240B"), () -> "Add assertion message");
        assertEquals(new DataStorageSpec.LongBytesBound.LongBytesBound("10240B"), new DataStorageSpec.LongBytesBound("10KiB"), () -> "Add assertion message");

        assertEquals(new DataStorageSpec.LongBytesBound("10MiB"), new DataStorageSpec.LongBytesBound("10240KiB"), () -> "Add assertion message");
        assertEquals(new DataStorageSpec.LongBytesBound("10240KiB"), new DataStorageSpec.LongBytesBound("10MiB"), () -> "Add assertion message");

        assertEquals(new DataStorageSpec.LongBytesBound("10GiB"), new DataStorageSpec.LongBytesBound("10240MiB"), () -> "Add assertion message");
        assertEquals(new DataStorageSpec.LongBytesBound("10240MiB"), new DataStorageSpec.LongBytesBound("10GiB"), () -> "Add assertion message");

        assertEquals(new DataStorageSpec.LongBytesBound(MAX_LONG_CONFIG_VALUE, BYTES), new DataStorageSpec.LongBytesBound(MAX_LONG_CONFIG_VALUE, BYTES), () -> "Add assertion message");

        assertNotEquals(new DataStorageSpec.LongBytesBound("0MiB"), new DataStorageSpec.LongBytesBound("10KiB"));
    }

    @Test
    public void thereAndBackLongBytesBound()
    {
        qt().forAll(gen()).check(there -> {
            DataStorageSpec.LongBytesBound back = new DataStorageSpec.LongBytesBound(there.toString());
            DataStorageSpec.LongBytesBound BACK = new DataStorageSpec.LongBytesBound(there.toString().toUpperCase(Locale.ROOT).replace("I", "i"));
            return there.equals(back) && there.equals(BACK);
        });
    }

    @Test
    public void eq()
    {
        qt().forAll(gen(), gen()).check((a, b) -> a.equals(b) == b.equals(a));
    }

    @Test
    public void eqAndHash()
    {
        qt().forAll(gen(), gen()).check((a, b) -> !a.equals(b) || a.hashCode() == b.hashCode());
    }

    private static Gen<DataStorageSpec.LongBytesBound> gen()
    {
        Gen<DataStorageSpec.LongBytesBound.DataStorageUnit> unitGen = SourceDSL.arbitrary().enumValues(DataStorageSpec.LongBytesBound.DataStorageUnit.class);
        Gen<Long> valueGen = SourceDSL.longs().between(0, Long.MAX_VALUE/1024L/1024/1024); // max in GiB we can have without overflowing
        Gen<DataStorageSpec.LongBytesBound> gen = rs -> new DataStorageSpec.LongBytesBound(valueGen.generate(rs), unitGen.generate(rs));
        return gen.describedAs(DataStorageSpec.LongBytesBound::toString);
    }
}