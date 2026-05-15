package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun insertNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
}
