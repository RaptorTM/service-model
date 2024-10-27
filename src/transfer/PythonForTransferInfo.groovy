package transfer

class PythonForTransferInfo {
    String name // GM.JobsManager
    String srcDir // src/GM.SeismicBackend/GM.JobsManager
    String image // geomate-mapbuilder

    PythonForTransferInfo getPythonForTransferInfo(String name) {
        def service = ServiceModelConfig['services']
                .find { svc -> svc['name'] == name }

        def info = new PythonForTransferInfo(
                name: service['name'],
                image: service['image'],
                srcDir: service['path'],
        )
        return info
    }
}

return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!