package modules

class build {
    static Map<String,String> getBuildProps(String segmentId, CommonBuildProps, ProjectBuildProps) {
        def commonProps = CommonBuildProps['segments'].find { svc -> svc['id'] == segmentId}
        def projectProps = ProjectBuildProps['segments'].find { svc -> svc['id'] == segmentId}
        projectProps = projectProps.findAll { it.key != 'jenkinsCredentials' }
        return commonProps + projectProps
    }

    void setBuildPropsAsEnvVars(String segmentId, CommonBuildProps, ProjectBuildProps) {
        def props = getBuildProps(segmentId, CommonBuildProps, ProjectBuildProps)
        props.each { key, value -> env."$key" = "$value" }
    }
}

return this