@file:Suppress("DEPRECATION")

gradle.addListener(object : TaskExecutionListener {
    override fun beforeExecute(task: Task) {}
    override fun afterExecute(task: Task, state: TaskState) {}
})

tasks.register("verify")
