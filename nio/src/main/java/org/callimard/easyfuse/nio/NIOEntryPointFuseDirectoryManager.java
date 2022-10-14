package org.callimard.easyfuse.nio;

import jnr.ffi.Pointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Singleton
public class NIOEntryPointFuseDirectoryManager extends NIOFuseDirectoryManager {

    // Variables.

    @NonNull
    private final EntryPointFactory entryPointFactory;

    // Constructors.

    @Inject
    NIOEntryPointFuseDirectoryManager(@NonNull EntryPointFactory entryPointFactory, @NonNull SeveralEntryPointPathRecover pathRecover,
                                      @Nullable DirectoryFileFilter directoryFileFilter) {
        super(pathRecover, directoryFileFilter);
        this.entryPointFactory = entryPointFactory;
    }

    // Methods.

    @Override
    public int mkdir(String path, long mode) {
        var fusePath = Paths.get(path);
        if (isFuseRootPath(fusePath)) {
            // Cannot create directory in root.
            log.warn("Impossible to create dir in Fuse FS root");
            return -ErrorCodes.EACCES();
        } else {
            return super.mkdir(path, mode);
        }
    }

    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, long offset, FuseFileInfo fi) {
        var fusePath = Paths.get(path);
        if (isFuseRootPath(fusePath)) {
            log.trace("Read directory from Fuse FS root path {}", path);
            return applyEntryPointNames(path, buf, filter, entryPointFactory.entryPointNames());
        } else {
            return super.readdir(path, buf, filter, offset, fi);
        }
    }

    private static int applyEntryPointNames(String path, Pointer buf, FuseFillDir filter, List<String> entryPointNames) {
        for (String name : entryPointNames) {
            if (filter.apply(buf, name, null, 0) != 0) {
                log.warn("Buffer out of memory during read dir for {}", path);
                return -ErrorCodes.ENOMEM();
            }
        }
        return 0;
    }

    @Override
    public int rmdir(String path) {
        var fusePath = Paths.get(path);
        if (isEntryPointRoot(fusePath)) {
            // Cannot remove these paths.
            log.warn("Impossible to remove the directory {}, it is the Fuse FS root or a Entry Point root", path);
            return -ErrorCodes.EACCES();
        } else {
            return super.rmdir(path);
        }
    }

    private boolean isFuseRootPath(Path path) {
        return path.getNameCount() == 0;
    }

    private boolean isEntryPointRoot(Path path) {
        return path.getNameCount() == 1;
    }
}