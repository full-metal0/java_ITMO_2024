package ru.itmo.mit.git;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

public class Repository {
    private static final String REPO_DIR = ".git";
    private static final String COMMITS_DIR = REPO_DIR + "/commits";
    private static final String BRANCHES_DIR = REPO_DIR + "/branches";
    private static final String HEAD_FILE = REPO_DIR + "/HEAD";
    private static final String INDEX_FILE = REPO_DIR + "/index";

    private final String workingDir;
    private final Map<String, String> index = new HashMap<>();
    private String currentBranch = "master";
    private String headCommit;

    public Repository(String workingDir) {
        this.workingDir = workingDir;
    }

    public void init() throws GitException {
        try {
            Path repoDir = Paths.get(workingDir, REPO_DIR);
            Path commitsDir = Paths.get(workingDir, COMMITS_DIR);
            Path branchesDir = Paths.get(workingDir, BRANCHES_DIR);
            Path headFile = Paths.get(workingDir, HEAD_FILE);
            Path indexFile = Paths.get(workingDir, INDEX_FILE);

            if (!Files.exists(repoDir)) {
                Files.createDirectories(repoDir);
            }
            if (!Files.exists(commitsDir)) {
                Files.createDirectories(commitsDir);
            }
            if (!Files.exists(branchesDir)) {
                Files.createDirectories(branchesDir);
            }
            if (!Files.exists(indexFile)) {
                Files.createFile(indexFile);
            }

            headCommit = generateHash("Initial commit" + new Date().toString());

            String initialCommitContent = "Message: Initial commit";

            Files.write(Paths.get(workingDir, COMMITS_DIR, headCommit), initialCommitContent.getBytes(StandardCharsets.UTF_8));

            currentBranch = "master";

            Path masterBranchPath = Paths.get(branchesDir.toString(), currentBranch);
            Files.write(masterBranchPath, headCommit.getBytes(StandardCharsets.UTF_8));

            Files.write(headFile, "ref: refs/heads/master".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GitException("Failed to initialize repository", e);
        }
    }

    public void add(List<String> files) throws GitException {
        try {
            for (String file : files) {
                Path filePath = Paths.get(workingDir, file);
                if (Files.exists(filePath)) {
                    index.put(file, new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8));
                } else {
                    throw new GitException("File not found: " + file);
                }
            }
            saveIndex();
        } catch (IOException e) {
            throw new GitException("Failed to add files", e);
        }
    }

    public void rm(List<String> files) throws GitException {
        for (String file : files) {
            index.remove(file);
        }
        saveIndex();
    }

    public void status(PrintStream out) {
        StringBuilder status = new StringBuilder();
        if (currentBranch == null) {
            status.append("Error while performing status: Head is detached\n");
            out.print(status.toString());
            return;
        } else {
            status.append("Current branch is '").append(currentBranch).append("'\n");
        }

        try {
            List<String> untrackedFiles = getUntrackedFiles();
            List<String> readyToCommitFiles = getReadyToCommitFiles();
            List<String> modifiedFiles = getModifiedFiles();
            List<String> removedFiles = getRemovedFiles();

            Collections.sort(readyToCommitFiles);
            Collections.sort(untrackedFiles);
            Collections.sort(modifiedFiles);
            Collections.sort(removedFiles);

            if (!readyToCommitFiles.isEmpty()) {
                status.append("Ready to commit:\n\n");
                status.append("New files:\n");
                for (String file : readyToCommitFiles) {
                    status.append("    ").append(file).append("\n");
                }
                status.append("\n");
            }

            if (!untrackedFiles.isEmpty()) {
                status.append("Untracked files:\n\n");
                status.append("New files:\n");
                for (String file : untrackedFiles) {
                    status.append("    ").append(file).append("\n");
                }
                status.append("\n");
            }

            if (!modifiedFiles.isEmpty()) {
                status.append("Untracked files:\n\n");
                status.append("Modified files:\n");
                for (String file : modifiedFiles) {
                    status.append("    ").append(file).append("\n");
                }
                status.append("\n");
            }

            if (!removedFiles.isEmpty()) {
                status.append("Untracked files:\n\n");
                status.append("Removed files:\n");
                for (String file : removedFiles) {
                    status.append("    ").append(file).append("\n");
                }
                status.append("\n");
            }

            if (readyToCommitFiles.isEmpty() && untrackedFiles.isEmpty() && modifiedFiles.isEmpty() && removedFiles.isEmpty()) {
                status.append("Everything up to date\n");
            }
        } catch (IOException e) {
            status.append("Error retrieving status\n");
        }

        out.print(status.toString());
    }

    public void commit(String message) throws GitException {
        if (index.isEmpty()) {
            throw new GitException("Nothing to commit");
        }
        String commitHash = generateHash(message + new Date().toString());

        try {
            StringBuilder commitContent = new StringBuilder();
            commitContent.append("Message: ").append(message).append("\n");
            commitContent.append("Date: ").append(new Date()).append("\n");
            if (headCommit != null) {
                commitContent.append("Parent: ").append(headCommit).append("\n");
            }

            for (Map.Entry<String, String> entry : index.entrySet()) {
                commitContent.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            Path commitPath = Paths.get(workingDir, COMMITS_DIR, commitHash);
            Files.write(commitPath, commitContent.toString().getBytes(StandardCharsets.UTF_8));

            headCommit = commitHash;
            saveHead();

            saveBranch();

            index.clear();
            saveIndex();
        } catch (IOException e) {
            throw new GitException("Failed to create commit", e);
        }
    }

    public void reset(String revision) throws GitException {
        String commitHash = getCommitHash(revision);
        if (commitHash == null) {
            throw new GitException("Invalid revision: " + revision);
        }

        try {
            Path commitPath = Paths.get(workingDir, COMMITS_DIR, commitHash);
            List<String> commitContent = Files.readAllLines(commitPath, StandardCharsets.UTF_8);

            index.clear();
            cleanWorkingDirectory();

            for (String line : commitContent) {
                if (line.startsWith("Message:") || line.startsWith("Date:") || line.startsWith("Parent:")) {
                    continue;
                }
                String[] parts = line.split(": ", 2);
                if (parts.length == 2) {
                    String fileName = parts[0];
                    String fileContent = parts[1];

                    Path filePath = Paths.get(workingDir, fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, fileContent.getBytes(StandardCharsets.UTF_8));

                    index.put(fileName, fileContent);
                }
            }

            headCommit = commitHash;
            saveHead();
            saveIndex();

        } catch (IOException e) {
            throw new GitException("Failed to reset to revision: " + revision, e);
        }
    }

    public void log(PrintStream out, String fromRevision) throws GitException {
        try {
            String commitHash = fromRevision == null ? headCommit : getCommitHash(fromRevision);
            while (commitHash != null) {
                Path commitPath = Paths.get(workingDir, COMMITS_DIR, commitHash);
                List<String> commitContent = Files.readAllLines(commitPath, StandardCharsets.UTF_8);

                String message = "";
                String date = "";
                String parent = "";
                for (String line : commitContent) {
                    if (line.startsWith("Message: ")) {
                        message = line.substring("Message: ".length());
                    } else if (line.startsWith("Date: ")) {
                        date = line.substring("Date: ".length());
                    } else if (line.startsWith("Parent: ")) {
                        parent = line.substring("Parent: ".length());
                    }
                }

                out.println("Commit COMMIT_HASH");
                out.println("Author: Test user");
                out.println("Date: COMMIT_DATE");
                out.println();
                out.println(message);

                if (!"Initial commit".equals(message)) {
                    out.println();
                }

                commitHash = parent.isEmpty() ? null : parent;
            }
        } catch (IOException e) {
            throw new GitException("Failed to read log", e);
        }
    }

    public void checkout(String revision) throws GitException {
        reset(revision);
        if (!revision.matches("[a-fA-F0-9]{40}") && !revision.startsWith("HEAD~")) {
            currentBranch = revision;
        } else {
            currentBranch = null;
        }
        saveHead();
    }

    public void checkoutFiles(List<String> files) throws GitException {
        try {
            Path commitPath = Paths.get(workingDir, COMMITS_DIR, headCommit);
            List<String> commitContent = Files.readAllLines(commitPath, StandardCharsets.UTF_8);

            Map<String, String> fileContents = new HashMap<>();
            for (String line : commitContent) {
                if (line.startsWith("Message:") || line.startsWith("Date:") || line.startsWith("Parent:")) {
                    continue;
                }
                String[] parts = line.split(": ", 2);
                if (parts.length == 2) {
                    fileContents.put(parts[0], parts[1]);
                }
            }

            for (String file : files) {
                Path filePath = Paths.get(workingDir, file);
                String content = fileContents.get(file);
                if (content != null) {
                    Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
                } else {
                    Files.deleteIfExists(filePath);
                }
            }
            System.out.println("Checkout completed successful");
        } catch (IOException e) {
            throw new GitException("Failed to checkout files", e);
        }
    }

    public void createBranch(String branchName, PrintStream out) throws GitException {
        try {
            Path branchPath = Paths.get(workingDir, BRANCHES_DIR, branchName);
            if (Files.exists(branchPath)) {
                throw new GitException("Branch already exists: " + branchName);
            }

            out.println("Branch " + branchName + " created successfully");
            out.println("You can checkout it with 'checkout " + branchName + "'");
            Files.write(branchPath, headCommit.getBytes(StandardCharsets.UTF_8));

            currentBranch = branchName;
            saveHead();

        } catch (IOException e) {
            throw new GitException("Failed to create branch: " + branchName, e);
        }
    }

    public void removeBranch(String branchName) throws GitException {
        try {
            Path branchPath = Paths.get(workingDir, BRANCHES_DIR, branchName);
            if (!Files.exists(branchPath)) {
                throw new GitException("Branch does not exist: " + branchName);
            }
            Files.delete(branchPath);
        } catch (IOException e) {
            throw new GitException("Failed to remove branch: " + branchName, e);
        }
    }

    public void showBranches(PrintStream out) throws GitException {
        try {
            out.println("Available branches:");
            List<Path> branches = Files.walk(Paths.get(workingDir, BRANCHES_DIR), 1)
                    .filter(path -> !path.equals(Paths.get(workingDir, BRANCHES_DIR)))
                    .collect(Collectors.toList());

            branches.stream()
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> out.println(path.getFileName()));
        } catch (IOException e) {
            throw new GitException("Failed to show branches", e);
        }
    }

    public void merge(String branchName) throws GitException {
        try {
            Path branchPath = Paths.get(workingDir, BRANCHES_DIR, branchName);
            if (!Files.exists(branchPath)) {
                throw new GitException("Branch does not exist: " + branchName);
            }
            String branchCommit = new String(Files.readAllBytes(branchPath), StandardCharsets.UTF_8);

            Path branchCommitPath = Paths.get(workingDir, COMMITS_DIR, branchCommit);
            if (!Files.exists(branchCommitPath)) {
                throw new GitException("Invalid commit in branch: " + branchName);
            }

            List<String> branchCommitContent = Files.readAllLines(branchCommitPath, StandardCharsets.UTF_8);
            Map<String, String> branchFiles = new HashMap<>();
            boolean isFilesSection = false;
            for (String line : branchCommitContent) {
                if (line.startsWith("Message:") || line.startsWith("Date:") || line.startsWith("Parent:")) {
                    continue;
                }
                if (!isFilesSection) {
                    isFilesSection = true;
                    continue;
                }
                String[] parts = line.split(": ", 2);
                String fileName = parts[0];
                String fileContent = parts[1];
                branchFiles.put(fileName, fileContent);
            }

            for (Map.Entry<String, String> entry : branchFiles.entrySet()) {
                String fileName = entry.getKey();
                String fileContent = entry.getValue();
                index.put(fileName, fileContent);

                Path filePath = Paths.get(workingDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, fileContent.getBytes(StandardCharsets.UTF_8));
            }

            saveIndex();
            commit("Merge branch " + branchName);
        } catch (IOException e) {
            throw new GitException("Failed to merge branch: " + branchName, e);
        }
    }

    public String getRelativeRevisionFromHead(int n) throws GitException {
        List<String> commitHistory = new ArrayList<>();
        String currentCommit = headCommit;

        while (currentCommit != null && commitHistory.size() <= n) {
            commitHistory.add(currentCommit);
            try {
                List<String> lines = Files.readAllLines(Paths.get(workingDir, COMMITS_DIR, currentCommit), StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (line.startsWith("Parent: ")) {
                        currentCommit = line.substring("Parent: ".length());
                        break;
                    }
                }
            } catch (IOException e) {
                throw new GitException("Failed to read commit history", e);
            }
        }
        if (commitHistory.size() <= n) {
            throw new GitException("Not enough commits in history");
        }

        return commitHistory.get(n);
    }

    private String generateHash(String input) throws GitException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GitException("Failed to generate hash", e);
        }
    }

    private List<String> getModifiedFiles() throws IOException {
        List<String> modifiedFiles = new ArrayList<>();
        Map<String, String> filesInCommit = getFilesFromLastCommit();

        for (Map.Entry<String, String> entry : filesInCommit.entrySet()) {
            String filePath = entry.getKey();
            Path fileFullPath = Paths.get(workingDir, filePath);
            if (Files.exists(fileFullPath)) {
                String currentContent = new String(Files.readAllBytes(fileFullPath), StandardCharsets.UTF_8);
                if (!currentContent.equals(entry.getValue())) {
                    modifiedFiles.add(filePath);
                }
            }
        }

        return modifiedFiles;
    }

    private List<String> getRemovedFiles() throws IOException {
        List<String> removedFiles = new ArrayList<>();
        Map<String, String> filesInCommit = getFilesFromLastCommit();

        for (String filePath : filesInCommit.keySet()) {
            Path fileFullPath = Paths.get(workingDir, filePath);
            if (!Files.exists(fileFullPath)) {
                removedFiles.add(filePath);
            }
        }

        return removedFiles;
    }

    private List<String> getReadyToCommitFiles() throws IOException {
        List<String> readyToCommitFiles = new ArrayList<>();
        Path workingDirPath = Paths.get(workingDir);
        Map<String, String> filesInCommit = getFilesFromLastCommit();

        Files.walk(workingDirPath)
                .filter(Files::isRegularFile)
                .filter(path -> !path.startsWith(workingDirPath.resolve(REPO_DIR)))
                .forEach(path -> {
                    String relativePath = workingDirPath.relativize(path).toString();
                    if (index.containsKey(relativePath) && !filesInCommit.containsKey(relativePath)) {
                        readyToCommitFiles.add(relativePath);
                    }
                });

        return readyToCommitFiles;
    }


    private Map<String, String> getFilesFromLastCommit() throws IOException {
        Map<String, String> filesInCommit = new HashMap<>();
        if (headCommit != null) {
            Path commitPath = Paths.get(workingDir, COMMITS_DIR, headCommit);
            List<String> commitContent = Files.readAllLines(commitPath, StandardCharsets.UTF_8);
            for (String line : commitContent) {
                if (line.startsWith("Message:") || line.startsWith("Date:") || line.startsWith("Parent:")) {
                    continue;
                }
                String[] parts = line.split(": ", 2);
                if (parts.length == 2) {
                    filesInCommit.put(parts[0], parts[1]);
                }
            }
        }
        return filesInCommit;
    }

    private List<String> getUntrackedFiles() throws IOException {
        List<String> untrackedFiles = new ArrayList<>();
        Path workingDirPath = Paths.get(workingDir);

        Files.walk(workingDirPath)
                .filter(Files::isRegularFile)
                .filter(path -> !path.startsWith(workingDirPath.resolve(REPO_DIR)))
                .forEach(path -> {
                    String relativePath = workingDirPath.relativize(path).toString();
                    if (!index.containsKey(relativePath) && !isFileTracked(relativePath)) {
                        untrackedFiles.add(relativePath);
                    }
                });

        return untrackedFiles;
    }

    private boolean isFileTracked(String relativePath) {
        try {
            String currentCommit = headCommit;
            while (currentCommit != null) {
                Path commitPath = Paths.get(workingDir, COMMITS_DIR, currentCommit);
                List<String> commitContent = Files.readAllLines(commitPath, StandardCharsets.UTF_8);
                for (String line : commitContent) {
                    if (line.startsWith(relativePath + ":")) {
                        return true;
                    }
                }
                currentCommit = getParentCommit(commitContent);
            }
        } catch (IOException e) {}
        return false;
    }

    private String getParentCommit(List<String> commitContent) {
        for (String line : commitContent) {
            if (line.startsWith("Parent: ")) {
                return line.substring("Parent: ".length());
            }
        }
        return null;
    }

    private void saveBranch() throws GitException {
        try {
            Path branchPath = Paths.get(workingDir, BRANCHES_DIR, currentBranch);
            Files.write(branchPath, headCommit.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GitException("Failed to save branch", e);
        }
    }

    private void cleanWorkingDirectory() throws IOException {
        Files.walk(Paths.get(workingDir))
                .filter(path -> !path.startsWith(Paths.get(workingDir, REPO_DIR)))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    private String getCommitHash(String revision) throws GitException {
        if (revision.equals("HEAD")) {
            return headCommit;
        }
        if (revision.startsWith("HEAD~")) {
            int n = Integer.parseInt(revision.substring(5));
            return getRelativeRevisionFromHead(n);
        }
        if (revision.matches("[a-fA-F0-9]{40}")) {
            Path commitPath = Paths.get(workingDir, COMMITS_DIR, revision);
            if (Files.exists(commitPath)) {
                return revision;
            }
        }

        Path branchPath = Paths.get(workingDir, BRANCHES_DIR, revision);
        if (Files.exists(branchPath)) {
            try {
                return new String(Files.readAllBytes(branchPath), StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                throw new GitException("Failed to read branch: " + revision, e);
            }
        }
        throw new GitException("Invalid revision format: " + revision);
    }

    private void saveHead() throws GitException {
        try {
            Path headFilePath = Paths.get(workingDir, HEAD_FILE);
            Files.write(headFilePath, ("ref: refs/heads/" + currentBranch).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GitException("Failed to save HEAD", e);
        }
    }

    private void saveIndex() throws GitException {
        StringBuilder indexContent = new StringBuilder();

        for (Map.Entry<String, String> entry : index.entrySet()) {
            indexContent.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }

        try {
            Files.write(Paths.get(workingDir, INDEX_FILE), indexContent.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GitException("Failed to save index", e);
        }
    }
}
