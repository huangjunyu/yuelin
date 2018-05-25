package com.yuelin.o2cabin;

import java.io.*;

public class Base64
{
    private static final char[] legalChars;

    static {
        legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    }

    private static int decode(final char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        }
        if (c >= 'a' && c <= 'z') {
            return '\u001a' + (c - 'a');
        }
        if (c >= '0' && c <= '9') {
            return '\u001a' + ('\u001a' + (c - '0'));
        }
        if (c == '+') {
            return 62;
        }
        if (c == '/') {
            return 63;
        }
        if (c != '=') {
            final StringBuilder sb = new StringBuilder();
            sb.append("unexpected code: ");
            sb.append(c);
            throw new RuntimeException(sb.toString());
        }
        return 0;
    }

    private static void decode(final String s, final OutputStream outputStream) throws IOException {
        final int length = s.length();
        int n = 0;
        while (true) {
            if (n < length && s.charAt(n) <= ' ') {
                ++n;
            }
            else {
                if (n == length) {
                    return;
                }
                final int n2 = (decode(s.charAt(n)) << 18) + (decode(s.charAt(n + 1)) << 12);
                final int n3 = n + 2;
                final int n4 = n2 + (decode(s.charAt(n3)) << 6);
                final int n5 = n + 3;
                final int n6 = n4 + decode(s.charAt(n5));
                outputStream.write(0xFF & n6 >> 16);
                if (s.charAt(n3) == '=') {
                    return;
                }
                outputStream.write(0xFF & n6 >> 8);
                if (s.charAt(n5) == '=') {
                    return;
                }
                outputStream.write(n6 & 0xFF);
                n += 4;
            }
        }
    }

    public static byte[] decode(final String s) {
        if (s == null) {
            return new byte[0];
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            decode(s, byteArrayOutputStream);
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            }
            catch (IOException ex) {}
            return byteArray;
        }
        catch (IOException ex2) {
            throw new RuntimeException();
        }
    }

    public static String encode(final byte[] array) {
        if (array == null) {
            return "";
        }
        final int length = array.length;
        final StringBuffer sb = new StringBuffer(3 * array.length / 2);
        final int n = length - 3;
        int i = 0;
        int n2 = 0;
        while (i <= n) {
            final int n3 = (0xFF & array[i]) << 16 | (0xFF & array[i + 1]) << 8 | (0xFF & array[i + 2]);
            sb.append(Base64.legalChars[0x3F & n3 >> 18]);
            sb.append(Base64.legalChars[0x3F & n3 >> 12]);
            sb.append(Base64.legalChars[0x3F & n3 >> 6]);
            sb.append(Base64.legalChars[n3 & 0x3F]);
            i += 3;
            final int n4 = n2 + 1;
            if (n2 >= 14) {
                sb.append(" ");
                n2 = 0;
            }
            else {
                n2 = n4;
            }
        }
        final int n5 = 0 + length;
        if (i == n5 - 2) {
            final int n6 = (0xFF & array[i]) << 16 | (0xFF & array[i + 1]) << 8;
            sb.append(Base64.legalChars[0x3F & n6 >> 18]);
            sb.append(Base64.legalChars[0x3F & n6 >> 12]);
            sb.append(Base64.legalChars[0x3F & n6 >> 6]);
            sb.append("=");
        }
        else if (i == n5 - 1) {
            final int n7 = (0xFF & array[i]) << 16;
            sb.append(Base64.legalChars[0x3F & n7 >> 18]);
            sb.append(Base64.legalChars[0x3F & n7 >> 12]);
            sb.append("==");
        }
        return sb.toString();
    }
}
