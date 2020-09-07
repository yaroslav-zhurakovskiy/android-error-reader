package com.example.myapplication.db

class InMemoryErrorDatabase: ErrorDatabase {
    private val database = mapOf(
        "e0" to ErrorDetails(
            description = "Error 0",
            fixSteps = listOf(
                "Step #1",
                "Step #2",
                "Step #3"
            )
        ),
        "e1" to ErrorDetails(
            description = "Error 'e1' description",
            fixSteps = listOf(
                "Step #1",
                "Step #2",
                "Step #3"
            )
        )
    )

    override fun findErrorDetailsByCode(errorCode: String): ErrorDetails? {
        return database[errorCode]
    }
}
