/**
 * Copyright 2014 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.monitoring.appleveldc;

import org.apache.commons.validator.routines.UrlValidator;

public class Config {

	private static Config _instance = null;
	private UrlValidator validator;
	private String ddaIP = "127.0.0.1";
	private String ddaPort = "8175";
	private String kbIP = "127.0.0.1";
	private String kbPort = "3030";
	private String kbPath = "/modaclouds/kb";
	private String ddaUrl;
	private String kbUrl;
	private int kbSyncPeriod = 10;
	private String appId;
	private boolean startSyncingWithKB = true;

	public static Config getInstance() throws ConfigurationException {
		if (_instance == null)
			_instance = new Config();
		return _instance;
	}

	public static void setInstance(Config config) {
		_instance = config;
	}

	private Config() throws ConfigurationException {
		validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		ddaIP = getOptionalEnvVar(Env.MODACLOUDS_MONITORING_DDA_ENDPOINT_IP, ddaIP);
		ddaPort = getOptionalEnvVar(Env.MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT, ddaPort);
		kbIP = getOptionalEnvVar(Env.MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP,kbIP);
		kbPort = getOptionalEnvVar(Env.MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT,kbPort);
		kbPath = getOptionalEnvVar(Env.MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH,kbPath);
		String kbSyncPeriodString = getOptionalEnvVar(
				Env.MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD,
				Integer.toString(kbSyncPeriod));
		String startSyncingWithKBString = getOptionalEnvVar(
				Env.MODACLOUDS_START_SYNC_WITH_KB,
				Boolean.toString(startSyncingWithKB));
		appId = getMandatoryEnvVar(Env.MODACLOUDS_MONITORED_APP_ID);

		ddaUrl = "http://" + ddaIP + ":" + ddaPort;
		kbUrl = "http://" + kbIP + ":" + kbPort + kbPath;

		if (!validator.isValid(ddaUrl))
			throw new ConfigurationException(ddaUrl + " is not a valid URL");
		if (!validator.isValid(kbUrl))
			throw new ConfigurationException(kbUrl + " is not a valid URL");

		try {
			kbSyncPeriod = Integer.parseInt(kbSyncPeriodString);
			startSyncingWithKB = Boolean.parseBoolean(startSyncingWithKBString);
		} catch (NumberFormatException e) {
			throw new ConfigurationException(kbSyncPeriodString
					+ " is not a valid value for "
					+ Env.MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD);
		}
	}

	public String getDdaUrl() {
		return ddaUrl;
	}

	public String getKbUrl() {
		return kbUrl;
	}

	public int getKbSyncPeriod() {
		return kbSyncPeriod;
	}

	public String getAppId() {
		return appId;
	}

	private String getMandatoryEnvVar(String varName)
			throws ConfigurationException {
		String var = System.getProperty(varName);
		if (var == null) {
			var = System.getenv(varName);
		}
		if (var == null) {
			throw new ConfigurationException(varName
					+ " variable was not defined");
		}
		return var;
	}

	private String getOptionalEnvVar(String varName, String defaultValue) {
		String var = System.getProperty(varName);
		if (var == null) {
			var = System.getenv(varName);
		}
		if (var == null) {
			var = defaultValue;
		}
		return var;
	}

	public boolean isStartSyncingWithKB() {
		return startSyncingWithKB;
	}

	public void setStartSyncingWithKB(boolean startSyncingWithKB) {
		this.startSyncingWithKB = startSyncingWithKB;
	}

}
