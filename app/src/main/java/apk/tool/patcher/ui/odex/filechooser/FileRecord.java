package apk.tool.patcher.ui.odex.filechooser;

public class FileRecord {

    public String fileName;
    public boolean isDir;
    public long totalSize;

    public FileRecord() {
    }

    public FileRecord(String fileName, boolean isDir) {
        this(fileName, isDir, 0);
    }

    public FileRecord(String fileName, boolean isDir, long size) {
        this.fileName = fileName;
        this.isDir = isDir;
        this.totalSize = size;
    }
}
