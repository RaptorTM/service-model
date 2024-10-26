package modules

class build {
    Map<String,String> getBuildProps(String segmentId) {
        def commonProps = CommonBuildProps['segments'].find { svc -> svc['id'] == segmentId}
        def projectProps = ProjectBuildProps['segments'].find { svc -> svc['id'] == segmentId}
        projectProps = projectProps.findAll { it.key != 'jenkinsCredentials' }
        return commonProps + projectProps
    }

    void setBuildPropsAsEnvVars(String segmentId) {
        def props = getBuildProps(segmentId)
        props.each { key, value -> env."$key" = "$value" }
    }

}
