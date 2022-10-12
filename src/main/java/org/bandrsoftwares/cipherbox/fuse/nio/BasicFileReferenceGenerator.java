package org.bandrsoftwares.cipherbox.fuse.nio;

import lombok.NonNull;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class BasicFileReferenceGenerator implements FileReferenceGenerator {

    // Variables.

    private final int fileReferenceBufferSize;

    // Constructors.

    @Inject
    BasicFileReferenceGenerator(@BufferSize @Nullable Integer fileReferenceBufferSize) {
        this.fileReferenceBufferSize = fileReferenceBufferSize != null ? fileReferenceBufferSize : -1;
    }

    // Methods.

    @Override
    public FileReference generate(@NonNull Path path, @NonNull FileChannel fileChannel) {
        if (fileReferenceBufferSize <= 0) {
            return new BasicFileReference(path, fileChannel);
        } else {
            return new BasicFileReference(fileReferenceBufferSize, path, fileChannel);
        }
    }

    @Documented
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface BufferSize {
    }
}