import kotlin.contracts.contract

fun String?.isPresent(): Boolean {
    contract { returns(true) implies (this@isPresent != null) }
    return this != null
}
