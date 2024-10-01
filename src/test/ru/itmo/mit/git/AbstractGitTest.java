package ru.itmo.mit.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractGitTest {
    protected enum TestMode {
        TEST_DATA, SYSTEM_OUT
    }

    protected abstract TestMode testMode();
    protected abstract GitCli createCli(String workingDir);

    private static final String DASHES = "----------------------------";

    private PrintStream output;
    private ByteArrayOutputStream byteArrayOutputStream;
    private final File projectDir = new File("./playground/");
    private GitCli cli;

    @BeforeEach
    public void setUp() throws GitException {
        cleanPlayground();
        switch (testMode()) {
            case SYSTEM_OUT:
                output = System.out;
                break;
            case TEST_DATA:
                byteArrayOutputStream = new ByteArrayOutputStream();
                output = new PrintStream(byteArrayOutputStream);
                break;
        }
        cli = createCli(projectDir.getAbsolutePath());
        cli.setOutputStream(output);
        runCommand(GitConstants.INIT);
    }

    private void cleanPlayground() {
        projectDir.mkdirs();
        Collection<File> files = FileUtils.listFilesAndDirs(projectDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            if (!file.equals(projectDir)) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

    private @Nullable String getFileContent(@NotNull String fileName) {
        try {
            return FileUtils.readFileToString(new File(projectDir, fileName), Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    private @Nullable String getResourcesFileContent(@NotNull String fileName) {
        URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
        if (url == null) {
            return null;
        }
        try {
            return FileUtils.readFileToString(new File(url.toURI()), Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    private void runCommand(@NotNull String command, String... args) throws GitException {
        List<String> arguments = Arrays.asList(args);
        String input = (command + " " + String.join(" ", arguments)).trim();
        output.println(DASHES);
        output.println("Command: " + input);

        cli.runCommand(command, arguments);
    }

    private void runRelativeCommand(@NotNull String command, int to) throws GitException {
        output.println(DASHES);
        output.println("Command: " + command + " HEAD~" + to);

        String revision = cli.getRelativeRevisionFromHead(to);
        cli.runCommand(command, Collections.singletonList(revision));
    }

    protected void createFile(@NotNull String fileName, @NotNull String content) throws Exception {
        output.println(DASHES);
        output.println("Create file '" + fileName + "' with content '" + content + "'");
        File file = new File(projectDir, fileName);
        FileUtils.writeStringToFile(file, content, Charset.defaultCharset());
    }

    protected void deleteFile(@NotNull String fileName) {
        output.println(DASHES);
        output.println("Delete file " + fileName);
        File file = new File(projectDir, fileName);
        FileUtils.deleteQuietly(file);
    }

    protected void fileContent(@NotNull String fileName) {
        String content = getFileContent(fileName);
        output.println(DASHES);
        output.println("Command: content of file " + fileName);
        output.println(content);
    }

    protected void status() throws GitException {
        runCommand(GitConstants.STATUS);
    }

    protected void add(String... files) throws GitException {
        runCommand(GitConstants.ADD, files);
    }

    protected void rm(String... files) throws GitException {
        runCommand(GitConstants.RM, files);
    }

    protected void commit(String message) throws GitException {
        runCommand(GitConstants.COMMIT, message);
    }

    protected void reset(int to) throws GitException {
        runRelativeCommand(GitConstants.RESET, to);
    }

    protected void checkoutFiles(String... args) throws GitException {
        runCommand(GitConstants.CHECKOUT, args);
    }

    protected void checkoutRevision(int to) throws GitException {
        runRelativeCommand(GitConstants.CHECKOUT, to);
    }

    protected void checkoutMaster() throws GitException {
        checkoutBranch(GitConstants.MASTER);
    }

    protected void log() throws GitException {
        runCommand(GitConstants.LOG);
    }

    protected void createBranch(@NotNull String branch) throws GitException {
        runCommand(GitConstants.BRANCH_CREATE, branch);
    }

    protected void removeBranch(@NotNull String branch) throws GitException {
        runCommand(GitConstants.BRANCH_REMOVE, branch);
    }

    protected void checkoutBranch(@NotNull String branch) throws GitException {
        runCommand(GitConstants.CHECKOUT, branch);
    }

    protected void showBranches() throws GitException {
        runCommand(GitConstants.SHOW_BRANCHES);
    }

    protected void merge(@NotNull String branch) throws GitException {
        runCommand(GitConstants.MERGE, branch);
    }

    protected void createFileAndCommit(@NotNull String fileName, @NotNull String content) throws Exception {
        createFile(fileName, content);
        add(fileName);
        commit(fileName);
    }

    protected void check(@NotNull String testDataFilePath) {
        if (testMode() == TestMode.SYSTEM_OUT) return;

        String expected = getResourcesFileContent(testDataFilePath);
        if (expected == null) {
            fail(testDataFilePath + " file is missing");
        }
        String actual = byteArrayOutputStream.toString();
        assertEquals(expected, actual);
    }
}
