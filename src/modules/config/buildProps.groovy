package modules.config

import groovy.yaml.YamlSlurper

class buildProps {
    def projectBuildProps

    def getConfig() {
        if (this.projectBuildProps == null) {
            projectBuildProps = new YamlSlurper().parse(new File('config/build-props.yaml'))
        }
//        else {
//            throw new Exception("ProjectBuildProps file not found or is empty.")
//        }
        return this.projectBuildProps
    }
}
