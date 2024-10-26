package modules.smConfig

import groovy.yaml.YamlSlurper

class ServiceModelConfig {
    def serviceModelConfig

    def getServiceModelConfig() {
        if (this.serviceModelConfig != null) {
            return serviceModelConfig
        }
        else {
            serviceModelConfig = new YamlSlurper().parse(new File('config/services-model.yaml'))
//            throw new Exception("ServiceModelConfig file not found or is empty.")
        }
        return this.serviceModelConfig
    }
}