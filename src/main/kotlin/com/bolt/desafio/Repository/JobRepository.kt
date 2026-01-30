package com.bolt.desafio.Repository

import com.bolt.desafio.Entity.Job
import org.springframework.data.jpa.repository.JpaRepository

interface JobRepository : JpaRepository<Job, String> {
}