package modules.config

import groovy.transform.CompileStatic
import org.yaml.snakeyaml.Yaml

@CompileStatic
class buildProps {
    def projectBuildProps

    def getConfig() {
        if (this.projectBuildProps == null) {
            Yaml yaml = new Yaml();
            projectBuildProps = yaml.load(('config/build-props.yaml'as File).text)
        }
//        else {
//            throw new Exception("ProjectBuildProps file not found or is empty.")
//        }
        return this.projectBuildProps
    }
}

