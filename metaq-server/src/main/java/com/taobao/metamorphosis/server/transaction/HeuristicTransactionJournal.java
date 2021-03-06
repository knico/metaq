package com.taobao.metamorphosis.server.transaction;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.taobao.metamorphosis.server.utils.FileUtils;
import com.taobao.metamorphosis.utils.codec.impl.JavaDeserializer;
import com.taobao.metamorphosis.utils.codec.impl.JavaSerializer;


/**
 * 手工处理的事务日志
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-11-24
 * 
 */
public class HeuristicTransactionJournal implements Closeable {

    private final FileChannel fc;
    private final JavaSerializer serializer;
    private final JavaDeserializer deserializer;
    private static final String FILE_NAME = "heuristic.log";


    public HeuristicTransactionJournal(final String path) throws IOException {
        FileUtils.makesureDir(new File(path));
        this.serializer = new JavaSerializer();
        this.deserializer = new JavaDeserializer();
        final File transactionsDir = new File(path + File.separator + "transactions");
        FileUtils.makesureDir(transactionsDir);
        this.fc = new RandomAccessFile(new File(transactionsDir + File.separator + FILE_NAME), "rw").getChannel();
    }


    @Override
    public synchronized void close() throws IOException {
        this.fc.force(true);
        this.fc.close();
    }


    public void write(final Object obj) throws Exception {
        if (!this.fc.isOpen()) {
            return;
        }
        if (obj == null) {
            return;
        }
        final byte[] result = this.serializer.encodeObject(obj);
        final ByteBuffer buf = ByteBuffer.wrap(result);

        synchronized (this) {
            while (buf.hasRemaining()) {
                this.fc.write(buf, 0);
            }
            this.fc.truncate(result.length);
        }
    }


    public Object read() throws Exception {
        byte[] data = null;
        synchronized (this) {
            final long size = this.fc.size();
            if (size == 0) {
                return null;
            }
            final ByteBuffer buf = ByteBuffer.allocate((int) size);
            this.fc.position(0);
            while (buf.hasRemaining()) {
                this.fc.read(buf);
            }
            buf.flip();
            data = new byte[buf.remaining()];
            buf.get(data);
        }
        if (data != null) {
            return this.deserializer.decodeObject(data);
        }
        else {
            return null;
        }
    }
}
