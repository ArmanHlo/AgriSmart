package com.shoping.agrismartapp.data.repository

import com.shoping.agrismartapp.data.local.dao.NoteDao
import com.shoping.agrismartapp.data.local.entity.NoteEntity
import com.shoping.agrismartapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    override suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    override suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
