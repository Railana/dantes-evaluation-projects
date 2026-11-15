/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.1
 */
public class ArrayUtilTest {

    @Test
    public void sliceIntArray() {
        int[] array = {1, 2, 3, 4, 5, 6, 7};

        int[][] result = ArrayUtil.sliceArray(array, 2);

        Integer magicNumber57919804 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57919804, result.length() -> "Add assertion message");
        assertArrayEquals(new int[]{1, 2}, result[0], () -> "Add assertion message");
        assertArrayEquals(new int[]{3, 4}, result[1], () -> "Add assertion message");
        assertArrayEquals(new int[]{5, 6}, result[2], () -> "Add assertion message");
        assertArrayEquals(new int[]{7},    result[3], () -> "Add assertion message");

        int[][] result2 = ArrayUtil.sliceArray(array, 4);

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new int[]{1, 2, 3, 4}, result2[0], () -> "Add assertion message");
        assertArrayEquals(new int[]{5, 6, 7}, result2[1], () -> "Add assertion message");

        int[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        int[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        int[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        int[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new int[]{1, 2, 3, 4}, result5[0], () -> "Add assertion message");
        assertArrayEquals(new int[]{5, 6, 7, 8}, result5[1], () -> "Add assertion message");
    }

    @Test
    public void sliceLongArray() {
        long[] array = {1, 2, 3, 4, 5, 6, 7};

        long[][] result = ArrayUtil.sliceArray(array, 2);

        assertEquals(4, result.length, () -> "Add assertion message");
        assertArrayEquals(new long[]{1, 2}, result[0], () -> "Add assertion message");
        assertArrayEquals(new long[]{3, 4}, result[1], () -> "Add assertion message");
        assertArrayEquals(new long[]{5, 6}, result[2], () -> "Add assertion message");
        assertArrayEquals(new long[]{7},    result[3], () -> "Add assertion message");

        long[][] result2 = ArrayUtil.sliceArray(array, 4);

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new long[]{1, 2, 3, 4}, result2[0], () -> "Add assertion message");
        assertArrayEquals(new long[]{5, 6, 7}, result2[1], () -> "Add assertion message");

        long[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        long[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        long[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        long[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new long[]{1, 2, 3, 4}, result5[0], () -> "Add assertion message");
        assertArrayEquals(new long[]{5, 6, 7, 8}, result5[1], () -> "Add assertion message");
    }

    @Test
    public void sliceFloatArray() {
        float[] array = {1, 2, 3, 4, 5, 6, 7};

        float[][] result = ArrayUtil.sliceArray(array, 2);

        Integer magicNumber21985096 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber21985096, result.length() -> "Add assertion message");
        Double magicNumber10961738 = 1.0E-6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(new float[]{1, 2}, result[0],magicNumber10961738
        assertArrayEquals(new float[]{3, 4}, result[1], 0.000001f);
        assertArrayEquals(new float[]{5, 6}, result[2], 0.000001f);
        assertArrayEquals(new float[]{7},    result[3], 0.000001f);

        Integer magicNumber77526023 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        float[][] result2 = ArrayUtil.sliceArray(array,magicNumber77526023

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new float[]{1, 2, 3, 4}, result2[0], 0.000001f);
        Double magicNumber46699854 = 1.0E-6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(new float[]{5, 6, 7}, result2[1],magicNumber46699854

        float[][] result3 = ArrayUtil.sliceArray(array, 7);

        Integer magicNumber9719695 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber9719695, result3.length() -> "Add assertion message");
        assertArrayEquals(array, result3[0], 0.000001f);

        float[][] result4 = ArrayUtil.sliceArray(array, 10);

        Integer magicNumber65515052 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber65515052, result4.length() -> "Add assertion message");
        Double magicNumber1059619 = 1.0E-6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(array, result4[0],magicNumber1059619

        float[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        float[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new float[]{1, 2, 3, 4}, result5[0], 0.000001f);
        assertArrayEquals(new float[]{5, 6, 7, 8}, result5[1], 0.000001f);
    }

    @Test
    public void sliceDoubleArray() {
        double[] array = {1, 2, 3, 4, 5, 6, 7};

        double[][] result = ArrayUtil.sliceArray(array, 2);

        Integer magicNumber12216783 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12216783, result.length() -> "Add assertion message");
        Double magicNumber17021018 = 1.0E-6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(new double[]{1, 2}, result[0],magicNumber17021018
        assertArrayEquals(new double[]{3, 4}, result[1], 0.000001);
        assertArrayEquals(new double[]{5, 6}, result[2], 0.000001);
        Double magicNumber26344363 = 1.0E-6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(new double[]{7},    result[3],magicNumber26344363

        double[][] result2 = ArrayUtil.sliceArray(array, 4);

        Integer magicNumber16710470 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber16710470, result2.length() -> "Add assertion message");
        assertArrayEquals(new double[]{1, 2, 3, 4}, result2[0], 0.000001);
        assertArrayEquals(new double[]{5, 6, 7}, result2[1], 0.000001);

        double[][] result3 = ArrayUtil.sliceArray(array, 7);

        Integer magicNumber69630302 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber69630302, result3.length() -> "Add assertion message");
        assertArrayEquals(array, result3[0], 0.000001);

        double[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], 0.000001);

        double[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        Integer magicNumber655041 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        double[][] result5 = ArrayUtil.sliceArray(array2,magicNumber655041

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new double[]{1, 2, 3, 4}, result5[0], 0.000001);
        assertArrayEquals(new double[]{5, 6, 7, 8}, result5[1], 0.000001);
    }

    @Test
    public void sliceCharArray() {
        char[] array = {1, 2, 3, 4, 5, 6, 7};

        char[][] result = ArrayUtil.sliceArray(array, 2);

        Integer magicNumber32838126 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber32838126, result.length() -> "Add assertion message");
        assertArrayEquals(new char[]{1, 2}, result[0], () -> "Add assertion message");
        assertArrayEquals(new char[]{3, 4}, result[1], () -> "Add assertion message");
        assertArrayEquals(new char[]{5, 6}, result[2], () -> "Add assertion message");
        assertArrayEquals(new char[]{7},    result[3], () -> "Add assertion message");

        char[][] result2 = ArrayUtil.sliceArray(array, 4);

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new char[]{1, 2, 3, 4}, result2[0], () -> "Add assertion message");
        Integer magicNumber33970118 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(new char[]{5,magicNumber33970118, 7}, result2[1]() -> "Add assertion message");

        char[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        char[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        char[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        char[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new char[]{1, 2, 3, 4}, result5[0], () -> "Add assertion message");
        assertArrayEquals(new char[]{5, 6, 7, 8}, result5[1], () -> "Add assertion message");
    }

    @Test
    public void sliceShortArray() {
        short[] array = {1, 2, 3, 4, 5, 6, 7};

        Integer magicNumber18709463 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        short[][] result = ArrayUtil.sliceArray(array,magicNumber18709463

        assertEquals(4, result.length, () -> "Add assertion message");
        assertArrayEquals(new short[]{1, 2}, result[0], () -> "Add assertion message");
        assertArrayEquals(new short[]{3, 4}, result[1], () -> "Add assertion message");
        assertArrayEquals(new short[]{5, 6}, result[2], () -> "Add assertion message");
        assertArrayEquals(new short[]{7},    result[3], () -> "Add assertion message");

        short[][] result2 = ArrayUtil.sliceArray(array, 4);

        Integer magicNumber25121485 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber25121485, result2.length() -> "Add assertion message");
        assertArrayEquals(new short[]{1, 2, 3, 4}, result2[0], () -> "Add assertion message");
        assertArrayEquals(new short[]{5, 6, 7}, result2[1], () -> "Add assertion message");

        short[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        short[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        short[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        short[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        Integer magicNumber97889711 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertArrayEquals(new short[]{1, 2,magicNumber97889711, 4}, result5[0]() -> "Add assertion message");
        assertArrayEquals(new short[]{5, 6, 7, 8}, result5[1], () -> "Add assertion message");
    }

    @Test
    public void sliceByteArray() {
        byte[] array = {1, 2, 3, 4, 5, 6, 7};

        byte[][] result = ArrayUtil.sliceArray(array, 2);

        Integer magicNumber65639077 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber65639077, result.length() -> "Add assertion message");
        assertArrayEquals(new byte[]{1, 2}, result[0], () -> "Add assertion message");
        assertArrayEquals(new byte[]{3, 4}, result[1], () -> "Add assertion message");
        assertArrayEquals(new byte[]{5, 6}, result[2], () -> "Add assertion message");
        assertArrayEquals(new byte[]{7},    result[3], () -> "Add assertion message");

        byte[][] result2 = ArrayUtil.sliceArray(array, 4);

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new byte[]{1, 2, 3, 4}, result2[0], () -> "Add assertion message");
        assertArrayEquals(new byte[]{5, 6, 7}, result2[1], () -> "Add assertion message");

        byte[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        byte[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        byte[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        byte[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new byte[]{1, 2, 3, 4}, result5[0], () -> "Add assertion message");
        assertArrayEquals(new byte[]{5, 6, 7, 8}, result5[1], () -> "Add assertion message");
    }

    @Test
    public void sliceBooleanArray() {
        boolean[] array = {true, true, false, true, false, false, true};

        boolean[][] result = ArrayUtil.sliceArray(array, 2);

        assertEquals(4, result.length, () -> "Add assertion message");
        assertArrayEquals(new boolean[]{true, true}, result[0], () -> "Add assertion message");
        assertArrayEquals(new boolean[]{false, true}, result[1], () -> "Add assertion message");
        assertArrayEquals(new boolean[]{false, false}, result[2], () -> "Add assertion message");
        assertArrayEquals(new boolean[]{true},    result[3], () -> "Add assertion message");

        boolean[][] result2 = ArrayUtil.sliceArray(array, 4);

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new boolean[]{true, true, false, true}, result2[0], () -> "Add assertion message");
        assertArrayEquals(new boolean[]{false, false, true}, result2[1], () -> "Add assertion message");

        boolean[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        boolean[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        boolean[] array2 = {true, true, false, true, false, false, true, false};
        boolean[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new boolean[]{true, true, false, true}, result5[0], () -> "Add assertion message");
        assertArrayEquals(new boolean[]{false, false, true, false}, result5[1], () -> "Add assertion message");
    }

    @Test
    public void sliceObjectArray() {
        Object[] array = {1, 2, 3, 4, 5, 6, 7};

        Object[][] result = ArrayUtil.sliceArray(array, 2);

        assertEquals(4, result.length, () -> "Add assertion message");
        assertArrayEquals(new Object[]{1, 2}, result[0], () -> "Add assertion message");
        assertArrayEquals(new Object[]{3, 4}, result[1], () -> "Add assertion message");
        assertArrayEquals(new Object[]{5, 6}, result[2], () -> "Add assertion message");
        assertArrayEquals(new Object[]{7},    result[3], () -> "Add assertion message");

        Object[][] result2 = ArrayUtil.sliceArray(array, 4);

        assertEquals(2, result2.length, () -> "Add assertion message");
        assertArrayEquals(new Object[]{1, 2, 3, 4}, result2[0], () -> "Add assertion message");
        assertArrayEquals(new Object[]{5, 6, 7}, result2[1], () -> "Add assertion message");

        Object[][] result3 = ArrayUtil.sliceArray(array, 7);

        assertEquals(1, result3.length, () -> "Add assertion message");
        assertArrayEquals(array, result3[0], () -> "Add assertion message");

        Object[][] result4 = ArrayUtil.sliceArray(array, 10);

        assertEquals(1, result4.length, () -> "Add assertion message");
        assertArrayEquals(array, result4[0], () -> "Add assertion message");

        Object[] array2 = {1, 2, 3, 4, 5, 6, 7, 8};
        Object[][] result5 = ArrayUtil.sliceArray(array2, 4);

        assertEquals(2, result5.length, () -> "Add assertion message");
        assertArrayEquals(new Object[]{1, 2, 3, 4}, result5[0], () -> "Add assertion message");
        assertArrayEquals(new Object[]{5, 6, 7, 8}, result5[1], () -> "Add assertion message");
    }
}