package org.callimard.easyfuse.nio;

import org.callimard.easyfuse.core.EasyFuseFS;
import ru.serce.jnrfuse.struct.FileStat;

import java.nio.file.attribute.BasicFileAttributes;

public interface NIOAttributeGetter {

    int getAttribute(BasicFileAttributes attributes, FileStat stat, EasyFuseFS fuseFS);
}
