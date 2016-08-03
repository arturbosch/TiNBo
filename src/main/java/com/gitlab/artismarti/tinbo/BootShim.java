package com.gitlab.artismarti.tinbo;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.shell.CommandLine;
import org.springframework.shell.ShellException;
import org.springframework.shell.SimpleShellCommandLineOptions;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.util.StopWatch;

import java.io.IOException;

/**
 * Copied and refactored from "https://github.com/jeffellin/springshellwithboot".
 */
public class BootShim {

	private static final StopWatch sw = new StopWatch("Spring Shell");
	private static CommandLine commandLine;
	private final ConfigurableApplicationContext ctx;

	public BootShim(String[] args, ConfigurableApplicationContext context) {
		this.ctx = context;

		try {
			commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
		} catch (IOException e) {
			throw new ShellException(e.getMessage(), e);
		}

		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) this.ctx);
		scanner.scan("org.springframework.shell.converters", "org.springframework.shell.plugin.support");

	}

	public ExitShellRequest run() {
		sw.start();
		String[] commandsToExecuteAndThenQuit = commandLine.getShellCommandsToExecute();
		JLineShellComponent shell = this.ctx.getBean("shell", JLineShellComponent.class);
		ExitShellRequest exitShellRequest;
		if (null != commandsToExecuteAndThenQuit) {
			boolean successful = false;
			exitShellRequest = ExitShellRequest.FATAL_EXIT;

			for (String cmd : commandsToExecuteAndThenQuit) {
				successful = shell.executeCommand(cmd).isSuccess();
				if (!successful) {
					break;
				}
			}

			if (successful) {
				exitShellRequest = ExitShellRequest.NORMAL_EXIT;
			}
		} else {
			shell.start();
			shell.promptLoop();
			exitShellRequest = shell.getExitShellRequest();
			if (exitShellRequest == null) {
				exitShellRequest = ExitShellRequest.NORMAL_EXIT;
			}

			shell.waitForComplete();
		}

		sw.stop();
		if (shell.isDevelopmentMode()) {
			System.out.println("Total execution time: " + sw.getLastTaskTimeMillis() + " ms");
		}

		return exitShellRequest;
	}

}
