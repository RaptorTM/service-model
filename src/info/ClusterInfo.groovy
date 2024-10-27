package info

class ClusterInfo {
    String name
    String landscape
    String cloudApiUrl
    String paasId
    String platform

    static ClusterInfo makeClusterInfo(svc) {
        return new ClusterInfo(
                name: svc['name'],
                landscape: svc['landscape'],
                cloudApiUrl: svc['cloudApiUrl'],
                paasId: svc['paasId'],
                platform: svc['platform'] ? svc['platform'] : 'ocp',
        )
    }

    static ClusterInfo getClusterInfoByName(String name, ServiceModelConfig) {
        def svc = ServiceModelConfig['clusters'].find { svc -> svc['name'] == name }
        return makeClusterInfo(svc)
    }

}

return this