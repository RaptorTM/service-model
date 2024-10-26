package modules.config

import groovy.yaml.YamlSlurper

class commonBuildProps {
    def commonBuildProps

    def getConfig() {
        if (this.commonBuildProps == null) {
            commonBuildProps = new YamlSlurper().parse(new File('config/common-build-props.yaml'))
        }
//        else {
//            throw new Exception("CommonBuildProps file not found or is empty.")
//        }
        return this.commonBuildProps
    }
}