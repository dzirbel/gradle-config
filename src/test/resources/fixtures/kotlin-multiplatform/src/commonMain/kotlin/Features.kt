class Greeter(val name: String)

interface Greeting {
    fun message(): String = "hello"
}

context(greeter: Greeter)
fun greeting(): String = "Hello ${greeter.name}"

fun greetAda(): String = greeting(greeter = Greeter("Ada"))

fun numbers(): List<Int> = [1, 2, 3]

fun destructure(): String {
    val (name) = Greeter("Ada")
    return name
}
