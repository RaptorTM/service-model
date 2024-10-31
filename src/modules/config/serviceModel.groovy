package src.modules.config

import org.yaml.snakeyaml.Yaml

class serviceModel {
    static def serviceModelConfig

    static def getConfig() {
        if (serviceModelConfig != null) {
            return serviceModelConfig
        }
        else {
            Yaml yaml = new Yaml()
            serviceModelConfig = yaml.load(('C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\test-service-model\\config\\services-model.yaml' as File).text)
//            throw new Exception("serviceModel file not found or is empty.")
        }
        return serviceModelConfig
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!