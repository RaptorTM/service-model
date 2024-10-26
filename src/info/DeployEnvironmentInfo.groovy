package info

class DeployEnvironmentInfo {
    String name
    String cluster
    String projectId
    String cloudNamespace
    String landscape
    String appUrlOrSuffix
    String configMapVariation
    String configMap
    String storageClassForSeismicData

    static DeployEnvironmentInfo makeDeployEnvironmentInfo(svc) {
        return new DeployEnvironmentInfo(
                name: svc['name'],
                cluster: svc['cluster'],
                projectId: svc['projectId'],
                cloudNamespace: svc['cloudNamespace'],
                landscape: svc['landscape'],
                appUrlOrSuffix: svc['appUrlOrSuffix'],
                configMapVariation: svc['configMapVariation'],
                configMap: svc['configMap'],
                storageClassForSeismicData: svc['storageClassForSeismicData'],
        )
    }

    DeployEnvironmentInfo getDeployEnvironmentInfoByName(String name, ServiceModelConfig) {
        def svc = ServiceModelConfig['environments'].find { svc -> svc['name'] == name }
        return makeDeployEnvironmentInfo(svc)
    }

    List<String> getAllEnvironmentNames(ServiceModelConfig) {
        def envNames = ServiceModelConfig['environments']
                .collect { env -> return env['name'] }
        return envNames
    }
}