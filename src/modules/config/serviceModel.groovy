package modules.config

import groovy.yaml.YamlSlurper

class serviceModel {
    def serviceModelConfig

    def getConfig() {
        if (this.serviceModelConfig != null) {
            return serviceModelConfig
        }
        else {
            serviceModelConfig = new YamlSlurper().parse(new File('config/services-model.yaml'))
//            throw new Exception("serviceModel file not found or is empty.")
        }
        return this.serviceModelConfig
    }
}