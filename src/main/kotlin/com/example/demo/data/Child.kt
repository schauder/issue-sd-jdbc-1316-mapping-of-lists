package com.example.demo.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("child")
data class Child(
    @Id
    val cId: UUID?,
    val name: String
)
