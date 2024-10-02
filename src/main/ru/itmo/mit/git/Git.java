package ru.itmo.mit.git;

import java.io.PrintStream;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Git implements GitCli {
    private PrintStream outputStream = System.out;
    private final Repository repository;

    public Git(String workingDir) {
        this.repository = new Repository(workingDir);
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        switch (command) {
            case GitConstants.INIT:
                repository.init();
                outputStream.println("Project initialized");
                break;
            case GitConstants.ADD:
                repository.add(arguments);
                outputStream.println("Add completed successful");
                break;
            case GitConstants.RM:
                repository.rm(arguments);
                outputStream.println("Rm completed successful");
                break;
            case GitConstants.STATUS:
                repository.status(outputStream);
                break;
            case GitConstants.COMMIT:
                if (arguments.isEmpty()) {
                    throw new GitException("Commit message is required");
                }
                repository.commit(arguments.get(0));
                outputStream.println("Files committed");
                break;
            case GitConstants.RESET:
                if (arguments.isEmpty()) {
                    throw new GitException("Revision is required");
                }
                repository.reset(arguments.get(0));
                outputStream.println("Reset successful");
                break;
            case GitConstants.LOG:
                repository.log(outputStream, arguments.isEmpty() ? null : arguments.get(0));
                break;
            case GitConstants.CHECKOUT:
                if (arguments.isEmpty()) {
                    throw new GitException("Revision or file is required");
                }
                if (arguments.get(0).equals("--")) {
                    repository.checkoutFiles(arguments.subList(1, arguments.size()));
                    outputStream.println("Checkout completed successful");
                } else if (arguments.get(0).startsWith("HEAD~")) {
                    int n = Integer.parseInt(arguments.get(0).substring(5));
                    String revision = repository.getRelativeRevisionFromHead(n);
                    repository.checkout(revision);
                    outputStream.println("Checkout completed successful");
                } else {
                    repository.checkout(arguments.get(0));
                    outputStream.println("Checkout completed successful");
                }
                break;
            case GitConstants.BRANCH_CREATE:
                if (arguments.isEmpty()) {
                    throw new GitException("Branch name is required");
                }
                repository.createBranch(arguments.get(0), outputStream);
                break;
            case GitConstants.BRANCH_REMOVE:
                if (arguments.isEmpty()) {
                    throw new GitException("Branch name is required");
                }
                repository.removeBranch(arguments.get(0));
                outputStream.println("Branch " + arguments.get(0) + " removed successfully");
                break;
            case GitConstants.SHOW_BRANCHES:
                repository.showBranches(outputStream);
                break;
            case GitConstants.MERGE:
                if (arguments.isEmpty()) {
                    throw new GitException("Branch name is required");
                }
                repository.merge(arguments.get(0));
                outputStream.println("Files committed");
                break;
            default:
                throw new GitException("Unknown command: " + command);
        }
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return repository.getRelativeRevisionFromHead(n);
    }
}



