@file:Suppress("DEPRECATION")

if (!providers.gradleProperty("compatible").isPresent) {
    gradle.addListener(object : TaskExecutionListener {
        override fun beforeExecute(task: Task) {}
        override fun afterExecute(task: Task, state: TaskState) {}
    })
}

tasks.register("verify")
