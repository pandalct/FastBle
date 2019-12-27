package com.clj.blesample.comm;


import java.util.Arrays;

/**
 *
 * CRC数组处理工具类及数组合并
 */
public class CRCUtil {


    public static int do_crc(byte[] pSrcData) {
        int[] CRC16_ccitt_table = {/*   CRC余式表   */ 0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7, 0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef};
        short da;
        int crc = 0x0000;

        int len = pSrcData.length;
        int index = 0;
        while(len-- != 0){
            short currentByte = (short) (pSrcData[index] & 0xFF);
            da = (short) ((crc >> 8) >> 4 & 0xFF);
            crc = crc << 4  & 0xFFFF;
            crc ^=  CRC16_ccitt_table[(da ^ currentByte>>4) & 0xFF];
            da =  (short)((crc >> 8) >> 4);
            crc  = crc  << 4  & 0xFFFF;
            crc ^=  CRC16_ccitt_table[(da ^ (currentByte & 0x0f)) & 0xFF];
            index++;
        }

        return crc  & 0xFFFF;
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