package myPkg;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Vector;
import java.text.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.nio.file.Path;

public class FileTable {
    
    private DefaultTableModel mdl;
    private Vector<File> fileArray;
    private long rootSize;
    private File rootFile;
    private HashMap<String, Long> diagMap;

    public FileTable(File root) {
        String[] columns = {"Name", "Type", "Directory", "Size", "Last Modified"};
        this.mdl = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int y, int x) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 3 || columnIndex == 4) {
                    return Long.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        this.rootFile = root;
        this.fileArray = new Vector<>();
        this.diagMap = new HashMap<>();
        runVisitor(root);
    }

    public TableModel getMdl() {
        return mdl;
    }

    public Vector<File> getFArray() {
        return fileArray;
    }

    public long getRootSize() {
        return rootSize;
    }

    public File getRootFile() {
        return rootFile;
    }
    public HashMap<String, Long> getDiagMap() {
        return diagMap;
    }
    private void runVisitor(File root) {

        try{
            Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) {
                    File ptf = f.toFile();
    
                    String name = ptf.getName();
                    String type = FileOps.getFileExtension(ptf);
                    if(type.equals("")) type = "NULL";
                    String dir = "ERROR";
                    try {
                        dir = ptf.getCanonicalPath().replace("\\" + name, "");
                    } catch (IOException e) {}
                    long size = ptf.length();
                    long lastMod = ptf.lastModified();
                    
                    mdl.addRow(new Object[]{name, type, dir, size, lastMod});

                    fileArray.add(ptf);

                    rootSize += size;
                    if(diagMap.containsKey(type)) {
                        long old = diagMap.get(type);
                        diagMap.replace(type, old+size);
                    }
                    else {
                        diagMap.put(type, size);
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path f, IOException exc) {
                    System.out.println("skipped: " + f + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("IOexc");
        }
    }


}


