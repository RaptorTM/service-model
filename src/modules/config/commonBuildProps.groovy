package modules.config

import groovy.transform.CompileStatic
import org.yaml.snakeyaml.Yaml

@CompileStatic
class commonBuildProps {
    def commonBuildProps

    def getConfig() {
        if (this.commonBuildProps == null) {
            Yaml yaml = new Yaml();
            commonBuildProps = yaml.load(('config/common-build-props.yaml'as File).text)
        }
//        else {
//            throw new Exception("CommonBuildProps file not found or is empty.")
//        }
        return this.commonBuildProps
    }
}