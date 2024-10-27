package modules.config

import org.yaml.snakeyaml.Yaml

class buildProps {
    def projectBuildProps

    def getConfig() {
        if (this.projectBuildProps == null) {
            Yaml yaml = new Yaml()
            projectBuildProps = yaml.load(('C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\test-service-model\\config\\build-props.yaml'as File).text)
        }
//        else {
//            throw new Exception("ProjectBuildProps file not found or is empty.")
//        }
        return this.projectBuildProps
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!