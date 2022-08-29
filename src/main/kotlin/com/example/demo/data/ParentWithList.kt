package com.example.demo.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("parent")
data class ParentWithList(
    @Id
    val pId: UUID?,
    val name: String,

    @MappedCollection(idColumn = "p_id", keyColumn = "c_id")
    val children: List<Child>
)
