package com.example.demo.repository

import com.example.demo.data.ParentWithSet
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ParentWithSetRepository : CrudRepository<ParentWithSet, UUID> {
}
