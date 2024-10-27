package modules

class shellResult {
    String stdOut
    String stdErr
    String allOutput
    Integer exitCode

    // возвращает одновременно стандартные выходы и код возврата
//    shellResult runShell(String script, Boolean exitCodeMustBeZero = true) {
//        def header = """\
//        #!/bin/bash
//        set -eu -o pipefail
//        # перенаправляем вывод для последующих команд
//        # tee будет выводить одновременно в файлы и в консоль
//        # >() называется process substitution
//        exec 1> >(tee -a stdout stdall) 2> >(tee -a stderr stdall)
//    """
//        echo "++ " + script.stripIndent().trim()
//        def fullScript = "${header.stripIndent()}\n${script.stripIndent()}"
//
//        def exitCode = sh(script: fullScript, returnStatus: true)
//
//        def result = new shellResult(
//                stdOut: readFile('stdout'),
//                stdErr: readFile('stderr'),
//                allOutput: readFile('stdall'),
//                exitCode: exitCode,
//        )
//
//        sh('rm stdout stderr stdall')
//
//        if (exitCodeMustBeZero && exitCode != 0) { error "previous ShellResult script's exit code indicates failure" }
//
//        return result
//    }
}
// return this //!!!!!!!!!_____Закоментировать на время отладки в IDE_____!!!!!!!!!

