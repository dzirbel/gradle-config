import org.gradle.api.Plugin
import org.gradle.api.Project

class ExamplePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("example")
    }
}
