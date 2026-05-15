package com.shoping.agrismartapp.domain.model

data class GovernmentScheme(
    val id: Int,
    val name: String,
    val ministry: String,
    val benefit: String,
    val eligibility: String,
    val applyLink: String,
    val state: String
)
