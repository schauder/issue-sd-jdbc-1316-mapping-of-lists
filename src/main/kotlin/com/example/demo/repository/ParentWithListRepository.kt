package com.example.demo.repository

import com.example.demo.data.ParentWithList
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParentWithListRepository : CrudRepository<ParentWithList, UUID> {
}
