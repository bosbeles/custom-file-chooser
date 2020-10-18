package com.bosbeles.home.test.filechooser;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestrictedFileChooser extends JFileChooser {

    private boolean readOnly;

    public RestrictedFileChooser() {
        super(".");
    }

    public RestrictedFileChooser(Path allowedFolder) {
        initialize(Collections.singletonList(allowedFolder.toFile()), Paths.get("."));
    }

    public RestrictedFileChooser(Path allowedFolder, Path otherDrivePath) {
        List<File> rootList = Stream.concat(
                Stream.of(allowedFolder.toFile().getParentFile()),
                Stream.of(File.listRoots()).map(f -> f.toPath().resolve(otherDrivePath).toFile()))
                .collect(Collectors.toList());
        initialize(rootList, allowedFolder.getFileName());

    }

    public RestrictedFileChooser(Path allowedFolder, List<File> allowedDirectoryList) {
        initialize(allowedDirectoryList, allowedFolder);
    }

    @Override
    public void updateUI() {
        Boolean old = UIManager.getBoolean("FileChooser.readOnly");
        UIManager.put("FileChooser.readOnly", readOnly);
        super.updateUI();
        UIManager.put("FileChooser.readOnly", old);
    }

    public void setReadOnly(boolean flag) {
        this.readOnly = flag;
        updateUI();
    }

    public void setNewFolderDisabled(boolean flag) {
        ActionMap am = getActionMap();
        Action newFolder = am.get("New Folder");
        newFolder.setEnabled(!flag);
    }


    private void initialize(List<File> allowedDirectoryList, Path allowedFolder) {
        CustomFileSystemView fsv = new CustomFileSystemView(allowedDirectoryList, allowedFolder);
        setup(fsv);
        File[] roots = fsv.getRoots();
        this.setCurrentDirectory(roots.length > 0 ? roots[0] : null);
    }


    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();
        FileSystemView fileSystemView = getFileSystemView();
        if (selectedFile != null && fileSystemView instanceof CustomFileSystemView) {
            CustomFileSystemView customFileSystemView = (CustomFileSystemView) fileSystemView;
            if (customFileSystemView.isSelectable(selectedFile)) {
                return selectedFile;
            }
        }
        return selectedFile;
    }

    @Override
    public File[] getSelectedFiles() {
        File[] selectedFiles = super.getSelectedFiles();
        FileSystemView fileSystemView = getFileSystemView();
        if (fileSystemView instanceof CustomFileSystemView) {
            return Arrays.stream(selectedFiles).filter(f -> {
                CustomFileSystemView customFileSystemView = (CustomFileSystemView) fileSystemView;
                return customFileSystemView.isSelectable(f);
            }).toArray(File[]::new);
        }

        return selectedFiles;
    }


    private static class CustomFileSystemView extends FileSystemView {

        private File[] roots;
        private boolean containMultipleItems;

        CustomFileSystemView(Collection<File> rootList, Path other) {
            roots = rootList.stream().map(r -> r.toPath().resolve(other).toAbsolutePath().normalize())
                    .distinct()
                    .map(p -> p.toFile()).filter(File::exists)
                    .toArray(File[]::new);
            Arrays.sort(roots);
            containMultipleItems = Arrays.stream(roots).map(f -> f.getName()).distinct().count() != roots.length;
            System.out.println(Arrays.toString(roots));
        }

        @Override
        public File createNewFolder(File containingDir) throws IOException {
            return null;
        }

        @Override
        public File getParentDirectory(File file) {
            if (isSelectable(file)) {
                return file.getParentFile();
            }
            return file;
        }

        public boolean isSelectable(File file) {
            return Arrays.stream(roots).anyMatch(r -> isParentOf(r, file));
        }

        private boolean isParentOf(File possibleParent, File maybeChild) {
            File maybeParent = possibleParent.toPath().normalize().toFile();
            File parent = maybeChild.toPath().normalize().toFile().getParentFile();
            while (parent != null) {
                if (parent.equals(maybeParent))
                    return true;
                parent = parent.getParentFile();
            }
            return false;
        }


        @Override
        public String getSystemDisplayName(File f) {
            if (containMultipleItems && Arrays.binarySearch(roots, f) >= 0) {
                return f.getAbsolutePath();
            }

            return super.getSystemDisplayName(f);
        }

        @Override
        public File getHomeDirectory() {
            if (roots.length > 0) {
                return roots[0];
            }
            return null;
        }

        @Override
        public File getDefaultDirectory() {
            return getHomeDirectory();
        }

        @Override
        public File[] getRoots() {
            return roots;
        }

    }
}
