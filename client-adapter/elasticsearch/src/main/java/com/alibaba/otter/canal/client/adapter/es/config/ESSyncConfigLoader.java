package com.alibaba.otter.canal.client.adapter.es.config;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.alibaba.otter.canal.client.adapter.support.MappingConfigsLoader;

/**
 * ES 配置装载器
 *
 * @author rewerma 2018-11-01
 * @version 1.0.0
 */
public class ESSyncConfigLoader {

    private static Logger logger = LoggerFactory.getLogger(ESSyncConfigLoader.class);

    @SuppressWarnings("unchecked")
    public static synchronized Map<String, ESSyncConfig> load() {
        logger.info("## Start loading es mapping config ... ");

        Map<String, ESSyncConfig> esSyncConfig = new LinkedHashMap<>();

        Map<String, String> configContentMap = MappingConfigsLoader.loadConfigs("es");
        configContentMap.forEach((fileName, content) -> {
            Map configMap = new Yaml().loadAs(content, Map.class); // yml自带的对象反射不是很稳定
            JSONObject configJson = new JSONObject(configMap);
            ESSyncConfig config = configJson.toJavaObject(ESSyncConfig.class);
            try {
                config.validate();
            } catch (Exception e) {
                throw new RuntimeException("ERROR Config: " + fileName + " " + e.getMessage(), e);
            }
            esSyncConfig.put(fileName, config);
        });

        logger.info("## ES mapping config loaded");
        return esSyncConfig;
    }
}
