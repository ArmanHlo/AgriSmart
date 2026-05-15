package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun insertNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
}
