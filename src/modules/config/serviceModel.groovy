package modules.config

import org.yaml.snakeyaml.Yaml

class serviceModel {
    def serviceModelConfig

    def getConfig() {
        if (this.serviceModelConfig != null) {
            return serviceModelConfig
        }
        else {
            Yaml yaml = new Yaml()
            serviceModelConfig = yaml.load(('/Users/ruslan/IdeaProjects/new/service-model/config/services-model.yaml' as File).text)
//            throw new Exception("serviceModel file not found or is empty.")
        }
        return this.serviceModelConfig
    }
}

// return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!