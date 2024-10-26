package modules.smConfig

import groovy.yaml.YamlSlurper

class ProjectBuildPropsConfig {
    def projectBuildProps

    def getProjectBuildProps() {
        if (this.projectBuildProps == null) {
            projectBuildProps = new YamlSlurper().parse(new File('config/build-props.yaml'))
        }
//        else {
//            throw new Exception("ProjectBuildProps file not found or is empty.")
//        }
        return this.projectBuildProps
    }
}
