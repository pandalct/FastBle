package com.clj.blesample.comm;


import java.util.Arrays;

/**
 *
 * CRC数组处理工具类及数组合并
 */
public class CRCUtil {


    public static int do_crc(byte[] pSrcData) {
        int[] CRC16_ccitt_table = {/*   CRC余式表   */ 0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7, 0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef};
        int da;
        int crc = 0x0000;
        for (int i = 0; i < pSrcData.length; i++) {
            da = (crc >> 8) >> 4;
            crc <<=4;
            crc ^=  CRC16_ccitt_table[(da ^ pSrcData[i]>>4) & 0xFF];
            da =  (crc >> 8) >> 4;
            crc <<=4;
            crc ^=  CRC16_ccitt_table[(da ^ (pSrcData[i] & 0x0f)) & 0xFF];
        }
        return crc;
    }

    /**
     * 多个数组合并
     *
     * @param first
     * @param rest
     * @return
     */
    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}