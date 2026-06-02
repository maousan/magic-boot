package org.ssssssss.magicapi.job.starter;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("magic-api.job")
public class MagicJobConfig {

	private static final String DEFAULT_TABLE_NAME = "magic_job_log";

	private boolean enabled = true;

	private boolean log = false;

	private String tableName = DEFAULT_TABLE_NAME;

	private final Pool pool = new Pool();

	private final Shutdown shutdown = new Shutdown();

	private String threadNamePrefix = "magic-task-";

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Pool getPool() {
		return this.pool;
	}

	public Shutdown getShutdown() {
		return this.shutdown;
	}

	public String getThreadNamePrefix() {
		return this.threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	public static class Pool {

		private int size = Runtime.getRuntime().availableProcessors();

		public int getSize() {
			return this.size;
		}

		public void setSize(int size) {
			this.size = size;
		}

	}

	public static class Shutdown {

		private boolean awaitTermination;

		private Duration awaitTerminationPeriod;

		public boolean isAwaitTermination() {
			return this.awaitTermination;
		}

		public void setAwaitTermination(boolean awaitTermination) {
			this.awaitTermination = awaitTermination;
		}

		public Duration getAwaitTerminationPeriod() {
			return this.awaitTerminationPeriod;
		}

		public void setAwaitTerminationPeriod(Duration awaitTerminationPeriod) {
			this.awaitTerminationPeriod = awaitTerminationPeriod;
		}

	}
}
