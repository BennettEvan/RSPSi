package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.io.Buffer;
import com.jagex.util.ByteBufferUtils;
import lombok.val;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

public class RSAreaLoaderOSRS extends RSAreaLoader {

    private RSArea[] areas;

    @Override
    public RSArea forId(int id) {
        if (id < 0 || id >= areas.length) {
            return null;
        }
        return areas[id];
    }

    @Override
    public int count() {
        return areas.length;
    }

    @Override
    public void init(Archive archive) {
        if (archive == null) {
            areas = new RSArea[1000];
            IntStream.range(0, areas.length).forEach(index -> {
                RSArea dummyArea = new RSArea(index);
                dummyArea.setSpriteId(index);
                areas[index] = dummyArea;
            });
            return;
        }
        val highestId = Arrays.stream(archive.fileIds()).max().getAsInt();
        areas = new RSArea[highestId + 1];
        for (File file : archive.files()) {
            if (file != null && file.getData() != null) {
                RSArea area = decode(file.getId(), ByteBuffer.wrap(file.getData()));
                areas[file.getId()] = area;
            }
        }
    }

    private RSArea decode(int id, ByteBuffer buffer) {
        RSArea area = new RSArea(id);
        while (true) {
            int opcode = buffer.get() & 0xFF;
            if (opcode == 0) {
                break;
            }

            if (opcode == 1) {
                area.setSpriteId(ByteBufferUtils.getSmartInt(buffer));
            } else if (opcode == 2) {
                ByteBufferUtils.getSmartInt(buffer);
            } else if (opcode == 3) {
                ByteBufferUtils.getOSRSString(buffer);
            } else if (opcode == 4) {
                ByteBufferUtils.getMedium(buffer);
            } else if (opcode == 5) {
                ByteBufferUtils.getMedium(buffer);
            } else if (opcode == 6) {
                buffer.get();
            } else if (opcode == 7) {
                buffer.get();
            } else if (opcode == 8) {
                buffer.get();
            } else if (opcode >= 10 && opcode <= 14) {
                ByteBufferUtils.getOSRSString(buffer);
            } else if (opcode == 15) {
                int size = buffer.get() & 0xFF;
                for (int i = 0; i < size * 2; ++i) {
                    buffer.getShort();
                }

                buffer.getInt();
                int size2 = buffer.get() & 0xFF;
                for (int i = 0; i < size2; ++i) {
                    buffer.getInt();
                }

                for (int i = 0; i < size; i++) {
                    buffer.get();
                }
            } else if (opcode == 17) {
                ByteBufferUtils.getOSRSString(buffer);
            } else if (opcode == 18) {
                ByteBufferUtils.getSmartInt(buffer);
            } else if (opcode == 19) {
                buffer.getShort();
            } else if (opcode == 21) {
                buffer.getInt();
            } else if (opcode == 22) {
                buffer.getInt();
            } else if (opcode == 23) {
                buffer.get();
                buffer.get();
                buffer.get();
            } else if (opcode == 24) {
                buffer.getShort();
                buffer.getShort();
            } else if (opcode == 25) {
                ByteBufferUtils.getSmartInt(buffer);
            } else if (opcode == 28) {
                buffer.get();
            } else if (opcode == 29) {
                buffer.get();
            } else if (opcode == 30) {
                buffer.get();
            }
        }
        return area;
    }

    @Override
    public void init(Buffer data, Buffer indexBuffer) {
        // TODO Auto-generated method stub
    }
}
