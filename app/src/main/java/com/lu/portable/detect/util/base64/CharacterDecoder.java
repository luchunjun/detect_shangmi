package com.lu.portable.detect.util.base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;

public abstract class CharacterDecoder {
    protected abstract int bytesPerAtom();

    protected abstract int bytesPerLine();

    protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt)
            throws IOException {
        throw new CEStreamExhausted();
    }


    public void decodeBuffer(InputStream aStream, OutputStream bStream)
            throws IOException {
        int totalBytes = 0;
        int i;
        PushbackInputStream ps = new PushbackInputStream(aStream);
        decodeBufferPrefix(ps, bStream);
        try {
            while (true) {
                int length = decodeLinePrefix(ps, bStream);
                for (i = 0; i + bytesPerAtom() < length; i += bytesPerAtom()) {
                    decodeAtom(ps, bStream, bytesPerAtom());
                    totalBytes += bytesPerAtom();
                }
                if (i + bytesPerAtom() == length) {
                    decodeAtom(ps, bStream, bytesPerAtom());
                    totalBytes += bytesPerAtom();
                } else {
                    decodeAtom(ps, bStream, length - i);
                    totalBytes += length - i;
                }
                decodeLineSuffix(ps, bStream);
            }

        } catch (CEStreamExhausted localCEStreamExhausted) {
            decodeBufferSuffix(ps, bStream);
        }
    }

    public byte[] decodeBuffer(InputStream paramInputStream)
            throws IOException {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        decodeBuffer(paramInputStream, localByteArrayOutputStream);
        return localByteArrayOutputStream.toByteArray();
    }

    public byte[] decodeBuffer(String inputString)
            throws IOException {
        byte[] inputBuffer = new byte[inputString.length()];

        inputString.getBytes(0, inputString.length(), inputBuffer, 0);
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputBuffer);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        decodeBuffer(inStream, outStream);
        return outStream.toByteArray();
    }

    protected void decodeBufferPrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
            throws IOException {
    }

    protected void decodeBufferSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
            throws IOException {
    }

    public ByteBuffer decodeBufferToByteBuffer(InputStream paramInputStream)
            throws IOException {
        return ByteBuffer.wrap(decodeBuffer(paramInputStream));
    }

    public ByteBuffer decodeBufferToByteBuffer(String paramString)
            throws IOException {
        return ByteBuffer.wrap(decodeBuffer(paramString));
    }

    protected int decodeLinePrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
            throws IOException {
        return bytesPerLine();
    }

    protected void decodeLineSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
            throws IOException {
    }

    protected int readFully(InputStream in, byte[] buffer, int offset, int len)
            throws IOException {
        for (int i = 0; i < len; i++) {
            int q = in.read();
            if (q == -1)
                return i == 0 ? -1 : i;
            buffer[(i + offset)] = (byte) q;
        }
        return len;
    }
}