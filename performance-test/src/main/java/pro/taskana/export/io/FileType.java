package pro.taskana.export.io;

public enum FileType {
    CSV("csv");
    
    private String extension;
    
    private FileType(String extension) {
        this.extension = extension;
    }
    
    public String getExtension() {
        return extension;
    }
}
