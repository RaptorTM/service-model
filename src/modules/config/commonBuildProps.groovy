package modules.config

import org.yaml.snakeyaml.Yaml

class commonBuildProps {
    def commonBuildProps

    def getConfig() {
        if (this.commonBuildProps == null) {
            Yaml yaml = new Yaml()
            commonBuildProps = yaml.load(('/Users/ruslan/IdeaProjects/new/service-model/config/common-build-props.yaml'as File).text)
        }
//        else {
//            throw new Exception("CommonBuildProps file not found or is empty.")
//        }
        return this.commonBuildProps
    }
}

// return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!