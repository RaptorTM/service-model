package modules.smConfig

import groovy.yaml.YamlSlurper

class CommonBuildPropsConfig {
    def commonBuildProps

    def getCommonBuildProps() {
        if (this.commonBuildProps == null) {
            commonBuildProps = new YamlSlurper().parse(new File('config/common-build-props.yaml'))
        }
//        else {
//            throw new Exception("CommonBuildProps file not found or is empty.")
//        }
        return this.commonBuildProps
    }
}